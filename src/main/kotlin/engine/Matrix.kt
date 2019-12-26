package engine

import models.agents.Agent
import models.obstacles.ExplosiveBarrel
import models.obstacles.Wall
import models.pickups.Pickup
import models.projectiles.Projectile

class Matrix {
    val walls = HashMap<String, ArrayList<Wall>>()
    val agents = HashMap<String, ArrayList<Agent>>()
    val projectiles = HashMap<String, ArrayList<Projectile>>()
    val pickups = HashMap<String, ArrayList<Pickup>>()
    val explosiveBarrels = HashMap<String, ArrayList<ExplosiveBarrel>>()
}