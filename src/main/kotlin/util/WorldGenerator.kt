package util

import models.obstacles.Wall
import settings.*
import kotlin.random.Random

private const val minX = WALL_SPRITE_WIDTH
private const val maxX = MAP_WIDTH - 2 * WALL_SPRITE_WIDTH
private const val minY = WALL_SPRITE_HEIGHT
private const val maxY = MAP_HEIGHT - 2 * WALL_SPRITE_HEIGHT

private val occupiedPlaces = ArrayList<dynamic>()

class WorldGenerator {
    companion object {
        fun generateWalls(): ArrayList<Wall> {
            val walls = ArrayList<Wall>()

            repeat(MAP_OBJECTS_COUNT / 3) {
                walls.addAll(generateRandomVerticalWall())
                walls.addAll(generateRandomHorizontalWall())
                walls.addAll(generateRandomHouse())
            }

            occupiedPlaces.clear()
            return walls
        }
    }
}

private fun generateRandomVerticalWall(): ArrayList<Wall> {
    var x: Int
    var y: Int

    while (true) {
        var free = true
        x = Random.nextInt(minX, maxX)
        y = Random.nextInt(
            minY + PLAYER_SPRITE_HEIGHT,
            maxY - 10 * WALL_SPRITE_HEIGHT - PLAYER_SPRITE_HEIGHT
        )

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

private fun generateRandomHorizontalWall(): ArrayList<Wall> {
    var x: Int
    var y: Int

    while (true) {
        var free = true
        x = Random.nextInt(
            minX + PLAYER_SPRITE_WIDTH,
            maxX - 10 * WALL_SPRITE_WIDTH - PLAYER_SPRITE_WIDTH
        )
        y = Random.nextInt(minY, maxY)

        val rect = Matter.Bodies.rectangle(
            x + ((10 * WALL_SPRITE_WIDTH) / 2),
            y + WALL_SPRITE_HEIGHT / 2,
            10 * WALL_SPRITE_WIDTH,
            WALL_SPRITE_HEIGHT
        )

        occupiedPlaces.forEach { place ->
            if (Matter.SAT.collides(place, rect).collided as Boolean) free = false
        }

        if (free) break
    }

    return generateHorizontalWall(x, y)
}

private fun generateHorizontalWall(x: Int, y: Int): ArrayList<Wall> {
    val walls = ArrayList<Wall>()

    var pos = x

    repeat(10) {
        walls.add(Wall(pos, y))
        pos += WALL_SPRITE_WIDTH
    }

    occupiedPlaces.add(
        Matter.Bodies.rectangle(
            x + ((10 * WALL_SPRITE_WIDTH) / 2),
            y + WALL_SPRITE_HEIGHT / 2,
            10 * WALL_SPRITE_WIDTH + 2 * PLAYER_SPRITE_WIDTH,
            WALL_SPRITE_HEIGHT + 2 * PLAYER_SPRITE_HEIGHT
        )
    )

    return walls
}

private fun generateRandomHouse(): ArrayList<Wall> {
    val minX = WALL_SPRITE_WIDTH + PLAYER_SPRITE_WIDTH
    val maxX = MAP_WIDTH - 12 * WALL_SPRITE_WIDTH - PLAYER_SPRITE_WIDTH

    val minY = WALL_SPRITE_HEIGHT + PLAYER_SPRITE_HEIGHT
    val maxY = MAP_HEIGHT - 12 * WALL_SPRITE_HEIGHT - PLAYER_SPRITE_HEIGHT

    var x: Int
    var y: Int

    while (true) {
        var free = true
        x = Random.nextInt(minX, maxX)
        y = Random.nextInt(minY, maxY)

        val rect = Matter.Bodies.rectangle(
            x + ((10 * WALL_SPRITE_WIDTH) / 2),
            y + ((11 * WALL_SPRITE_HEIGHT) / 2),
            10 * WALL_SPRITE_WIDTH,
            11 * WALL_SPRITE_HEIGHT
        )

        for (place in occupiedPlaces) {
            if (Matter.SAT.collides(place, rect).collided as Boolean) {
                free = false
                break
            }
        }

        if (free) break
    }

    return when (Random.nextInt(0, 4)) {
        0 -> generateHouseFacingDown(x, y)
        1 -> generateHouseFacingUp(x, y)
        2 -> generateHouseFacingRight(x, y)
        else -> generateHouseFacingLeft(x, y)
    }
}

private fun generateHouseFacingDown(x: Int, y: Int): ArrayList<Wall> {
    val walls = ArrayList<Wall>()

    walls.addAll(generateVerticalWall(x, y))
    walls.addAll(generateVerticalWall(x + 9 * WALL_SPRITE_WIDTH, y))
    walls.addAll(generateHorizontalWall(x, y + 9 * WALL_SPRITE_HEIGHT))

    for (i in 1..2) walls.add(Wall(x + i * WALL_SPRITE_WIDTH, y))

    for (i in 1..3) walls.add(Wall(x + (10 * WALL_SPRITE_WIDTH) - i * WALL_SPRITE_WIDTH, y))

    occupiedPlaces.add(
        Matter.Bodies.rectangle(
            x + ((10 * WALL_SPRITE_WIDTH) / 2),
            y + ((11 * WALL_SPRITE_HEIGHT) / 2),
            10 * WALL_SPRITE_WIDTH + 2 * PLAYER_SPRITE_WIDTH,
            11 * WALL_SPRITE_HEIGHT + 2 * PLAYER_SPRITE_HEIGHT
        )
    )

    return walls
}

private fun generateHouseFacingUp(x: Int, y: Int): ArrayList<Wall> {
    val walls = ArrayList<Wall>()

    walls.addAll(generateVerticalWall(x, y))
    walls.addAll(generateVerticalWall(x + 9 * WALL_SPRITE_WIDTH, y))
    walls.addAll(generateHorizontalWall(x, y))

    for (i in 1..2) walls.add(Wall(x + i * WALL_SPRITE_WIDTH, y + 9 * WALL_SPRITE_HEIGHT))

    for (i in 1..3) walls.add(Wall(x + (10 * WALL_SPRITE_WIDTH) - i * WALL_SPRITE_WIDTH, y + 9 * WALL_SPRITE_HEIGHT))

    occupiedPlaces.add(
        Matter.Bodies.rectangle(
            x + ((10 * WALL_SPRITE_WIDTH) / 2),
            y + ((11 * WALL_SPRITE_HEIGHT) / 2),
            10 * WALL_SPRITE_WIDTH + 2 * PLAYER_SPRITE_WIDTH,
            11 * WALL_SPRITE_HEIGHT + 2 * PLAYER_SPRITE_HEIGHT
        )
    )

    return walls
}

private fun generateHouseFacingRight(x: Int, y: Int): ArrayList<Wall> {
    val walls = ArrayList<Wall>()

    walls.addAll(generateVerticalWall(x, y))
    walls.addAll(generateHorizontalWall(x, y + 9 * WALL_SPRITE_HEIGHT))
    walls.addAll(generateHorizontalWall(x, y))

    for (i in 1..2) walls.add(Wall(x + 9 * WALL_SPRITE_WIDTH, y + i * WALL_SPRITE_HEIGHT))

    for (i in 1..3) walls.add(Wall(x + 9 * WALL_SPRITE_WIDTH, y + (10 * WALL_SPRITE_WIDTH) - i * WALL_SPRITE_HEIGHT))

    occupiedPlaces.add(
        Matter.Bodies.rectangle(
            x + ((10 * WALL_SPRITE_WIDTH) / 2),
            y + ((11 * WALL_SPRITE_HEIGHT) / 2),
            10 * WALL_SPRITE_WIDTH + 2 * PLAYER_SPRITE_WIDTH,
            11 * WALL_SPRITE_HEIGHT + 2 * PLAYER_SPRITE_HEIGHT
        )
    )

    return walls
}

private fun generateHouseFacingLeft(x: Int, y: Int): ArrayList<Wall> {
    val walls = ArrayList<Wall>()

    walls.addAll(generateVerticalWall(x + 9 * WALL_SPRITE_WIDTH, y))
    walls.addAll(generateHorizontalWall(x, y + 9 * WALL_SPRITE_HEIGHT))
    walls.addAll(generateHorizontalWall(x, y))

    for (i in 1..2) walls.add(Wall(x, y + i * WALL_SPRITE_HEIGHT))

    for (i in 1..3) walls.add(Wall(x, y + (10 * WALL_SPRITE_HEIGHT) - i * WALL_SPRITE_HEIGHT))

    occupiedPlaces.add(
        Matter.Bodies.rectangle(
            x + ((10 * WALL_SPRITE_WIDTH) / 2),
            y + ((11 * WALL_SPRITE_HEIGHT) / 2),
            10 * WALL_SPRITE_WIDTH + 2 * PLAYER_SPRITE_WIDTH,
            11 * WALL_SPRITE_HEIGHT + 2 * PLAYER_SPRITE_HEIGHT
        )
    )

    return walls
}
