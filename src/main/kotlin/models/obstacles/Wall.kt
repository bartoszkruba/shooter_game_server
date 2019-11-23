package models.obstacles

import models.BaseObject
import settings.WALL_SPRITE_HEIGHT
import settings.WALL_SPRITE_WIDTH
import util.Matter

class Wall(val x: Int, val y: Int) : BaseObject() {
    val bounds = Matter.Bodies.rectangle(x, y, WALL_SPRITE_WIDTH, WALL_SPRITE_HEIGHT)
}