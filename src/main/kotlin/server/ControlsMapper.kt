package server

import engine.GameEngine
import engine.Key

class ControlsMapper() {
    companion object {
        fun processKeyPressed(data: dynamic, socket: dynamic, engine: GameEngine) {
            when (data[0]) {
                "W" -> engine.setAgentKeyPressed(socket.id as String, Key.UP)
                "A" -> engine.setAgentKeyPressed(socket.id as String, Key.RIGHT)
                "S" -> engine.setAgentKeyPressed(socket.id as String, Key.DOWN)
                "D" -> engine.setAgentKeyPressed(socket.id as String, Key.RIGHT)
            }
        }
    }
}