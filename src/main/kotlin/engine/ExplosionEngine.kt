package engine

import models.explosions.ExplosionType
import server.DataBroadcaster
import settings.BARREL_EXPLOSION_DAMAGE
import settings.BARREL_EXPLOSION_SIZE
import settings.BAZOOKA_EXPLOSION_DAMAGE
import settings.BAZOOKA_EXPLOSION_SIZE
import util.Matter
import util.ZoneUtils

class ExplosionEngine(private val matrix: Matrix, private val engine: GameEngine) {

    fun spawnExplosion(x: Float, y: Float, agentId: String, type: String, dataBroadcaster: DataBroadcaster) {
        val bounds = when (type) {
            ExplosionType.BAZOOKA -> Matter.Bodies.circle(x, y, BAZOOKA_EXPLOSION_SIZE / 2f)
            else -> Matter.Bodies.circle(x, y, BARREL_EXPLOSION_SIZE / 2f)
        }
        val damage = when (type) {
            ExplosionType.BAZOOKA -> BAZOOKA_EXPLOSION_DAMAGE
            else -> BARREL_EXPLOSION_DAMAGE
        }
        val zones = ZoneUtils.getZonesForBounds(bounds)

        val agentIds = ArrayList<String>()
        for (zone in zones) {
            if (matrix.agents[zone] != null) for (agent in matrix.agents[zone]!!) {
                if (Matter.SAT.collides(agent.bounds, bounds).collided as Boolean && !agent.invincible && !agent.dead &&
                    !agentIds.contains(agent.id)
                ) {
                    agent.health -= damage
                }
            }
            if (matrix.explosiveBarrels[zone] != null) for (barrel in matrix.explosiveBarrels[zone]!!) {
                if (Matter.SAT.collides(barrel.bounds, bounds).collided as Boolean) {
                    engine.explodeBarrel(barrel, agentId)
                }
            }
        }

        dataBroadcaster.broadcastNewExplosion(x, y, type)
    }
}