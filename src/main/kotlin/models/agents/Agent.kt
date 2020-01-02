package models.agents

import models.physics.Velocity
import settings.PLAYER_BASE_HEALTH
import util.Matter

abstract class Agent(
    x: Float,
    y: Float,
    width: Int,
    height: Int,
    health: Int,
    val id: String,
    var directionAngle: Float = 0f
) {
    val zones = ArrayList<String>()
    val velocity = Velocity()
    var deaths = 0
        private set
    var dead = false
        private set
    var health = health
        set(value) {
            println("setting health to $value")
            if (value <= 0) {
                field = 0
                if (!this.dead) this.deaths++
                this.dead = true
            } else {
                this.dead = false
                field = value
            }
        }
//    val bounds = Matter.Bodies.rectangle(x, y, PLAYER_SPRITE_WIDTH, PLAYER_SPRITE_HEIGHT)
    val bounds = Matter.Bodies.rectangle(x, y, width, height)
}