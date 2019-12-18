package server

import models.projectiles.Projectile

interface DataBroadcaster {
    fun broadcastNewProjectile(projectile: Projectile)
}