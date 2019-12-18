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
        matrix = gameEngine.matrix,
        socketIo = io
    )

    server.listen(8080) {
        println("Server is running on 8080...")
        println("Sending player data $AGENT_UPDATES_PER_SECOND times per second")
        println("Sending projectile data $PROJECTILE_UPDATES_PER_SECOND times per second")
        println("Sending pickup data $PICKUP_UPDATES_PER_SECOND times per second")
        println("Sending scoreboard data $SCOREBOARD_UPDATES_PER_SECOND times per second")
    }

    io.on("connection") { socket ->
        println("Player connected, ${socket.id}")

        configureSocketEvents(socket, gameEngine)
        gameEngine.addAgent(socket.id.toString(), 500, 500)
        dataUpdater.sendSocketId(socket)
        dataUpdater.sendWallData(socket)
    }
    gameEngine.start()
    dataUpdater.agentDataLoop(gameEngine)
}

private fun configureSocketEvents(socket: dynamic, gameEngine: GameEngine) {
    socket.on("startKey") { data -> ControlsMapper.processKeyPressed(data, socket.id as String, gameEngine) }
    socket.on("stopKey") { data -> ControlsMapper.processKeyReleased(data, socket.id as String, gameEngine) }
    socket.on("playerName") { data ->
        ControlsMapper.processNameChange(data.name as String, socket.id as String, gameEngine)
    }
    socket.on("mouseStart") { ControlsMapper.processMousePressed(socket.id as String, gameEngine) }
    socket.on("mouseStop") { ControlsMapper.processMouseReleased(socket.id as String, gameEngine) }
}
