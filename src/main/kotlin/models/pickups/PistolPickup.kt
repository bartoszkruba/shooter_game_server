package models.pickups

import models.projectiles.ProjectileType
import settings.PISTOL_BULLETS_IN_CHAMBER
import settings.PISTOL_SPRITE_HEIGHT
import settings.PISTOL_SPRITE_WIDTH

class PistolPickup(
    x: Float,
    y: Float,
    ammunition: Int = PISTOL_BULLETS_IN_CHAMBER
) : Pickup(
    x = x,
    y = y,
    width = PISTOL_SPRITE_WIDTH,
    height = PISTOL_SPRITE_HEIGHT,
    type = ProjectileType.PISTOL,
    ammunition = ammunition
)