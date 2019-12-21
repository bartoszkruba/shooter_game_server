package server

import engine.GameEngine
import engine.Matrix
import models.agents.Agent
import models.obstacles.Wall
import models.projectiles.Projectile
import settings.*
import util.ZoneUtils
import util.delay
import util.jsObject
import util.launch

class DataUpdater(
    private val walls: ArrayList<Wall>,
    private val agents: ArrayList<Agent>,
    private val projectiles: ArrayList<Projectile>,
    private val matrix: Matrix,
    private val socketIo: dynamic
) : DataBroadcaster {
    fun agentDataLoop(gameEngine: GameEngine) = launch {
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

                for (zone in agent.viewportZones) matrix.agents[zone]?.forEach {
                    if (!ids.contains(it.id)) {
                        ids.add(it.id)
                        agData.add(jsObject {
                            x = it.bounds.bounds.min.x
                            y = it.bounds.bounds.min.y
                            name = it.name
                            xVelocity = it.velocity.x * PLAYER_MOVEMENT_SPEED
                            yVelocity = it.velocity.y * PLAYER_MOVEMENT_SPEED
                            bulletsLeft = if (it.weapon.reloadMark == -1.0) it.weapon.bulletsInChamber else -1
                            isDead = it.dead
                            currentHealth = it.health
                            id = it.id
                            weapon = it.weapon.projectileType
                            angle = it.directionAngle
                            inv = it.invincible
                        })
                    }
                }
                socketIo.to(agent.id).emit("agentData", jsObject { agentData = agData })
            }
            delay(1000L / AGENT_UPDATES_PER_SECOND)
        }
    }

    fun projectileDataLoop(gameEngine: GameEngine) = launch {
        while (gameEngine.continueLooping) {
            for (agent in agents) {
                val projData = ArrayList<dynamic>()
                val ids = ArrayList<String>()

                for (zone in agent.viewportZones) matrix.projectiles[zone]?.let {
                    for (projectile in it) {
                        if (ids.contains(projectile.id)) continue
                        ids.add(projectile.id)
                        projData.add(jsObject {
                            x = projectile.bounds.position.x
                            y = projectile.bounds.position.y
                            id = projectile.id
                            xSpeed = projectile.velocity.x
                            ySpeed = projectile.velocity.y
                            type = projectile.type
                            agentId = projectile.agentId
                        })
                    }
                }

                socketIo.to(agent.id).emit("projectileData", jsObject { projectileData = projData })
            }
            delay(1000L / PROJECTILE_UPDATES_PER_SECOND)
        }
    }

    fun pickupDataLoop(gameEngine: GameEngine) = launch {
        while (gameEngine.continueLooping) {
            for (agent in agents) {
                val pickData = ArrayList<dynamic>()
                val ids = ArrayList<String>()

                for (zone in agent.zones) matrix.pickups[zone]?.let {
                    for (pickup in it) {
                        if (ids.contains(pickup.id)) continue
                        ids.add(pickup.id)
                        pickData.add(jsObject {
                            x = pickup.x
                            y = pickup.y
                            type = pickup.type
                            id = pickup.id
                        })
                    }
                }
                socketIo.to(agent.id).emit("pickupData", jsObject { pickupData = pickData })
            }
            delay(1000L / PICKUP_UPDATES_PER_SECOND)
        }
    }

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

    fun broadcastPlayerDisconnect(agentId: String) {
        for (agent in agents) socketIo.to(agent.id).emit("playerDisconnected", jsObject { id = agentId })
    }

    override fun broadcastNewProjectile(projectile: Projectile) {
        for (agent in agents) {
            if (projectile.bounds.position.x > agent.bounds.position.x - WINDOW_WIDTH &&
                projectile.bounds.position.x < agent.bounds.position.x + WINDOW_WIDTH &&
                projectile.bounds.position.y > agent.bounds.position.y - WINDOW_HEIGHT &&
                projectile.bounds.position.y < agent.bounds.position.y + WINDOW_HEIGHT
            ) {
                socketIo.to(agent.id).emit("newProjectile", jsObject {
                    x = projectile.bounds.position.x
                    y = projectile.bounds.position.y
                    id = projectile.id
                    xSpeed = projectile.velocity.x
                    ySpeed = projectile.velocity.y
                    type = projectile.type
                    agentId = projectile.agentId
                })
            }
        }
    }
}

