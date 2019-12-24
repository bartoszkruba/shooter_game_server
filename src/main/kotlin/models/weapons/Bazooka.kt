package models.weapons

import models.projectiles.ProjectileType
import settings.BAZOOKA_BULLETS_IN_CHAMBER
import settings.BAZOOKA_MAGAZINE_REFILL_TIME
import settings.BAZOOKA_RELOAD_TIME

class Bazooka(
    bulletsInMagazine: Int = BAZOOKA_BULLETS_IN_CHAMBER
) : Weapon(
    reloadTime = BAZOOKA_RELOAD_TIME,
    bulletsInChamber = bulletsInMagazine,
    magazineRefillTime = BAZOOKA_MAGAZINE_REFILL_TIME,
    magazineCapacity = BAZOOKA_BULLETS_IN_CHAMBER,
    projectileType = ProjectileType.BAZOOKA
)