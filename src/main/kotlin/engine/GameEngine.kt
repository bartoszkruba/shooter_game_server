package engine

import models.agents.Agent
import models.obstacles.Wall
import util.delay
import util.launch

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
        launch(block = {
            while (true) {

                delay(1000 / 60)
            }
        })
    }

    fun addAgent(id: String, x: Int, y: Int) = agentEngine.addAgent(id, x, y)
}