package models.pickups

import models.projectiles.ProjectileType
import settings.SHOTGUN_BULLETS_IN_CHAMBER
import settings.SHOTGUN_SPRITE_HEIGHT
import settings.SHOTGUN_SPRITE_WIDTH

class ShotgunPickup(
    x: Float,
    y: Float,
    ammunition: Int = SHOTGUN_BULLETS_IN_CHAMBER
) : Pickup(
    x = x,
    y = y,
    width = SHOTGUN_SPRITE_WIDTH,
    height = SHOTGUN_SPRITE_HEIGHT,
    type = ProjectileType.SHOTGUN,
    ammunition = ammunition
)