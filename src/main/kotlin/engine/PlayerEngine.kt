package engine

import models.agents.Player
import models.pickups.Pickup
import models.projectiles.ProjectileType
import models.weapons.Bazooka
import models.weapons.MachineGun
import models.weapons.Pistol
import models.weapons.Shotgun
import server.DataBroadcaster
import settings.*
import util.Matter
import util.ZoneUtils
import util.jsObject
import kotlin.js.Date
import kotlin.random.Random

enum class Key { UP, DOWN, LEFT, RIGHT, RELOAD }

class PlayerEngine(private val matrix: Matrix, private val players: ArrayList<Player>, private val engine: GameEngine) {

    fun processPlayerActions(delta: Float) {
        for (player in players) {
            try {
                processMoveControls(player, delta)
                processWeaponControls(player)

            } catch (ex: Exception) {
                println(ex.message)
            }
        }
    }

    private fun processMoveControls(player: Player, delta: Float) {
        movePlayer(
            player,
            (player.bounds.position.x + player.velocity.x * delta * PLAYER_MOVEMENT_SPEED) as Float,
            (player.bounds.position.y + player.velocity.y * delta * PLAYER_MOVEMENT_SPEED) as Float,
            player.bounds.position.x as Float,
            player.bounds.position.y as Float
        )
    }

    private fun processWeaponControls(player: Player) {
        if (player.reloadPressed && player.weapon.reloadMark == -1.0) {
            if (player.weapon.bulletsInChamber != player.weapon.magazineCapacity) {
                player.weapon.reloadMark = Date().getTime()
                player.weapon.bulletsInChamber = 0
            }
        }

        if (player.weapon.reloadMark != -1.0 && Date().getTime() - player.weapon.reloadMark > player.weapon.magazineRefillTime) {
            player.weapon.reload()
            player.weapon.reloadMark = -1.0
        }

        if (player.pickWeapon) {
            player.pickWeapon = false
            pickWeapon(player)
            return
        }

        if (player.shootPressed && player.weapon.canShoot && !player.dead) {
            player.weapon.shoot()
            engine.spawnProjectile(player)
        }
    }

    private fun pickWeapon(player: Player) {
        var foundPickup: Pickup? = null

        for (zone in player.zones) {
            if (foundPickup != null) break
            if (matrix.pickups[zone] != null) for (pickup in matrix.pickups[zone]!!) {
                if (Matter.SAT.collides(pickup.bounds, player.bounds).collided as Boolean) {
                    foundPickup = pickup
                    break
                }
            }
        }

        if (foundPickup == null) return

        engine.removePickup(foundPickup)
        engine.spawnPickup(
            foundPickup.bounds.position.x,
            foundPickup.bounds.position.y,
            player.weapon.projectileType,
            player.weapon.bulletsInChamber
        )

        player.weapon = when (foundPickup.type) {
            ProjectileType.MACHINE_GUN -> MachineGun(foundPickup.ammunition)
            ProjectileType.PISTOL -> Pistol(foundPickup.ammunition)
            ProjectileType.SHOTGUN -> Shotgun(foundPickup.ammunition)
            ProjectileType.BAZOOKA -> Bazooka(foundPickup.ammunition)
            else -> Pistol(foundPickup.ammunition)
        }
    }

    private fun movePlayer(player: Player, newX: Float, newY: Float, oldX: Float, oldY: Float) {
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

        Matter.Body.setPosition(player.bounds, jsObject {
            x = clampX
            y = clampY
        })

        val oldZones = ArrayList(player.zones)

        player.zones.clear()
        player.zones.addAll(ZoneUtils.getZonesForBounds(player.bounds))

        for (zone in player.zones) {
            if (matrix.walls[zone] != null) for (wall in matrix.walls[zone]!!) {
                if (Matter.SAT.collides(wall.bounds, player.bounds).collided as Boolean) {
                    player.zones.clear()
                    player.zones.addAll(oldZones)
                    Matter.Body.setPosition(player.bounds, jsObject {
                        x = oldX
                        y = oldY
                    })
                    return
                }
            }
            if (matrix.explosiveBarrels[zone] != null) for (barrel in matrix.explosiveBarrels[zone]!!) {
                if (Matter.SAT.collides(barrel.bounds, player.bounds).collided as Boolean) {
                    player.zones.clear()
                    player.zones.addAll(oldZones)
                    Matter.Body.setPosition(player.bounds, jsObject {
                        x = oldX
                        y = oldY
                    })
                    return
                }
            }
        }

        oldZones.filter { !player.zones.contains(it) }.forEach { matrix.players[it]?.remove(player) }
        player.zones.filter { !oldZones.contains(it) }.forEach { zone ->
            matrix.players[zone]?.add(player) ?: run {
                matrix.players[zone] = ArrayList()
                matrix.players[zone]?.add(player)
            }
        }
    }

    fun respawnPlayer(id: String) = players.find { it.id == id }?.let {
        if (!it.dead) return@let

        movePlayerToRandomPlace(it)
        it.lastRespawn = Date().getTime()
        it.weapon = Pistol()
        it.health = PLAYER_BASE_HEALTH
    }

    fun addPlayerAtRandomPlace(id: String) {
        val agent = Player(id = id)
        movePlayerToRandomPlace(agent)
        agent.lastRespawn = Date().getTime()
        players.add(agent)
    }

    private fun movePlayerToRandomPlace(player: Player) {
        val minX = WALL_SPRITE_WIDTH + PLAYER_SPRITE_WIDTH
        val maxX = MAP_WIDTH - WALL_SPRITE_WIDTH - PLAYER_SPRITE_WIDTH
        val minY = WALL_SPRITE_HEIGHT + PLAYER_SPRITE_HEIGHT
        val maxY = MAP_HEIGHT - WALL_SPRITE_HEIGHT - PLAYER_SPRITE_HEIGHT

        while (true) {
            var collided = false

            Matter.Body.setPosition(player.bounds, jsObject {
                x = Random.nextInt(minX, maxX)
                y = Random.nextInt(minY, maxY)
            })

            loop@ for (zone in ZoneUtils.getZonesForBounds(player.bounds)) {
                if (matrix.walls[zone] != null) for (wall in matrix.walls[zone]!!) {
                    if (Matter.SAT.collides(player.bounds, wall.bounds).collided as Boolean) {
                        collided = true
                        break@loop
                    }
                }

                if (matrix.explosiveBarrels[zone] != null) for (barrel in matrix.explosiveBarrels[zone]!!) {
                    if (Matter.SAT.collides(player.bounds, barrel.bounds).collided as Boolean) {
                        collided = true
                        break@loop
                    }
                }
            }

            if (!collided) break
        }

        val oldZones = ArrayList(player.zones)
        player.zones.clear()
        player.zones.addAll(ZoneUtils.getZonesForBounds(player.bounds))

        oldZones.filter { !player.zones.contains(it) }.forEach { matrix.players[it]?.remove(player) }
        for (zone in player.zones) matrix.players[zone]?.add(player) ?: run {
            matrix.players[zone] = ArrayList()
            matrix.players[zone]!!.add(player)
        }
    }

    fun addPlayer(id: String, xPos: Int, yPos: Int) {

        val agent = Player(id = id)

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
        players.add(agent)
        agent.zones.addAll(ZoneUtils.getZonesForBounds(agent.bounds))

        for (zone in agent.zones) matrix.players[zone]?.add(agent) ?: run {
            matrix.players[zone] = ArrayList()
            matrix.players[zone]!!.add(agent)
        }
    }

    fun removePlayer(id: String) = players.find { it.id == id }?.let {
        for (zone in it.zones) matrix.players[zone]?.remove(it)
        players.remove(it)
    }

    fun setKeyPressed(agentId: String, key: Key) = players.find { it.id == agentId }?.let {
        when (key) {
            Key.UP -> it.upPressed = true
            Key.DOWN -> it.downPressed = true
            Key.LEFT -> it.leftPressed = true
            Key.RIGHT -> it.rightPressed = true
            Key.RELOAD -> it.reloadPressed = true
        }
        updatePlayerVelocity(it)
    }

    fun setKeyReleased(agentId: String, key: Key) = players.find { it.id == agentId }?.let {
        when (key) {
            Key.UP -> it.upPressed = false
            Key.DOWN -> it.downPressed = false
            Key.LEFT -> it.leftPressed = false
            Key.RIGHT -> it.rightPressed = false
            Key.RELOAD -> it.reloadPressed = false
        }
        updatePlayerVelocity(it)
    }

    fun setMousePressed(agentId: String) = players.find { it.id == agentId }?.let { it.shootPressed = true }
    fun setMouseReleased(agentId: String) = players.find { it.id == agentId }?.let { it.shootPressed = false }

    private fun updatePlayerVelocity(player: Player) {
        player.velocity.x = 0f
        player.velocity.y = 0f

        var pressedKeys = 0

        if (player.upPressed) pressedKeys++
        if (player.downPressed) pressedKeys++
        if (player.leftPressed) pressedKeys++
        if (player.rightPressed) pressedKeys++

        val velocity = if (pressedKeys > 1) 0.7f else 1f

        if (player.upPressed) player.velocity.y += velocity
        if (player.downPressed) player.velocity.y -= velocity
        if (player.leftPressed) player.velocity.x -= velocity
        if (player.rightPressed) player.velocity.x += velocity
    }

    fun setPlayerName(agentId: String, newName: String) =
        players.find { it.id == agentId }?.let { it.name = newName }

    fun setPlayerRotation(agentId: String, rotation: Float) =
        players.find { it.id == agentId }?.let { it.directionAngle = rotation }

    fun setPlayerPickWeapon(agentId: String, value: Boolean) =
        players.find { it.id == agentId }?.let { it.pickWeapon = value }

    fun incrementPlayerKills(agentId: String, dataBroadcaster: DataBroadcaster) =
        players.find { it.id == agentId }?.let {
            it.kills++;
            dataBroadcaster.broadcastKillConfirm(it.id);
            dataBroadcaster.broadcastScoreBoard()
        }
}