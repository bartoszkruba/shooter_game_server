package models.agents

import models.physics.Velocity
import models.weapons.Pistol
import models.weapons.Weapon
import settings.INVINCIBILITY_DURATION
import settings.PLAYER_BASE_HEALTH
import settings.PLAYER_SPRITE_HEIGHT
import settings.PLAYER_SPRITE_WIDTH
import util.Matter
import kotlin.js.Date

class Agent(
    var x: Float = 0f,
    var y: Float = 0f,
    var directionAngle: Float = 0f,
    var name: String = "",
    var weapon: Weapon = Pistol(),
    val id: String
) {

    var zones = ArrayList<String>()
    var viewportZones = ArrayList<String>()

    var upPressed = false
    var downPressed = false
    var rightPressed = false
    var leftPressed = false
    var reloadPressed = false
    var shootPressed = false
    var pickWeapon = false


    var health = PLAYER_BASE_HEALTH
        set(value) {
            if (value <= 0) {
                field = 0
                if (!this.dead) this.deaths++
                this.dead = true
            } else {
                this.dead = false
                field = value
            }
        }

    var invincible = true
        private set
        get() {
            return if (lastRespawn < Date().getTime() - INVINCIBILITY_DURATION * 1000) {
                field = false
                field
            } else {
                field = true
                field
            }
        }

    var lastRespawn = 0.0

    var dead = false
        private set

    var kills = 0
    var deaths = 0
        private set

    val bounds = Matter.Bodies.rectangle(x, y, PLAYER_SPRITE_WIDTH, PLAYER_SPRITE_HEIGHT)
    val velocity = Velocity()
}