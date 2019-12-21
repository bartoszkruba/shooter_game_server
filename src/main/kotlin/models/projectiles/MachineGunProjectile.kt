package models.projectiles

import settings.MACHINE_GUN_BULLET_SPEED
import settings.MACHINE_GUN_PROJECTILE_DAMAGE
import settings.STANDARD_PROJECTILE_WIDTH

class MachineGunProjectile(
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
    type = ProjectileType.MACHINE_GUN,
    speed = MACHINE_GUN_BULLET_SPEED,
    damage = MACHINE_GUN_PROJECTILE_DAMAGE
)