package engine

import models.agents.Player
import models.agents.Zombie
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
    val players = ArrayList<Player>()
    val projectiles = ArrayList<Projectile>()
    val pickups = ArrayList<Pickup>()
    val zombies = ArrayList<Zombie>()
    val explosiveBarrels = ArrayList<ExplosiveBarrel>()

    private val playerEngine = PlayerEngine(matrix, players, this)
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

            playerEngine.processPlayerActions(delta)
            projectileEngine.processProjectiles(delta)

            lastLoop = currentTime
            delay(1000 / 60)
        }
    }


    fun addPlayer(id: String, x: Int, y: Int) = playerEngine.addPlayer(id, x, y)
    fun addPlayerAtRandomPlace(id: String) = playerEngine.addPlayerAtRandomPlace(id)
    fun respawnPlayer(id: String) = playerEngine.respawnPlayer(id)
    fun removePlayer(id: String) = playerEngine.removePlayer(id)
    fun setPlayerKeyPressed(agentId: String, key: Key) = playerEngine.setKeyPressed(agentId, key)
    fun setPlayerKeyReleased(agentId: String, key: Key) = playerEngine.setKeyReleased(agentId, key)
    fun setPlayerMousePressed(agentId: String) = playerEngine.setMousePressed(agentId)
    fun setPlayerMouseReleased(agentId: String) = playerEngine.setMouseReleased(agentId)
    fun setPlayerName(agentId: String, newName: String) = playerEngine.setPlayerName(agentId, newName)
    fun setPlayerRotation(agentId: String, rotation: Float) = playerEngine.setPlayerRotation(agentId, rotation)
    fun setPlayerPickWeapon(agentId: String, value: Boolean) = playerEngine.setPlayerPickWeapon(agentId, value)
    fun incrementPlayerKills(agentId: String) = playerEngine.incrementPlayerKills(agentId, dataBroadcaster!!)

    fun spawnProjectile(player: Player) = projectileEngine.spawnProjectile(player, dataBroadcaster!!)

    fun spawnPickup(x: Float, y: Float, type: String, ammunition: Int) =
        pickupEngine.spawnPickup(x, y, type, ammunition)

    fun removePickup(pickup: Pickup) = pickupEngine.removePickup(pickup)

    fun spawnExplosion(x: Float, y: Float, agentId: String, type: String) =
        explosionEngine.spawnExplosion(x, y, agentId, type, dataBroadcaster!!)

    fun explodeBarrel(barrel: ExplosiveBarrel, agentId: String) = barrelEngine.explodeBarrel(barrel, agentId)

    fun updateScoreboard() = dataBroadcaster!!.broadcastScoreBoard()
}