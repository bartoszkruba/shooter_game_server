package models.projectiles

import models.IdObject
import models.physics.Velocity
import util.Matter

abstract class Projectile(
    x: Float,
    y: Float,
    xSpeed: Float,
    ySpeed: Float,
    radius: Float,
    val type: String,
    var speed: Float,
    val damage: Float,
    val agentId: String
) : IdObject() {
    val bounds = Matter.Bodies.circle(x, y, radius)
    val velocity = Velocity(xSpeed, ySpeed)
    val zones = ArrayList<String>()
}