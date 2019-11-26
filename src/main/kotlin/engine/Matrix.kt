package engine

import models.agents.Agent
import models.obstacles.Wall

class Matrix() {
    val walls = HashMap<String, ArrayList<Wall>>()
    val agents = HashMap<String, ArrayList<Agent>>()
}