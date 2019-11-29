package engine

import models.agents.Agent
import models.obstacles.Wall
import util.delay
import util.launch
import kotlin.js.Date

class GameEngine {
    val matrix = Matrix()
    val walls = ArrayList<Wall>()
    val agents = ArrayList<Agent>()

    private val agentEngine = AgentEngine(matrix, agents)
    private val wallEngine = WallEngine(matrix, walls)

    init {
        wallEngine.generateWalls()
    }

    var continueLooping = false
        private set

    fun start() {
        continueLooping = true
        gameLoop()
    }

    private fun gameLoop() = launch(block = {
        var lastLoop = Date().getTime()
        var currentTime: Double
        var delta: Float

        while (true) {
            currentTime = Date().getTime()
            delta = (currentTime - lastLoop).toFloat() / 1000f

            agentEngine.processAgentActions(delta)

            lastLoop = currentTime
            delay(1000 / 60)
        }
    })


    fun addAgent(id: String, x: Int, y: Int) = agentEngine.addAgent(id, x, y)
    fun setAgentKeyPressed(agentId: String, key: Key) = agentEngine.setKeyPressed(agentId, key)
    fun setAgentKeyReleased(agentId: String, key: Key) = agentEngine.setKeyReleased(agentId, key)
}