package server

import engine.GameEngine
import engine.Key

class ControlsMapper {
    companion object {
        fun processKeyPressed(data: dynamic, agentId: String, engine: GameEngine) {

            when (data["key"]) {
                KeyMappings.UP -> engine.setPlayerKeyPressed(agentId, Key.UP)
                KeyMappings.LEFT -> engine.setPlayerKeyPressed(agentId, Key.LEFT)
                KeyMappings.DOWN -> engine.setPlayerKeyPressed(agentId, Key.DOWN)
                KeyMappings.RIGHT -> engine.setPlayerKeyPressed(agentId, Key.RIGHT)
                KeyMappings.RELOAD -> engine.setPlayerKeyPressed(agentId, Key.RELOAD)
                KeyMappings.LEFT_MOUSE -> engine.setPlayerMousePressed(agentId)
                KeyMappings.PICK_WEAPON -> engine.setPlayerPickWeapon(agentId, true)
            }
        }

        fun processKeyReleased(data: dynamic, agentId: String, engine: GameEngine) {

            when (data["key"]) {
                KeyMappings.UP -> engine.setPlayerKeyReleased(agentId, Key.UP)
                KeyMappings.LEFT -> engine.setPlayerKeyReleased(agentId, Key.LEFT)
                KeyMappings.DOWN -> engine.setPlayerKeyReleased(agentId, Key.DOWN)
                KeyMappings.RIGHT -> engine.setPlayerKeyReleased(agentId, Key.RIGHT)
                KeyMappings.RELOAD -> engine.setPlayerKeyReleased(agentId, Key.RELOAD)
                KeyMappings.LEFT_MOUSE -> engine.setPlayerMouseReleased(agentId)
            }
        }

        fun processNameChange(name: String, agentId: String, engine: GameEngine) {
            engine.setPlayerName(agentId, name)
            engine.updateScoreboard()
        }

        fun processRotationChange(data: dynamic, agentId: String, engine: GameEngine) {
            engine.setPlayerRotation(agentId, data.degrees as Float)
        }

        fun processRespawn(agentId: String, engine: GameEngine) {
            engine.respawnPlayer(agentId)
        }
    }
}