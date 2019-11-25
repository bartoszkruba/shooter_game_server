package models.weapons

import models.projectiles.ProjectileType
import settings.PISTOL_BULLETS_IN_CHAMBER
import settings.PISTOL_MAGAZINE_REFILL_TIME
import settings.PISTOL_RELOAD_TIME

class Pistol(
    bulletsInMagazine: Int = PISTOL_BULLETS_IN_CHAMBER
) : Weapon(
    reloadTime = PISTOL_RELOAD_TIME,
    bulletsInChamber = bulletsInMagazine,
    magazineRefillTime = PISTOL_MAGAZINE_REFILL_TIME,
    magazineCapacity = PISTOL_BULLETS_IN_CHAMBER,
    projectileType = ProjectileType.PISTOL
)