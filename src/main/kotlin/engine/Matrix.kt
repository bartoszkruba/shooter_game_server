package engine

import models.agents.Agent
import models.obstacles.Wall
import models.projectiles.Projectile

class Matrix {
    val walls = HashMap<String, ArrayList<Wall>>()
    val agents = HashMap<String, ArrayList<Agent>>()
    val projectiles = HashMap<String, ArrayList<Projectile>>()
}