import engine.GameEngine
import server.ControlsMapper
import server.DataUpdater
import settings.AGENT_UPDATES_PER_SECOND
import settings.PICKUP_UPDATES_PER_SECOND
import settings.PROJECTILE_UPDATES_PER_SECOND
import settings.SCOREBOARD_UPDATES_PER_SECOND
import util.*


fun main() {
    val app = Express()
    val server = Http.Server(app)
    val io = SocketIO(server)
    val gameEngine = GameEngine()
    val dataUpdater = DataUpdater(
        walls = gameEngine.walls,
        agents = gameEngine.agents,
        projectiles = gameEngine.projectiles,
        matrix = gameEngine.matrix,
        socketIo = io
    )

    gameEngine.dataBroadcaster = dataUpdater

    server.listen(8080) {
        println("Server is running on 8080...")
        println("Sending player data $AGENT_UPDATES_PER_SECOND times per second")
        println("Sending projectile data $PROJECTILE_UPDATES_PER_SECOND times per second")
        println("Sending pickup data $PICKUP_UPDATES_PER_SECOND times per second")
        println("Sending scoreboard data $SCOREBOARD_UPDATES_PER_SECOND times per second")
    }

    io.on("connection") { socket ->
        println("Player connected, ${socket.id}")

        socket.on("disconnect") {
            println("Player disconnected, ${socket.id}")
            dataUpdater.broadcastScoreBoard()
            gameEngine.removeAgent(socket.id as String)
            dataUpdater.broadcastPlayerDisconnect(socket.id as String)
        }

        configureSocketEvents(socket, gameEngine)
        gameEngine.addAgentAtRandomPlace(socket.id.toString())
        dataUpdater.sendSocketId(socket)
        dataUpdater.sendWallData(socket)
        dataUpdater.broadcastScoreBoard()
    }


    gameEngine.start()
    dataUpdater.agentDataLoop(gameEngine)
    dataUpdater.projectileDataLoop(gameEngine)
    dataUpdater.pickupDataLoop(gameEngine)
    dataUpdater.explosiveBarrelDataLoop(gameEngine)
}

private fun configureSocketEvents(socket: dynamic, gameEngine: GameEngine) {
    socket.on("startKey") { data -> ControlsMapper.processKeyPressed(data, socket.id as String, gameEngine) }
    socket.on("stopKey") { data -> ControlsMapper.processKeyReleased(data, socket.id as String, gameEngine) }
    socket.on("playerName") { data ->
        ControlsMapper.processNameChange(data.name as String, socket.id as String, gameEngine)
    }
    socket.on("mouseStart") { ControlsMapper.processMousePressed(socket.id as String, gameEngine) }
    socket.on("mouseStop") { ControlsMapper.processMouseReleased(socket.id as String, gameEngine) }
    socket.on("playerRotation") { data -> ControlsMapper.processRotationChange(data, socket.id as String, gameEngine) }
    socket.on("pickWeapon") { ControlsMapper.processPickWeaponPressed(socket.id as String, gameEngine) }
    socket.on("restart") { ControlsMapper.processRespawn(socket.id as String, gameEngine) }
}
