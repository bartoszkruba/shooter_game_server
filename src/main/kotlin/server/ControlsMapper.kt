package server

import engine.GameEngine
import engine.Key

class ControlsMapper {
    companion object {
        fun processKeyPressed(data: dynamic, agentId: String, engine: GameEngine) {

            when (data["key"]) {
                KeyMappings.UP -> engine.setAgentKeyPressed(agentId, Key.UP)
                KeyMappings.LEFT -> engine.setAgentKeyPressed(agentId, Key.LEFT)
                KeyMappings.DOWN -> engine.setAgentKeyPressed(agentId, Key.DOWN)
                KeyMappings.RIGHT -> engine.setAgentKeyPressed(agentId, Key.RIGHT)
                KeyMappings.RELOAD -> engine.setAgentKeyPressed(agentId, Key.RELOAD)
                KeyMappings.LEFT_MOUSE -> engine.setAgentMousePressed(agentId)
                KeyMappings.PICK_WEAPON -> engine.setAgentPickWeapon(agentId, true)
            }
        }

        fun processKeyReleased(data: dynamic, agentId: String, engine: GameEngine) {

            when (data["key"]) {
                KeyMappings.UP -> engine.setAgentKeyReleased(agentId, Key.UP)
                KeyMappings.LEFT -> engine.setAgentKeyReleased(agentId, Key.LEFT)
                KeyMappings.DOWN -> engine.setAgentKeyReleased(agentId, Key.DOWN)
                KeyMappings.RIGHT -> engine.setAgentKeyReleased(agentId, Key.RIGHT)
                KeyMappings.RELOAD -> engine.setAgentKeyReleased(agentId, Key.RELOAD)
                KeyMappings.LEFT_MOUSE -> engine.setAgentMouseReleased(agentId)
            }
        }

        fun processNameChange(name: String, agentId: String, engine: GameEngine) {
            engine.setAgentName(agentId, name)
            engine.updateScoreboard()
        }

        fun processRotationChange(data: dynamic, agentId: String, engine: GameEngine) {
            engine.setAgentRotation(agentId, data.degrees as Float)
        }

        fun processRespawn(agentId: String, engine: GameEngine) {
            engine.respawnAgent(agentId)
        }
    }
}