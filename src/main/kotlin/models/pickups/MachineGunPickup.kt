package models.pickups

import models.projectiles.ProjectileType
import settings.MACHINE_GUN_BULLETS_IN_CHAMBER
import settings.MACHINE_GUN_SPRITE_HEIGHT
import settings.MACHINE_GUN_SPRITE_WIDTH

class MachineGunPickup(
    x: Float,
    y: Float,
    ammunition: Int = MACHINE_GUN_BULLETS_IN_CHAMBER
) : Pickup(
    x = x,
    y = y,
    width = MACHINE_GUN_SPRITE_WIDTH,
    height = MACHINE_GUN_SPRITE_HEIGHT,
    type = ProjectileType.MACHINE_GUN,
    ammunition = ammunition
)