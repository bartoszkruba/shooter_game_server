import engine.GameEngine
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
        dataUpdater.sendSocketId(socket)
        dataUpdater.sendWallData(socket)
    }

    dataUpdater.agentDataLoop()
}
