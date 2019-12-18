package engine

import models.agents.Agent
import settings.*
import util.Matter
import util.ZoneUtils
import util.jsObject

enum class Key { UP, DOWN, LEFT, RIGHT }

class AgentEngine(private val matrix: Matrix, private val agents: ArrayList<Agent>, private val engine: GameEngine) {

    fun processAgentActions(delta: Float) {
        for (agent in agents) {
            processWeaponControls(agent)
            processMoveControls(agent, delta)
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
        if (agent.shootPressed) println("${agent.id} shooting")
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

        agents.add(agent)
        agent.zones.addAll(ZoneUtils.getZonesForBounds(agent.bounds))

        for (zone in agent.zones) matrix.agents[zone]?.add(agent) ?: run {
            matrix.agents[zone] = ArrayList()
            matrix.agents[zone]!!.add(agent)
        }
    }

    fun setKeyPressed(agentId: String, key: Key) = agents.find { it.id == agentId }?.let {
        when (key) {
            Key.UP -> it.upPressed = true
            Key.DOWN -> it.downPressed = true
            Key.LEFT -> it.leftPressed = true
            Key.RIGHT -> it.rightPressed = true
        }
        updateAgentVelocity(it)
    }

    fun setKeyReleased(agentId: String, key: Key) = agents.find { it.id == agentId }?.let {
        when (key) {
            Key.UP -> it.upPressed = false
            Key.DOWN -> it.downPressed = false
            Key.LEFT -> it.leftPressed = false
            Key.RIGHT -> it.rightPressed = false
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

    fun changeAgentName(agentId: String, newName: String) {
        agents.find { it.id == agentId }?.name = newName
    }

}