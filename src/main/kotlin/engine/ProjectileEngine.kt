package engine

import models.agents.Agent
import models.physics.Point
import models.projectiles.PistolProjectile
import models.projectiles.Projectile
import models.projectiles.ProjectileType
import server.DataBroadcaster
import settings.PLAYER_SPRITE_HEIGHT
import settings.PLAYER_SPRITE_WIDTH
import util.ZoneUtils

class ProjectileEngine(private val matrix: Matrix, private val projectiles: ArrayList<Projectile>) {

    fun spawnProjectile(agent: Agent, dataBroadcaster: DataBroadcaster) {
        val xCentre = agent.bounds.position.x as Float
        val yCentre = agent.bounds.position.y as Float
        val edgePoint = projectToPlayerRectEdge(agent.directionAngle)

        edgePoint.x += xCentre - PLAYER_SPRITE_WIDTH / 2
        edgePoint.y += yCentre - PLAYER_SPRITE_HEIGHT / 2

        spawnProjectile(
            edgePoint.x,
            edgePoint.y,
            agent.directionAngle,
            agent.id,
            agent.weapon.projectileType,
            dataBroadcaster
        )
    }

    private fun spawnProjectile(
        x: Float, y: Float, angle: Float, agentId: String, type: String,
        dataBroadcaster: DataBroadcaster
    ) {

        val xSpeed = kotlin.math.cos(kotlin.math.PI / 180f * angle).toFloat()
        val ySpeed = kotlin.math.sin((kotlin.math.PI / 180f * angle)).toFloat()

        val projectile = when (type) {
            ProjectileType.PISTOL -> PistolProjectile(x, y, xSpeed, ySpeed, agentId)
            else -> PistolProjectile(x, y, xSpeed, ySpeed, agentId)
        }

        addProjectile(projectile)
        dataBroadcaster.broadcastNewProjectile(projectile)
    }

    private fun addProjectile(projectile: Projectile) {
        projectile.zones.addAll(ZoneUtils.getZonesForBounds(projectile.bounds))
        projectiles.add(projectile)
        for (zone in projectile.zones) matrix.projectiles[zone]?.add(projectile) ?: run {
            matrix.projectiles[zone] = ArrayList()
            matrix.projectiles[zone]?.add(projectile)
        }
    }

    private fun removeProjectile(projectile: Projectile) {
        for (zone in projectile.zones) matrix.projectiles[zone]?.remove(projectile)
        projectiles.remove(projectile)
    }

    private fun projectToPlayerRectEdge(angle: Float): Point {
        val twoPI = kotlin.math.PI * 2.0
        var theta = angle / kotlin.math.PI / 180

        while (theta < -kotlin.math.PI) theta += twoPI
        while (theta > kotlin.math.PI) theta -= twoPI

        val rectAtan = kotlin.math.atan2(PLAYER_SPRITE_HEIGHT.toDouble(), PLAYER_SPRITE_WIDTH.toDouble())
        val tanTheta = kotlin.math.tan(theta)

        val region = when {
            theta > -rectAtan && theta <= rectAtan -> 1
            theta > rectAtan && theta <= (kotlin.math.PI - rectAtan) -> 2
            theta > (kotlin.math.PI - rectAtan) || theta <= -(kotlin.math.PI - rectAtan) -> 3
            else -> 4
        }

        val edgePoint = Point(
            x = PLAYER_SPRITE_WIDTH / 2f,
            y = PLAYER_SPRITE_HEIGHT / 2f
        )

        var xFactor = 1
        var yFactor = 1

        if (region == 3 || region == 4) {
            xFactor = -1
            yFactor = -1
        }

        if (region == 1 || region == 3) {
            edgePoint.x += xFactor * (PLAYER_SPRITE_WIDTH / 2f)
            edgePoint.y += yFactor * (PLAYER_SPRITE_WIDTH / 2) * tanTheta.toFloat()
        } else {
            edgePoint.x += xFactor * (PLAYER_SPRITE_HEIGHT / (2 * tanTheta.toFloat()))
            edgePoint.y += yFactor * PLAYER_SPRITE_HEIGHT / 2f
        }

        return edgePoint
    }
}