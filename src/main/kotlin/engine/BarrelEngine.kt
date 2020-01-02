package engine

import models.explosions.ExplosionType
import models.obstacles.ExplosiveBarrel
import settings.*
import util.Matter
import util.ZoneUtils
import util.jsObject
import kotlin.js.Date
import kotlin.random.Random

class BarrelEngine(
    private val matrix: Matrix,
    private val explosiveBarrels: ArrayList<ExplosiveBarrel>,
    private val engine: GameEngine
) {
    private var lastRespawn = -0.1

    fun respawnBarrels() {
        if (CLEAR_OLD_BARRELS) clearAllBarrels()
        this.lastRespawn = Date().getTime()

        try {
            repeat(EXPLOSIVE_BARRELS_PER_RESPAWN) { spawnBarrelAtRandomPlace() }
        } catch (ex: Exception) {
            println(ex.message)
        }
    }

    private fun spawnBarrelAtRandomPlace() {
        val minX = WALL_SPRITE_WIDTH + EXPLOSIVE_BARREL_SPRITE_WIDTH
        val maxX = MAP_WIDTH - WALL_SPRITE_WIDTH - EXPLOSIVE_BARREL_SPRITE_WIDTH
        val minY = WALL_SPRITE_WIDTH + EXPLOSIVE_BARREL_SPRITE_HEIGHT
        val maxY = MAP_HEIGHT - WALL_SPRITE_HEIGHT - EXPLOSIVE_BARREL_SPRITE_HEIGHT

        val barrel = ExplosiveBarrel(Random.nextInt(minX, maxX).toFloat(), Random.nextInt(minY, maxY).toFloat())

        var zones = ZoneUtils.getZonesForBounds(barrel.bounds)

        while (true) {
            var collided = false
            loop@ for (zone in zones) {
                if (matrix.walls[zone] != null) for (wall in matrix.walls[zone]!!) {
                    if (Matter.SAT.collides(wall.bounds, barrel.bounds).collided as Boolean) {
                        collided = true
                        break@loop
                    }
                }
                if (matrix.players[zone] != null) for (player in matrix.players[zone]!!) {
                    if (Matter.SAT.collides(player.bounds, barrel.bounds).collided as Boolean) {
                        collided = true
                        break@loop
                    }
                }
                if (matrix.zombies[zone] != null) for (zombie in matrix.zombies[zone]!!) {
                    if (Matter.SAT.collides(zombie.bounds, barrel.bounds).collided as Boolean) {
                        collided = true
                        break@loop
                    }
                }
                if (matrix.explosiveBarrels[zone] != null) for (barr in matrix.explosiveBarrels[zone]!!) {
                    if (Matter.SAT.collides(barr.bounds, barrel.bounds).collided as Boolean) {
                        collided = true
                        break@loop
                    }
                }
            }

            if (!collided) break
            else {
                val newX = Random.nextInt(minX, maxX).toFloat()
                val newY = Random.nextInt(minY, maxY).toFloat()
                Matter.Body.setPosition(barrel.bounds, jsObject { x = newX; y = newY })
                zones = ZoneUtils.getZonesForBounds(barrel.bounds)
            }
        }

        for (zone in zones) matrix.explosiveBarrels[zone]?.add(barrel) ?: run {
            matrix.explosiveBarrels[zone] = ArrayList()
            matrix.explosiveBarrels[zone]!!.add(barrel)
        }
        explosiveBarrels.add(barrel)
    }

    private fun clearAllBarrels() {
        for (barrel in explosiveBarrels) removeBarrel(barrel)
    }

    fun explodeBarrel(barrel: ExplosiveBarrel, agentId: String) {
        removeBarrel(barrel)
        engine.spawnExplosion(
            barrel.bounds.position.x as Float,
            barrel.bounds.position.y as Float,
            agentId,
            ExplosionType.BARREL
        )
    }

    private fun removeBarrel(explosiveBarrel: ExplosiveBarrel) {
        for (zone in ZoneUtils.getZonesForBounds(explosiveBarrel.bounds))
            matrix.explosiveBarrels[zone]?.remove(explosiveBarrel)

        explosiveBarrels.remove(explosiveBarrel)
    }

    fun shouldRespawn() = Date().getTime() - lastRespawn > EXPLOSIVE_BARREL_RESPAWN_RATE * 1000
}