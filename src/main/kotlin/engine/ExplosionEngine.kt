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
        val zombieIds = ArrayList<String>()
        for (zone in zones) {
            if (matrix.players[zone] != null) for (player in matrix.players[zone]!!) {
                if (Matter.SAT.collides(
                        player.bounds,
                        bounds
                    ).collided as Boolean && !player.invincible && !player.dead &&
                    !agentIds.contains(player.id)
                ) {
                    player.health -= damage
                    if (player.dead) {
                        if (player.id != agentId) engine.incrementPlayerKills(agentId)
                        else engine.updateScoreboard()
                    }
                }
            }
            if (matrix.zombies[zone] != null) for (zombie in matrix.zombies[zone]!!) {
                if (Matter.SAT.collides(
                        zombie.bounds,
                        bounds
                    ).collided as Boolean && !zombieIds.contains(zombie.id) && !zombie.dead
                ) {
                    zombie.health -= damage
                    if (zombie.dead) engine.removeZombie(zombie)
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