package server

import engine.GameEngine
import engine.Key

class ControlsMapper {
    companion object {
        fun processKeyPressed(data: dynamic, socket: dynamic, engine: GameEngine) {
            when {
                data["W"] != null -> engine.setAgentKeyPressed(socket.id as String, Key.UP)
                data["A"] != null -> engine.setAgentKeyPressed(socket.id as String, Key.LEFT)
                data["S"] != null -> engine.setAgentKeyPressed(socket.id as String, Key.DOWN)
                data["D"] != null -> engine.setAgentKeyPressed(socket.id as String, Key.RIGHT)
            }
        }

        fun processKeyReleased(data: dynamic, socket: dynamic, engine: GameEngine) {
            when {
                data["W"] != null -> engine.setAgentKeyReleased(socket.id as String, Key.UP)
                data["A"] != null -> engine.setAgentKeyReleased(socket.id as String, Key.LEFT)
                data["S"] != null -> engine.setAgentKeyReleased(socket.id as String, Key.DOWN)
                data["D"] != null -> engine.setAgentKeyReleased(socket.id as String, Key.RIGHT)
            }
        }
    }
}