package engine

import models.agents.Agent
import models.obstacles.Wall
import models.projectiles.Projectile
import server.DataBroadcaster
import util.delay
import util.launch
import kotlin.js.Date

class GameEngine {
    val matrix = Matrix()
    val walls = ArrayList<Wall>()
    val agents = ArrayList<Agent>()
    val projectiles = ArrayList<Projectile>()

    private val agentEngine = AgentEngine(matrix, agents, this)
    private val projectileEngine = ProjectileEngine(matrix, projectiles)
    private val wallEngine = WallEngine(matrix, walls)

    var dataBroadcaster: DataBroadcaster? = null

    init {
        wallEngine.generateWalls()
    }

    var continueLooping = false
        private set

    fun start() {
        continueLooping = true
        gameLoop()
    }

    private fun gameLoop() = launch {
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
    }


    fun addAgent(id: String, x: Int, y: Int) = agentEngine.addAgent(id, x, y)
    fun removeAgent(id: String) = agentEngine.removeAgent(id)
    fun setAgentKeyPressed(agentId: String, key: Key) = agentEngine.setKeyPressed(agentId, key)
    fun setAgentKeyReleased(agentId: String, key: Key) = agentEngine.setKeyReleased(agentId, key)
    fun setAgentMousePressed(agentId: String) = agentEngine.setMousePressed(agentId)
    fun setAgentMouseReleased(agentId: String) = agentEngine.setMouseReleased(agentId)
    fun changeAgentName(agentId: String, newName: String) = agentEngine.changeAgentName(agentId, newName)

    fun spawnProjectile(agent: Agent) = projectileEngine.spawnProjectile(agent, dataBroadcaster!!)
}