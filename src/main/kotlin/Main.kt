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
    server.listen(8080) { println("Server is running on 8080...") }

    io.on("connection") { socket ->
        println("Player connected, ${socket.id}")
        socket.emit("socketID", jsObject { id = socket.id })
    }
}