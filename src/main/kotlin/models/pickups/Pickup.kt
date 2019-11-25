package models.pickups

import models.IdObject
import util.Matter

abstract class Pickup(
    x: Float,
    y: Float,
    width: Int,
    height: Int,
    val type: String,
    var ammunition: Int
) : IdObject() {
    val bounds = Matter.Bodies.rectangle(x, y, width, height)
}