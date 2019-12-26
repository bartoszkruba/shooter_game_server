package engine

import models.agents.Agent
import models.obstacles.ExplosiveBarrel
import models.obstacles.Wall
import models.pickups.Pickup
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
    val pickups = ArrayList<Pickup>()
    val explosiveBarrels = ArrayList<ExplosiveBarrel>()

    private val agentEngine = AgentEngine(matrix, agents, this)
    private val projectileEngine = ProjectileEngine(matrix, projectiles, this)
    private val pickupEngine = PickupEngine(matrix, pickups, this)
    private val wallEngine = WallEngine(matrix, walls)
    private val explosionEngine = ExplosionEngine(matrix, this)
    private val barrelEngine = BarrelEngine(matrix, explosiveBarrels, this)

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

        while (continueLooping) {
            currentTime = Date().getTime()
            delta = (currentTime - lastLoop).toFloat() / 1000f

            if (pickupEngine.shouldRespawn()) pickupEngine.respawnPickups()
            if (barrelEngine.shouldRespawn()) barrelEngine.respawnBarrels()

            agentEngine.processAgentActions(delta)
            projectileEngine.processProjectiles(delta)

            lastLoop = currentTime
            delay(1000 / 60)
        }
    }


    fun addAgent(id: String, x: Int, y: Int) = agentEngine.addAgent(id, x, y)
    fun addAgentAtRandomPlace(id: String) = agentEngine.addAgentAtRandomPlace(id)
    fun respawnAgent(id: String) = agentEngine.respawnAgent(id)
    fun removeAgent(id: String) = agentEngine.removeAgent(id)
    fun setAgentKeyPressed(agentId: String, key: Key) = agentEngine.setKeyPressed(agentId, key)
    fun setAgentKeyReleased(agentId: String, key: Key) = agentEngine.setKeyReleased(agentId, key)
    fun setAgentMousePressed(agentId: String) = agentEngine.setMousePressed(agentId)
    fun setAgentMouseReleased(agentId: String) = agentEngine.setMouseReleased(agentId)
    fun setAgentName(agentId: String, newName: String) = agentEngine.setAgentName(agentId, newName)
    fun setAgentRotation(agentId: String, rotation: Float) = agentEngine.setAgentRotation(agentId, rotation)
    fun setAgentPickWeapon(agentId: String, value: Boolean) = agentEngine.setAgentPickWeapon(agentId, value)
    fun addAgentKill(agentId: String) = agentEngine.addAgentKill(agentId)

    fun spawnProjectile(agent: Agent) = projectileEngine.spawnProjectile(agent, dataBroadcaster!!)

    fun spawnPickup(x: Float, y: Float, type: String, ammunition: Int) =
        pickupEngine.spawnPickup(x, y, type, ammunition)

    fun removePickup(pickup: Pickup) = pickupEngine.removePickup(pickup)

    fun spawnExplosion(x: Float, y: Float, agentId: String, type: String) =
        explosionEngine.spawnExplosion(x, y, agentId, type, dataBroadcaster!!)

    fun explodeBarrel(barrel: ExplosiveBarrel, agentId: String) = barrelEngine.explodeBarrel(barrel, agentId)
}