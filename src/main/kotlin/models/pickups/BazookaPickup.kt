package models.pickups

import models.projectiles.ProjectileType
import settings.BAZOOKA_BULLETS_IN_CHAMBER
import settings.BAZOOKA_SPRITE_HEIGHT
import settings.BAZOOKA_SPRITE_WIDTH

class BazookaPickup(
    x: Float,
    y: Float,
    ammunition: Int = BAZOOKA_BULLETS_IN_CHAMBER
) : Pickup(
    x = x,
    y = y,
    width = BAZOOKA_SPRITE_WIDTH,
    height = BAZOOKA_SPRITE_HEIGHT,
    type = ProjectileType.BAZOOKA,
    ammunition = ammunition
)