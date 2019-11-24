package models.obstacles

import models.idObject
import settings.WALL_SPRITE_HEIGHT
import settings.WALL_SPRITE_WIDTH
import util.Matter

class Wall(val x: Int, val y: Int) : idObject() {
    val bounds = Matter.Bodies.rectangle(x, y, WALL_SPRITE_WIDTH, WALL_SPRITE_HEIGHT)
    val zones = ArrayList<String>()
}