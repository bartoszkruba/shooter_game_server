package server

import engine.GameEngine
import engine.Matrix
import models.agents.Agent
import models.obstacles.Wall
import settings.*
import util.ZoneUtils
import util.delay
import util.jsObject
import util.launch

class DataUpdater(
    private val walls: ArrayList<Wall>,
    private val agents: ArrayList<Agent>,
    private val matrix: Matrix,
    private val socketIo: dynamic
) {
    fun agentDataLoop(gameEngine: GameEngine) = launch(block = {
        while (gameEngine.continueLooping) {
            for (agent in agents) {
                var minX = agent.bounds.position.x - WINDOW_WIDTH
                var minY = agent.bounds.position.y - WINDOW_HEIGHT
                var maxX = agent.bounds.position.x + WINDOW_WIDTH
                var maxY = agent.bounds.position.y + WINDOW_HEIGHT

                if (minX < 0) {
                    maxX -= minX
                    minX = 0
                } else if (maxX > MAP_WIDTH) {
                    minX -= maxX
                    maxX = MAP_WIDTH
                }

                if (minY < 0) {
                    maxY -= minY
                    minY = 0
                } else if (maxY > MAP_HEIGHT) {
                    minY -= maxY
                    maxY = MAP_WIDTH
                }

                agent.viewportZones = ZoneUtils.getZonesForBounds(
                    minX = minX as Int,
                    maxX = maxX as Int,
                    minY = minY as Int,
                    maxY = maxY as Int
                )

                val agData = ArrayList<dynamic>()
                val ids = ArrayList<String>()

                for (zone in agent.viewportZones) {
                    matrix.agents[zone]?.forEach {
                        if (!ids.contains(it.id)) {
                            ids.add(it.id)
                            agData.add(jsObject {
                                x = agent.bounds.bounds.min.x
                                y = agent.bounds.bounds.min.y
                                name = agent.name
                                xVelocity = agent.velocity.x
                                yVelocity = agent.velocity.y
                                bulletsLeft = if (agent.weapon.reloadMark == -1) agent.weapon.bulletsInChamber else -1
                                isDead = agent.dead
                                currentHealth = agent.health
                                id = agent.id
                                weapon = agent.weapon.projectileType
                                angle = agent.directionAngle
                                inv = agent.invincible
                            })
                        }
                    }
                }


                socketIo.to(agent.id).emit("agentData", jsObject { agentData = agData })
            }
            delay(1000L / AGENT_UPDATES_PER_SECOND)
        }
    })

    fun sendWallData(socket: dynamic) {
        val wallData = ArrayList<dynamic>()
        walls.forEach {
            wallData.add(jsObject {
                x = it.bounds.bounds.min.x
                y = it.bounds.bounds.min.y
            })
        }
        socket.emit("wallData", wallData)
    }

    fun sendSocketId(socket: dynamic) {
        socket.emit("socketID", jsObject { id = socket.id })
    }
}

