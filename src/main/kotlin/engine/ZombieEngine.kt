package engine

import models.agents.Zombie
import settings.*
import util.Matter
import util.ZoneUtils
import util.jsObject
import kotlin.js.Date
import kotlin.random.Random

class ZombieEngine(
    private val matrix: Matrix,
    private val zombies: ArrayList<Zombie>,
    private val gameEngine: GameEngine
) {
    private var lastRespawn = 0.0

    fun processZombieActions(delta: Float) {

    }

    fun shouldRespawn(): Boolean {
        return Date().getTime() - lastRespawn > ZOMBIE_RESPAWN_RATE * 1000
    }

    fun respawnZombies() {
        lastRespawn = Date().getTime()
        try {
            repeat(ZOMBIES_PER_RESPAWN) { spawnZombie() }
        } catch (ex: Exception) {
            println(ex.message)
        }
    }

    private fun spawnZombie() {
        val minX = WALL_SPRITE_WIDTH + ZOMBIE_SPRITE_WIDTH
        val maxX = MAP_WIDTH - WALL_SPRITE_WIDTH - ZOMBIE_SPRITE_WIDTH
        val minY = WALL_SPRITE_HEIGHT + ZOMBIE_SPRITE_HEIGHT
        val maxY = MAP_HEIGHT - WALL_SPRITE_HEIGHT - ZOMBIE_SPRITE_HEIGHT

        val zombie = Zombie(Random.nextInt(minX, maxX).toFloat(), Random.nextInt(minY, maxY).toFloat(), 0f)

        while (true) {
            var collided = false
            zombie.zones.clear()
            zombie.zones.addAll(ZoneUtils.getZonesForBounds(zombie.bounds))

            loop@ for (zone in zombie.zones) {
                if (matrix.walls[zone] != null) for (wall in matrix.walls[zone]!!) {
                    if (Matter.SAT.collides(zombie.bounds, wall.bounds).collided as Boolean) {
                        collided = true
                        break@loop
                    }
                }
                if (matrix.explosiveBarrels[zone] != null) for (barrel in matrix.explosiveBarrels[zone]!!) {
                    if (Matter.SAT.collides(zombie.bounds, barrel.bounds).collided as Boolean) {
                        collided = true
                        break@loop
                    }
                }
                if (matrix.players[zone] != null) for (player in matrix.players[zone]!!) {
                    if (Matter.SAT.collides(zombie.bounds, player.bounds).collided as Boolean) {
                        collided = true
                        break@loop
                    }
                }
                if (matrix.zombies[zone] != null) for (zomb in matrix.zombies[zone]!!) {
                    if (Matter.SAT.collides(zombie.bounds, zomb.bounds).colldied as Boolean) {
                        collided = true
                        break@loop
                    }
                }
            }

            if (!collided) break
            else Matter.Body.setPosition(zombie.bounds, jsObject {
                x = Random.nextInt(minX, maxX).toFloat()
                y = Random.nextInt(minY, maxY).toFloat()
            })
        }

        for (zone in zombie.zones) {
            matrix.zombies[zone]?.add(zombie) ?: run {
                matrix.zombies[zone] = ArrayList()
                matrix.zombies[zone]!!.add(zombie)
            }
        }
        zombies.add(zombie)
    }
}