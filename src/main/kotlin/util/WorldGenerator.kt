package util

import settings.*
import kotlin.random.Random

external fun require(module: String): dynamic

val Matter = require("matter-js")

private val minX = WALL_SPRITE_WIDTH
private val maxX = MAP_WIDTH - 2 * WALL_SPRITE_WIDTH
private val minY = WALL_SPRITE_HEIGHT
private val maxY = MAP_HEIGHT - 2 * WALL_SPRITE_HEIGHT

class Wall(val x: Int, val y: Int)

private val occupiedPlaces = ArrayList<dynamic>()

class WorldGenerator {
    companion object {
        fun generateWalls(): ArrayList<Wall> {
            val walls = ArrayList<Wall>()

            repeat(MAP_OBJECTS_COUNT / 3) {
                walls.addAll(generateRandomVerticalWall())
            }

            occupiedPlaces.clear()
            return walls
        }

        private fun generateRandomVerticalWall(): ArrayList<Wall> {
            var x: Int
            var y: Int

            while (true) {
                var free = true
                x = Random.nextInt(minX, maxX)
                y = Random.nextInt(minY, maxY)

                val rect = Matter.Bodies.rectangle(
                    x + WALL_SPRITE_WIDTH / 2,
                    y + ((10 * WALL_SPRITE_HEIGHT) / 2),
                    WALL_SPRITE_WIDTH,
                    10 * WALL_SPRITE_HEIGHT
                )

                occupiedPlaces.forEach { place ->
                    if (Matter.SAT.collides(place, rect).collided as Boolean) free = false
                }

                if (free) break
            }

            return generateVerticalWall(x, y)
        }

        private fun generateVerticalWall(x: Int, y: Int): ArrayList<Wall> {
            val walls = ArrayList<Wall>()

            var pos = y

            repeat(10) {
                walls.add(Wall(x, pos))
                pos += WALL_SPRITE_HEIGHT
            }

            occupiedPlaces.add(
                Matter.Bodies.rectangle(
                    x + WALL_SPRITE_WIDTH / 2,
                    y + ((10 * WALL_SPRITE_HEIGHT) / 2),
                    WALL_SPRITE_WIDTH + 2 * PLAYER_SPRITE_WIDTH,
                    10 * WALL_SPRITE_HEIGHT + 2 * PLAYER_SPRITE_HEIGHT
                )
            )
            return walls
        }
    }
}