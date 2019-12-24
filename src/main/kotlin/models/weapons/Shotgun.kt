package models.weapons

import models.projectiles.ProjectileType
import settings.SHOTGUN_BULLETS_IN_CHAMBER
import settings.SHOTGUN_MAGAZINE_REFILL_TIME
import settings.SHOTGUN_RELOAD_TIME

class Shotgun(
    bulletsInMagazine: Int = SHOTGUN_BULLETS_IN_CHAMBER
) : Weapon(
    reloadTime = SHOTGUN_RELOAD_TIME,
    bulletsInChamber = bulletsInMagazine,
    magazineRefillTime = SHOTGUN_MAGAZINE_REFILL_TIME,
    magazineCapacity = SHOTGUN_BULLETS_IN_CHAMBER,
    projectileType = ProjectileType.SHOTGUN
)