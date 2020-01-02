package models.agents

import settings.ZOMBIE_BASE_HEALTH
import settings.ZOMBIE_SPRITE_HEIGHT
import settings.ZOMBIE_SPRITE_WIDTH
import util.ShortId

class Zombie(
    x: Float,
    y: Float,
    directionAngle: Float
) : Agent(
    x = x,
    y = y,
    directionAngle = directionAngle,
    id = ShortId.generate() as String,
    width = ZOMBIE_SPRITE_WIDTH,
    height = ZOMBIE_SPRITE_HEIGHT,
    health = ZOMBIE_BASE_HEALTH
)