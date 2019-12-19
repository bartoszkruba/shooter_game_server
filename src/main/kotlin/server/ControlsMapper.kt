package server

import engine.GameEngine
import engine.Key

class ControlsMapper {
    companion object {
        fun processKeyPressed(data: dynamic, agentId: String, engine: GameEngine) {
            when {
                data["W"] != null -> engine.setAgentKeyPressed(agentId, Key.UP)
                data["A"] != null -> engine.setAgentKeyPressed(agentId, Key.LEFT)
                data["S"] != null -> engine.setAgentKeyPressed(agentId, Key.DOWN)
                data["D"] != null -> engine.setAgentKeyPressed(agentId, Key.RIGHT)
            }
        }

        fun processKeyReleased(data: dynamic, agentId: String, engine: GameEngine) {
            when {
                data["W"] != null -> engine.setAgentKeyReleased(agentId, Key.UP)
                data["A"] != null -> engine.setAgentKeyReleased(agentId, Key.LEFT)
                data["S"] != null -> engine.setAgentKeyReleased(agentId, Key.DOWN)
                data["D"] != null -> engine.setAgentKeyReleased(agentId, Key.RIGHT)
            }
        }

        fun processMousePressed(agentId: String, engine: GameEngine) =
            engine.setAgentMousePressed(agentId)

        fun processMouseReleased(agentId: String, engine: GameEngine) =
            engine.setAgentMouseReleased(agentId)

        fun processNameChange(name: String, agentId: String, engine: GameEngine) {
            engine.setAgentName(agentId, name)
        }

        fun processRotationChange(data: dynamic, agentId: String, engine: GameEngine) {
            engine.setAgentRotation(agentId, data.degrees as Float)
        }
    }
}