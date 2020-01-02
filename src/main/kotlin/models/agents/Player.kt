package models.agents

import models.weapons.Pistol
import models.weapons.Weapon
import settings.INVINCIBILITY_DURATION
import settings.PLAYER_BASE_HEALTH
import settings.PLAYER_SPRITE_HEIGHT
import settings.PLAYER_SPRITE_WIDTH
import kotlin.js.Date

class Player(
    x: Float = 0f,
    y: Float = 0f,
    directionAngle: Float = 0f,
    var name: String = "",
    var weapon: Weapon = Pistol(),
    id: String
) : Agent(
    x = x,
    y = y,
    directionAngle = directionAngle,
    id = id,
    height = PLAYER_SPRITE_HEIGHT,
    width = PLAYER_SPRITE_WIDTH,
    health = PLAYER_BASE_HEALTH
) {
    var viewportZones = ArrayList<String>()

    var upPressed = false
    var downPressed = false
    var rightPressed = false
    var leftPressed = false
    var reloadPressed = false
    var shootPressed = false
    var pickWeapon = false

    var invincible = true
        private set
        get() {
            return if (lastRespawn + INVINCIBILITY_DURATION * 1000 > Date().getTime()) {
                field = true
                field
            } else {
                field = false
                field
            }
        }

    var lastRespawn = 0.0

    var kills = 0
}