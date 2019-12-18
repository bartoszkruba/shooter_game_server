package engine

import models.projectiles.PistolProjectile
import models.projectiles.Projectile
import models.projectiles.ProjectileType
import server.DataBroadcaster
import util.ZoneUtils

class ProjectileEngine(private val matrix: Matrix, private val projectiles: ArrayList<Projectile>) {

    fun spawnProjectile(
        x: Float, y: Float, xSpeed: Float, ySpeed: Float, agentId: String, type: String,
        dataBroadcaster: DataBroadcaster
    ) {
        val projectile = when (type) {
            ProjectileType.PISTOL -> PistolProjectile(x, y, xSpeed, ySpeed, agentId)
            else -> PistolProjectile(x, y, xSpeed, ySpeed, agentId)
        }
        addProjectileToMatrix(projectile)
        dataBroadcaster.broadcastNewProjectile(projectile)
    }

    private fun addProjectileToMatrix(projectile: Projectile) {
        projectile.zones.addAll(ZoneUtils.getZonesForBounds(projectile.bounds))
        projectiles.add(projectile)
        for (zone in projectile.zones) matrix.projectiles[zone]?.add(projectile) ?: run {
            matrix.projectiles[zone] = ArrayList()
            matrix.projectiles[zone]?.add(projectile)
        }
    }
}