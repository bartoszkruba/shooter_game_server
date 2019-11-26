package engine

import models.obstacles.Wall
import util.WorldGenerator
import util.ZoneUtils

class WallEngine(private val matrix: Matrix, private val walls: ArrayList<Wall>) {

    fun generateWalls() {
        WorldGenerator.generateWalls().forEach { wall ->
            walls.add(wall)
            ZoneUtils.getZonesForBounds(wall.bounds).forEach { zone ->
                matrix.walls[zone]?.add(wall) ?: run {
                    matrix.walls[zone] = ArrayList()
                    matrix.walls[zone]?.add(wall)
                }
            }
        }
    }
}