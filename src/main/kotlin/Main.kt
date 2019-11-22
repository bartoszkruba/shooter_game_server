import settings.AGENT_UPDATES_PER_SECOND
import settings.PICKUP_UPDATES_PER_SECOND
import settings.PROJECTILE_UPDATES_PER_SECOND
import settings.SCOREBOARD_UPDATES_PER_SECOND
import util.Wall
import util.WorldGenerator

external fun require(module: String): dynamic

inline fun jsObject(init: dynamic.() -> Unit): dynamic {
    val o = js("{}")
    init(o)
    return o
}

fun main() {
    val app = require("express")()
    val server = require("http").Server(app)
    val io = require("socket.io")(server)

    val walls = WorldGenerator.generateWalls()

    server.listen(8080) {
        println("Server is running on 8080...")
        println("Sending player data $AGENT_UPDATES_PER_SECOND times per second")
        println("Sending projectile data $PROJECTILE_UPDATES_PER_SECOND times per second")
        println("Sending pickup data $PICKUP_UPDATES_PER_SECOND times per second")
        println("Sending scoreboard data $SCOREBOARD_UPDATES_PER_SECOND times per second")
    }

    io.on("connection") { socket ->
        println("Player connected, ${socket.id}")
        socket.emit("socketID", jsObject { id = socket.id })
        socket.emit("wallData", walls)
    }

}
