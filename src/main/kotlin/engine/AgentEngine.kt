package engine

import models.agents.Agent
import models.pickups.Pickup
import models.projectiles.ProjectileType
import models.weapons.Bazooka
import models.weapons.MachineGun
import models.weapons.Pistol
import models.weapons.Shotgun
import settings.*
import util.Matter
import util.ZoneUtils
import util.jsObject
import kotlin.js.Date
import kotlin.random.Random

enum class Key { UP, DOWN, LEFT, RIGHT, RELOAD }

class AgentEngine(private val matrix: Matrix, private val agents: ArrayList<Agent>, private val engine: GameEngine) {

    fun processAgentActions(delta: Float) {
        for (agent in agents) {
            try {
                processMoveControls(agent, delta)
                processWeaponControls(agent)

            } catch (ex: Exception) {
                println(ex.message)
            }
        }
    }

    private fun processMoveControls(agent: Agent, delta: Float) {
        moveAgent(
            agent,
            (agent.bounds.position.x + agent.velocity.x * delta * PLAYER_MOVEMENT_SPEED) as Float,
            (agent.bounds.position.y + agent.velocity.y * delta * PLAYER_MOVEMENT_SPEED) as Float,
            agent.bounds.position.x as Float,
            agent.bounds.position.y as Float
        )
    }

    private fun processWeaponControls(agent: Agent) {
        if (agent.reloadPressed && agent.weapon.reloadMark == -1.0) {
            if (agent.weapon.bulletsInChamber != agent.weapon.magazineCapacity) {
                agent.weapon.reloadMark = Date().getTime()
                agent.weapon.bulletsInChamber = 0
            }
        }

        if (agent.weapon.reloadMark != -1.0 && Date().getTime() - agent.weapon.reloadMark > agent.weapon.magazineRefillTime) {
            agent.weapon.reload()
            agent.weapon.reloadMark = -1.0
        }

        if (agent.pickWeapon) {
            agent.pickWeapon = false
            pickWeapon(agent)
            return
        }

        if (agent.shootPressed && agent.weapon.canShoot && !agent.dead) {
            agent.weapon.shoot()
            engine.spawnProjectile(agent)
        }
    }

    private fun pickWeapon(agent: Agent) {
        var foundPickup: Pickup? = null

        for (zone in agent.zones) {
            if (foundPickup != null) break
            if (matrix.pickups[zone] != null) for (pickup in matrix.pickups[zone]!!) {
                if (Matter.SAT.collides(pickup.bounds, agent.bounds).collided as Boolean) {
                    foundPickup = pickup
                    break
                }
            }
        }

        if (foundPickup == null) return

        engine.removePickup(foundPickup)
        engine.spawnPickup(foundPickup.x, foundPickup.y, agent.weapon.projectileType, agent.weapon.bulletsInChamber)

        agent.weapon = when (foundPickup.type) {
            ProjectileType.MACHINE_GUN -> MachineGun(foundPickup.ammunition)
            ProjectileType.PISTOL -> Pistol(foundPickup.ammunition)
            ProjectileType.SHOTGUN -> Shotgun(foundPickup.ammunition)
            ProjectileType.BAZOOKA -> Bazooka(foundPickup.ammunition)
            else -> Pistol(foundPickup.ammunition)
        }
    }

    private fun moveAgent(agent: Agent, newX: Float, newY: Float, oldX: Float, oldY: Float) {
        val clampX = Matter.Common.clamp(
            newX,
            WALL_SPRITE_WIDTH + PLAYER_SPRITE_WIDTH / 2,
            MAP_WIDTH - WALL_SPRITE_WIDTH,
            PLAYER_SPRITE_WIDTH / 2
        ) as Float

        val clampY = Matter.Common.clamp(
            newY,
            WALL_SPRITE_HEIGHT + PLAYER_SPRITE_HEIGHT / 2,
            MAP_HEIGHT - WALL_SPRITE_HEIGHT - PLAYER_SPRITE_HEIGHT / 2
        )

        Matter.Body.setPosition(agent.bounds, jsObject {
            x = clampX
            y = clampY
        })

        val oldZones = agent.zones

        agent.zones = ZoneUtils.getZonesForBounds(agent.bounds)

        for (zone in agent.zones) {
            if (matrix.walls[zone] != null) for (wall in matrix.walls[zone]!!) {
                if (Matter.SAT.collides(wall.bounds, agent.bounds).collided as Boolean) {
                    agent.zones = oldZones
                    Matter.Body.setPosition(agent.bounds, jsObject {
                        x = oldX
                        y = oldY
                    })
                    return
                }
            }
        }

        oldZones.filter { !agent.zones.contains(it) }.forEach { matrix.agents[it]?.remove(agent) }
        agent.zones.filter { !oldZones.contains(it) }.forEach { zone ->
            matrix.agents[zone]?.add(agent) ?: run {
                matrix.agents[zone] = ArrayList()
                matrix.agents[zone]?.add(agent)
            }
        }
    }

    fun respawnAgent(id: String) = agents.find { it.id == id }?.let {
        if (!it.dead) return@let

        moveAgentToRandomPlace(it)
        it.health = PLAYER_BASE_HEALTH
        it.weapon = Pistol()
    }

    fun addAgentAtRandomPlace(id: String) {
        val agent = Agent(id = id)
        moveAgentToRandomPlace(agent)
        agents.add(agent)
    }

    private fun moveAgentToRandomPlace(agent: Agent) {
        val minX = WALL_SPRITE_WIDTH + PLAYER_SPRITE_WIDTH
        val maxX = MAP_WIDTH - WALL_SPRITE_WIDTH - PLAYER_SPRITE_WIDTH
        val minY = WALL_SPRITE_HEIGHT + PLAYER_SPRITE_HEIGHT
        val maxY = MAP_HEIGHT - WALL_SPRITE_HEIGHT - PLAYER_SPRITE_HEIGHT

        while (true) {
            var collided = false

            Matter.Body.setPosition(agent.bounds, jsObject {
                x = Random.nextInt(minX, maxX)
                y = Random.nextInt(minY, maxY)
            })

            for (zone in ZoneUtils.getZonesForBounds(agent.bounds)) {
                if (collided) break
                if (matrix.walls[zone] != null) for (wall in matrix.walls[zone]!!) {
                    if (Matter.SAT.collides(agent.bounds, wall.bounds).collided as Boolean) {
                        collided = true
                        break
                    }
                }
            }

            if (!collided) break
        }

        val oldZones = ArrayList(agent.zones)
        agent.zones.clear()
        agent.zones.addAll(ZoneUtils.getZonesForBounds(agent.bounds))

        oldZones.filter { !agent.zones.contains(it) }.forEach { matrix.agents[it]?.remove(agent) }
        for (zone in agent.zones) matrix.agents[zone]?.add(agent) ?: run {
            matrix.agents[zone] = ArrayList()
            matrix.agents[zone]!!.add(agent)
        }
    }

    fun addAgent(id: String, xPos: Int, yPos: Int) {

        val agent = Agent(id = id)

        val xClamp = Matter.Common.clamp(
            xPos, WALL_SPRITE_WIDTH + PLAYER_SPRITE_WIDTH / 2,
            MAP_WIDTH - WALL_SPRITE_WIDTH - PLAYER_SPRITE_WIDTH / 2
        )

        val yClmap = Matter.Common.clamp(
            yPos, WALL_SPRITE_HEIGHT + PLAYER_SPRITE_HEIGHT / 2,
            MAP_HEIGHT - WALL_SPRITE_HEIGHT - PLAYER_SPRITE_HEIGHT / 2
        )

        Matter.Body.setPosition(agent.bounds, jsObject {
            x = xClamp
            y = yClmap
        })

        agent.lastRespawn = Date().getTime()
        agents.add(agent)
        agent.zones.addAll(ZoneUtils.getZonesForBounds(agent.bounds))

        for (zone in agent.zones) matrix.agents[zone]?.add(agent) ?: run {
            matrix.agents[zone] = ArrayList()
            matrix.agents[zone]!!.add(agent)
        }
    }

    fun removeAgent(id: String) = agents.find { it.id == id }?.let {
        for (zone in it.zones) matrix.agents[zone]?.remove(it)
        agents.remove(it)
    }

    fun setKeyPressed(agentId: String, key: Key) = agents.find { it.id == agentId }?.let {
        when (key) {
            Key.UP -> it.upPressed = true
            Key.DOWN -> it.downPressed = true
            Key.LEFT -> it.leftPressed = true
            Key.RIGHT -> it.rightPressed = true
            Key.RELOAD -> it.reloadPressed = true
        }
        updateAgentVelocity(it)
    }

    fun setKeyReleased(agentId: String, key: Key) = agents.find { it.id == agentId }?.let {
        when (key) {
            Key.UP -> it.upPressed = false
            Key.DOWN -> it.downPressed = false
            Key.LEFT -> it.leftPressed = false
            Key.RIGHT -> it.rightPressed = false
            Key.RELOAD -> it.reloadPressed = false
        }
        updateAgentVelocity(it)
    }

    fun setMousePressed(agentId: String) = agents.find { it.id == agentId }?.let { it.shootPressed = true }
    fun setMouseReleased(agentId: String) = agents.find { it.id == agentId }?.let { it.shootPressed = false }

    private fun updateAgentVelocity(agent: Agent) {
        agent.velocity.x = 0f
        agent.velocity.y = 0f

        var pressedKeys = 0

        if (agent.upPressed) pressedKeys++
        if (agent.downPressed) pressedKeys++
        if (agent.leftPressed) pressedKeys++
        if (agent.rightPressed) pressedKeys++

        val velocity = if (pressedKeys > 1) 0.7f else 1f

        if (agent.upPressed) agent.velocity.y += velocity
        if (agent.downPressed) agent.velocity.y -= velocity
        if (agent.leftPressed) agent.velocity.x -= velocity
        if (agent.rightPressed) agent.velocity.x += velocity
    }

    fun setAgentName(agentId: String, newName: String) {
        agents.find { it.id == agentId }?.name = newName
    }

    fun setAgentRotation(agentId: String, rotation: Float) {
        agents.find { it.id == agentId }?.directionAngle = rotation
    }

    fun setAgentPickWeapon(agentId: String, value: Boolean) {
        agents.find { it.id == agentId }?.pickWeapon = value
    }

    fun addAgentKill(agentId: String) {
        agents.find { it.id == agentId }?.let { it.kills++ }
    }
}