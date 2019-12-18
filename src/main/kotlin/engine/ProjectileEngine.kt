package engine

import models.agents.Agent
import models.projectiles.PistolProjectile
import models.projectiles.Projectile
import models.projectiles.ProjectileType
import server.DataBroadcaster
import util.ZoneUtils
import kotlin.js.Math

class ProjectileEngine(private val matrix: Matrix, private val projectiles: ArrayList<Projectile>) {

    fun spawnProjectile(agent: Agent, dataBroadcaster: DataBroadcaster) {
        val xCentre = agent.bounds.position.x as Float
        val yCentre = agent.bounds.position.y as Float
    }

    private fun spawnProjectile(
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

    private fun projectToRectEdge(angle: Float) {
        val twoPI = kotlin.math.PI * 2.0

    }
}