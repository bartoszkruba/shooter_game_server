package engine

import models.agents.Agent
import models.physics.Point
import models.projectiles.MachineGunProjectile
import models.projectiles.PistolProjectile
import models.projectiles.Projectile
import models.projectiles.ProjectileType
import server.DataBroadcaster
import settings.*
import util.Matter
import util.ZoneUtils
import util.jsObject
import kotlin.random.Random

class ProjectileEngine(
    private val matrix: Matrix, private val projectiles: ArrayList<Projectile>,
    private val engine: GameEngine
) {

    fun processProjectiles(delta: Float) {
        try {
            for (projectile in projectiles) moveProjectile(delta, projectile)
        } catch (ex: Exception) {
            println(ex.message)
        }
    }

    private fun moveProjectile(delta: Float, projectile: Projectile) {

        val newPosition = jsObject {
            x = projectile.bounds.position.x + projectile.velocity.x * delta * projectile.speed
            y = projectile.bounds.position.y + projectile.velocity.y * delta * projectile.speed
        }
        Matter.Body.setPosition(projectile.bounds, newPosition)

        if (projectile.bounds.position.x < WALL_SPRITE_WIDTH ||
            projectile.bounds.position.x > MAP_WIDTH - WALL_SPRITE_WIDTH ||
            projectile.bounds.position.y < WALL_SPRITE_HEIGHT ||
            projectile.bounds.y > MAP_HEIGHT - WALL_SPRITE_HEIGHT
        ) return removeProjectile(projectile)

        val oldZones = ArrayList(projectile.zones)

        projectile.zones.clear()
        projectile.zones.addAll(ZoneUtils.getZonesForBounds(projectile.bounds))

        oldZones.filter { !projectile.zones.contains(it) }.forEach { matrix.projectiles[it]?.remove(projectile) }

        projectile.zones.filter { !oldZones.contains(it) }.forEach {
            matrix.projectiles[it]?.add(projectile) ?: run {
                matrix.projectiles[it] = ArrayList()
                matrix.projectiles[it]?.add(projectile)
            }
        }

        if (checkAgentCollisions(projectile)) return removeProjectile(projectile)
        if (checkWallCollisions(projectile)) return removeProjectile(projectile)
    }

    private fun checkAgentCollisions(projectile: Projectile): Boolean {
        for (zone in projectile.zones) {
            if (matrix.agents[zone] != null) for (agent in matrix.agents[zone]!!) {
                if (!agent.dead && !agent.invincible && projectile.agentId != agent.id &&
                    Matter.SAT.collides(agent.bounds, projectile.bounds).collided as Boolean
                ) {
                    agent.health -= projectile.damage.toInt()
                    if (agent.dead) engine.addAgentKill(projectile.agentId)
                    return true
                }
            }
        }
        return false
    }

    private fun checkWallCollisions(projectile: Projectile): Boolean {
        for (zone in projectile.zones) {
            if (matrix.walls[zone] != null) for (wall in matrix.walls[zone]!!) {
                if (Matter.SAT.collides(wall.bounds, projectile.bounds).collided as Boolean) return true
            }
        }
        return false
    }

    fun spawnProjectile(agent: Agent, dataBroadcaster: DataBroadcaster) {
        val xCentre = agent.bounds.position.x as Float
        val yCentre = agent.bounds.position.y as Float

        if (agent.weapon.projectileType == ProjectileType.SHOTGUN) repeat(SHOTGUN_PROJECTILES_FIRED) {
            val edgePoint = projectToPlayerRectEdge(agent.directionAngle)

            edgePoint.x += xCentre - PLAYER_SPRITE_WIDTH / 2
            edgePoint.y += yCentre - PLAYER_SPRITE_HEIGHT / 2
            val angle = agent.directionAngle + Random.nextInt(-SHOTGUN_SPREAD, SHOTGUN_SPREAD)

            spawnProjectile(
                edgePoint.x, edgePoint.y, angle, agent.id, agent.weapon.projectileType,
                dataBroadcaster
            )
        }
        else {
            val edgePoint = projectToPlayerRectEdge(agent.directionAngle)

            edgePoint.x += xCentre - PLAYER_SPRITE_WIDTH / 2
            edgePoint.y += yCentre - PLAYER_SPRITE_HEIGHT / 2

            spawnProjectile(
                edgePoint.x, edgePoint.y, agent.directionAngle, agent.id, agent.weapon.projectileType,
                dataBroadcaster
            )
        }
    }

    private fun spawnProjectile(
        x: Float, y: Float, angle: Float, agentId: String, type: String, dataBroadcaster: DataBroadcaster
    ) {

        val xSpeed = kotlin.math.cos(kotlin.math.PI / 180f * angle).toFloat()
        val ySpeed = kotlin.math.sin((kotlin.math.PI / 180f * angle)).toFloat()

        val projectile = when (type) {
            ProjectileType.PISTOL -> PistolProjectile(x, y, xSpeed, ySpeed, agentId)
            ProjectileType.MACHINE_GUN -> MachineGunProjectile(x, y, xSpeed, ySpeed, agentId)
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
        var theta = angle * kotlin.math.PI / 180

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