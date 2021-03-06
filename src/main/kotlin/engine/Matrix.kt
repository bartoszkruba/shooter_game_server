package engine

import models.agents.Player
import models.agents.Zombie
import models.obstacles.ExplosiveBarrel
import models.obstacles.Wall
import models.pickups.Pickup
import models.projectiles.Projectile

class Matrix {
    val walls = HashMap<String, ArrayList<Wall>>()
    val players = HashMap<String, ArrayList<Player>>()
    val zombies = HashMap<String, ArrayList<Zombie>>()
    val projectiles = HashMap<String, ArrayList<Projectile>>()
    val pickups = HashMap<String, ArrayList<Pickup>>()
    val explosiveBarrels = HashMap<String, ArrayList<ExplosiveBarrel>>()
}