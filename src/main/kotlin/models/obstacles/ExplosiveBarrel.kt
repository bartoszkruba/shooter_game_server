package models.obstacles

import models.IdObject
import settings.EXPLOSIVE_BARREL_SPRITE_HEIGHT
import settings.EXPLOSIVE_BARREL_SPRITE_WIDTH
import util.Matter

class ExplosiveBarrel(
    x: Float,
    y: Float
) : IdObject() {
    val bounds = Matter.Bodies.rectangle(x, y, EXPLOSIVE_BARREL_SPRITE_WIDTH, EXPLOSIVE_BARREL_SPRITE_HEIGHT)
}