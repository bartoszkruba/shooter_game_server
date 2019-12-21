package models.weapons

import models.projectiles.ProjectileType
import settings.*

class MachineGun(
    bulletsInMagazine: Int = MACHINE_GUN_BULLETS_IN_CHAMBER
) : Weapon(
    reloadTime = MACHINE_GUN_RELOAD_TIME,
    bulletsInChamber = bulletsInMagazine,
    magazineRefillTime = MACHINE_GUN_MAGAZINE_REFILL_TIME,
    magazineCapacity = MACHINE_GUN_BULLETS_IN_CHAMBER,
    projectileType = ProjectileType.MACHINE_GUN
)