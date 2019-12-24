package models.projectiles

import settings.BAZOOKA_BULLET_SPEED
import settings.BAZOOKA_PROJECTILE_WIDTH

class BazookaProjectile(
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
    radius = BAZOOKA_PROJECTILE_WIDTH / 2f,
    type = ProjectileType.BAZOOKA,
    speed = BAZOOKA_BULLET_SPEED,
    damage = 0f
)