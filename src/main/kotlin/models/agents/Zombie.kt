package models.agents

import settings.ZOMBIE_BASE_HEALTH
import settings.ZOMBIE_SIGHT_RADIUS
import settings.ZOMBIE_SPRITE_HEIGHT
import settings.ZOMBIE_SPRITE_WIDTH
import util.Matter
import util.ShortId
import util.jsObject

class Zombie(
    x: Float,
    y: Float,
    directionAngle: Float
) : Agent(
    x = x,
    y = y,
    directionAngle = directionAngle,
    id = ShortId.generate() as String,
    width = ZOMBIE_SPRITE_WIDTH,
    height = ZOMBIE_SPRITE_HEIGHT,
    health = ZOMBIE_BASE_HEALTH
) {
    val sight = Matter.Bodies.circle(x, y, ZOMBIE_SIGHT_RADIUS)

    init {
        setPosition(x, y)
    }

    fun setPosition(xPos: Float, yPos: Float) {
        Matter.Body.setPosition(this.bounds, jsObject {
            x = xPos
            y = yPos
        })
        Matter.Body.setPosition(this.sight, jsObject {
            x = xPos
            y = yPos
        })
    }
}