external fun require(module: String): dynamic

fun main() {
    val express = require("express")
    val app = express()

    app.listen(3000) { println("Listening on port 3000") }

    app.get("/") { _, res, _ ->
        res.send("i am beautiful butterfly")
    }
}