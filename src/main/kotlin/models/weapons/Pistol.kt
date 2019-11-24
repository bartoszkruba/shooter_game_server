package models.weapons

import models.projectiles.ProjectileType
import settings.PISTOL_BULLETS_IN_CHAMBER
import settings.PISTOL_MAGAZINE_REFILL_TIME
import settings.PISTOL_RELOAD_TIME

class Pistol(bulletsInMagazine: Int = PISTOL_BULLETS_IN_CHAMBER) : Weapon(
    PISTOL_RELOAD_TIME,
    bulletsInMagazine,
    PISTOL_MAGAZINE_REFILL_TIME,
    PISTOL_BULLETS_IN_CHAMBER,
    ProjectileType.PISTOL
)