package models.projectiles

import settings.PISTOL_BULLET_SPEED
import settings.PISTOL_PROJECTILE_DAMAGE
import settings.STANDARD_PROJECTILE_WIDTH

class PistolProjectile(
    x: Float,
    y: Float,
    xSpeed: Float,
    ySpeed: Float,
    agentId: String
) : Projectile(
    x = x,
    y = y,
    xSpeed = xSpeed,
    ySpeed = ySpeed,
    agentId = agentId,
    radius = STANDARD_PROJECTILE_WIDTH / 2f,
    type = ProjectileType.PISTOL,
    speed = PISTOL_BULLET_SPEED,
    damage = PISTOL_PROJECTILE_DAMAGE
)