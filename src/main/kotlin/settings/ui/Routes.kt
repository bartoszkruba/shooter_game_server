package settings.ui

fun configureRoutes(app: dynamic) {
    app.set("view engine", "ejs")
    app.get("/") { _, res, _ -> res.render("index") }
}