package engine

import models.agents.Agent
import settings.*
import util.Matter
import util.ZoneUtils
import util.jsObject

class AgentEngine(val matrix: Matrix, val agents: ArrayList<Agent>) {

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

        for (zone in agent.zones) matrix.agents[zone]?.add(agent)
    }

}