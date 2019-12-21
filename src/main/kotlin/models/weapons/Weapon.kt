package models.weapons

import kotlin.js.Date

abstract class Weapon(
    val reloadTime: Int,
    var bulletsInChamber: Int,
    val magazineRefillTime: Int,
    val magazineCapacity: Int,
    val projectileType: String
) {
    var reloadMark = -1.0
    private var lastShoot = 0.0

    var canShoot: Boolean = false
        private set
        get() {
            return Date().getTime() - lastShoot > reloadTime && bulletsInChamber > 0
        }

    fun shoot(): Boolean {
        return if (canShoot) {
            this.lastShoot = Date().getTime()
            bulletsInChamber--
            true
        } else {
            false
        }
    }

    fun reload() {
        this.bulletsInChamber = magazineCapacity
    }
}