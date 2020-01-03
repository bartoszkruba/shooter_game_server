package engine

import models.agents.Player
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


    fun moveZombies(delta: Float) {
        for (zombie in zombies) moveZombie(zombie, delta)
    }

    private fun moveZombie(zombie: Zombie, delta: Float) {
        zombie.setPosition(
            (zombie.bounds.position.x + delta * zombie.velocity.x * ZOMBIE_MOVEMENT_SPEED) as Float,
            (zombie.bounds.position.y + delta * zombie.velocity.y * ZOMBIE_MOVEMENT_SPEED) as Float
        )
    }

    fun processZombieActions() {
        for (zombie in zombies) controlZombie(zombie)
    }

    private fun controlZombie(zombie: Zombie) {
        val spottedPlayer = findPlayerInSight(zombie.sight) ?: return

        val deltaX = (zombie.bounds.position.x - spottedPlayer.bounds.position.x) as Float
        val deltaY = (zombie.bounds.position.y - spottedPlayer.bounds.position.y) as Float
        val angle = kotlin.math.atan2(deltaY, deltaX)

        zombie.velocity.x = -kotlin.math.cos(angle)
        zombie.velocity.y = -kotlin.math.sin(angle)
    }

    private fun findPlayerInSight(sight: dynamic): Player? {
        for (zone in ZoneUtils.getZonesForBounds(sight)) {
            if (matrix.players[zone] != null) for (player in matrix.players[zone]!!) {
                return player
            }
        }
        return null
    }

    fun shouldRespawn(): Boolean {
        return Date().getTime() - lastRespawn > ZOMBIE_RESPAWN_RATE * 1000
    }

    fun respawnZombies() {
        lastRespawn = Date().getTime()
        try {
            repeat(ZOMBIES_PER_RESPAWN) { spawnZombie() }
        } catch (ex: Exception) {
            println("respawn")
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
                    if (Matter.SAT.collides(zombie.bounds, zomb.bounds).collided as Boolean) {
                        collided = true
                        break@loop
                    }
                }
            }

            if (!collided) break
            else zombie.setPosition(Random.nextInt(minX, maxX).toFloat(), Random.nextInt(minY, maxY).toFloat())
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