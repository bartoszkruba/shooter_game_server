package engine

import models.pickups.MachineGunPickup
import models.pickups.Pickup
import models.pickups.PistolPickup
import models.pickups.ShotgunPickup
import models.projectiles.ProjectileType
import settings.*
import util.Matter
import util.ZoneUtils
import util.jsObject
import kotlin.js.Date
import kotlin.random.Random

class PickupEngine(private val matrix: Matrix, private val pickups: ArrayList<Pickup>, engine: GameEngine) {

    private var lastRespawn = -1.0

    fun respawnPickups() {
        println("Respawning weapons...")
        clearAllPickups()
        this.lastRespawn = Date().getTime()

        try {
            repeat(MACHINE_GUNS_ON_MAP) { respawnPickup(ProjectileType.MACHINE_GUN) }
            repeat(PISTOLS_ON_MAP) { respawnPickup(ProjectileType.PISTOL) }
            repeat(SHOTGUNS_ON_MAP) { respawnPickup(ProjectileType.SHOTGUN) }
        } catch (ex: Exception) {
            println(ex.message)
        }
    }

    private fun clearAllPickups() {
        for (pickup in pickups) for (zone in pickup.zones) matrix.pickups[zone]?.remove(pickup)
        pickups.clear()
    }

    private fun respawnPickup(type: String) {
        val minX = WALL_SPRITE_WIDTH + 0.5f * PLAYER_SPRITE_WIDTH
        val maxX = MAP_HEIGHT - WALL_SPRITE_WIDTH - 0.5f * PLAYER_SPRITE_WIDTH
        val minY = WALL_SPRITE_HEIGHT + 0.5f * PLAYER_SPRITE_HEIGHT
        val maxY = MAP_HEIGHT - WALL_SPRITE_HEIGHT - 0.5f * PLAYER_SPRITE_HEIGHT

        val xPos = Random.nextInt(minX.toInt(), maxX.toInt()).toFloat()
        val yPos = Random.nextInt(minY.toInt(), maxY.toInt()).toFloat()

        val pickup = when (type) {
            ProjectileType.PISTOL -> PistolPickup(xPos, yPos)
            ProjectileType.MACHINE_GUN -> MachineGunPickup(xPos, yPos)
            ProjectileType.SHOTGUN -> ShotgunPickup(xPos, yPos)
            else -> PistolPickup(xPos, yPos)
        }

        while (true) {
            var collided = false

            for (zone in ZoneUtils.getZonesForBounds(pickup.bounds)) {
                if (matrix.walls[zone] != null) for (wall in matrix.walls[zone]!!) {
                    if (Matter.SAT.collides(wall.bounds, pickup.bounds).collided as Boolean) {
                        collided = true
                        break
                    }
                }
                // todo check barrels
            }

            if (!collided) {
                break
            } else {
                Matter.Body.setPosition(pickup.bounds, jsObject {
                    x = Random.nextInt(minX.toInt(), maxX.toInt()).toFloat()
                    y = Random.nextInt(minY.toInt(), maxY.toInt()).toFloat()
                })
            }
        }

        pickups.add(pickup)
        pickup.zones.addAll(ZoneUtils.getZonesForBounds(pickup.bounds))
        for (zone in pickup.zones) matrix.pickups[zone]?.add(pickup) ?: run {
            matrix.pickups[zone] = ArrayList()
            matrix.pickups[zone]?.add(pickup)
        }
    }

    fun shouldRespawn() = Date().getTime() - lastRespawn > WEAPON_RESPAWN_RATE * 1000

    fun removePickup(pickup: Pickup) {
        for (zone in pickup.zones) {
            matrix.pickups[zone]?.remove(pickup)
        }
        pickups.remove(pickup)
    }

    fun spawnPickup(x: Float, y: Float, type: String, ammunition: Int) {
        val pickup = when (type) {
            ProjectileType.MACHINE_GUN -> MachineGunPickup(x, y, ammunition)
            ProjectileType.PISTOL -> PistolPickup(x, y, ammunition)
            ProjectileType.SHOTGUN -> ShotgunPickup(x, y, ammunition)
            else -> PistolPickup(x, y, ammunition)
        }
        pickup.zones.addAll(ZoneUtils.getZonesForBounds(pickup.bounds))
        for (zone in pickup.zones) matrix.pickups[zone]?.add(pickup) ?: run {
            matrix.pickups[zone] = ArrayList()
            matrix.pickups[zone]!!.add(pickup)
        }
        pickups.add(pickup)
    }
}