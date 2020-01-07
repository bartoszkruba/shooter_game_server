package server

import models.agents.Zombie
import models.projectiles.Projectile

interface DataBroadcaster {
    fun broadcastNewProjectile(projectile: Projectile)
    fun broadcastNewExplosion(xPos: Float, yPos: Float, projType: String)
    fun broadcastScoreBoard()
    fun broadcastKillConfirm(agentId: String)
    fun broadcastZombieDead(zombie: Zombie)
    fun broadcastZombieBite(zombie: Zombie)
}