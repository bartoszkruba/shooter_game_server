package models.projectiles

import settings.SHOTGUN_BULLET_SPEED
import settings.SHOTGUN_PROJECTILE_DAMAGE
import settings.STANDARD_PROJECTILE_WIDTH

class ShotgunProjectile(
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
    type = ProjectileType.SHOTGUN,
    speed = SHOTGUN_BULLET_SPEED,
    damage = SHOTGUN_PROJECTILE_DAMAGE
)