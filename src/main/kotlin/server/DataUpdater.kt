package server

import engine.GameEngine
import engine.Matrix
import models.agents.Player
import models.obstacles.Wall
import models.projectiles.Projectile
import settings.*
import util.ZoneUtils
import util.delay
import util.jsObject
import util.launch

class DataUpdater(
    private val walls: ArrayList<Wall>,
    private val players: ArrayList<Player>,
    private val projectiles: ArrayList<Projectile>,
    private val matrix: Matrix,
    private val socketIo: dynamic
) : DataBroadcaster {
    fun playerDataLoop(gameEngine: GameEngine) = launch {
        while (gameEngine.continueLooping) {
            for (player in players) {

                calculatePlayerViewportZones(player)

                val agData = ArrayList<dynamic>()
                val ids = ArrayList<String>()

                for (zone in player.viewportZones) matrix.players[zone]?.forEach {
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
                socketIo.to(player.id).emit("agentData", jsObject { agentData = agData })
            }
            delay(1000L / AGENT_UPDATES_PER_SECOND)
        }
    }

    fun zombieDataLoop(gameEngine: GameEngine) = launch {
        while (gameEngine.continueLooping) {
            for (player in players) {
                val zombData = ArrayList<dynamic>()
                val ids = ArrayList<String>()
                for (zone in player.viewportZones) if (matrix.zombies[zone] != null) for (zombie in matrix.zombies[zone]!!) {
                    if (ids.contains(zombie.id)) continue
                    ids.add(zombie.id)
                    zombData.add(jsObject {
                        x = zombie.bounds.bounds.min.x
                        y = zombie.bounds.bounds.min.y
                        xVelocity = zombie.velocity.x * ZOMBIE_MOVEMENT_SPEED
                        yVelocity = zombie.velocity.y * ZOMBIE_MOVEMENT_SPEED
                        isDead = zombie.dead
                        currentHealth = zombie.health
                        id = zombie.id
                        angle = zombie.directionAngle
                    })
                }
                socketIo.to(player.id).emit("zombieData", jsObject { zombieData = zombData })
            }
            delay(1000L / AGENT_UPDATES_PER_SECOND)
        }
    }

    private fun calculatePlayerViewportZones(player: Player) {
        var minX = player.bounds.position.x - WINDOW_WIDTH
        var minY = player.bounds.position.y - WINDOW_HEIGHT
        var maxX = player.bounds.position.x + WINDOW_WIDTH
        var maxY = player.bounds.position.y + WINDOW_HEIGHT

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

        player.viewportZones = ZoneUtils.getZonesForBounds(
            minX = minX as Int,
            maxX = maxX as Int,
            minY = minY as Int,
            maxY = maxY as Int
        )
    }

    fun projectileDataLoop(gameEngine: GameEngine) = launch {
        while (gameEngine.continueLooping) {
            for (agent in players) {
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
            for (agent in players) {
                val pickData = ArrayList<dynamic>()
                val ids = ArrayList<String>()

                for (zone in agent.viewportZones) matrix.pickups[zone]?.let {
                    for (pickup in it) {
                        if (ids.contains(pickup.id)) continue
                        ids.add(pickup.id)
                        pickData.add(jsObject {
                            x = pickup.bounds.bounds.min.x
                            y = pickup.bounds.bounds.min.y
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

    fun explosiveBarrelDataLoop(gameEngine: GameEngine) = launch {
        while (gameEngine.continueLooping) {
            for (agent in players) {
                val barrData = ArrayList<dynamic>()
                val ids = ArrayList<String>()
                for (zone in agent.viewportZones) matrix.explosiveBarrels[zone]?.let {
                    for (barrel in it) {
                        if (ids.contains(barrel.id)) continue
                        ids.add(barrel.id)
                        barrData.add(jsObject {
                            x = barrel.bounds.bounds.min.x
                            y = barrel.bounds.bounds.min.y
                            id = barrel.id
                        })
                    }
                }
                socketIo.to(agent.id).emit("barrelData", barrData)
            }
            delay(1000L / EXPLOSIBE_BARREL_UPDATES_PER_SECOND)
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
        for (agent in players) socketIo.to(agent.id).emit("playerDisconnected", jsObject { id = agentId })
    }

    override fun broadcastNewProjectile(projectile: Projectile) {
        for (agent in players) {
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

    override fun broadcastNewExplosion(xPos: Float, yPos: Float, projType: String) {
        for (agent in players) if (xPos > agent.bounds.position.x as Float - WINDOW_WIDTH &&
            xPos < agent.bounds.position.x as Float + WINDOW_WIDTH &&
            yPos > agent.bounds.position.y as Float - WINDOW_HEIGHT &&
            yPos < agent.bounds.position.y as Float + WINDOW_HEIGHT
        ) socketIo.to(agent.id).emit("newExplosion", jsObject { x = xPos; y = yPos; type = projType; })
    }

    override fun broadcastScoreBoard() {
        val sb = ArrayList<dynamic>()
        for (agent in players) {
            sb.add(jsObject {
                id = agent.id
                kills = agent.kills
                deaths = agent.deaths
                name = agent.name
            })
        }
        for (agent in players) socketIo.to(agent.id).emit("scoreboardData", jsObject { scoreboardData = sb })
    }

    override fun broadcastKillConfirm(agentId: String) {
        socketIo.to(agentId).emit("killConfirm", jsObject { })
    }
}

