package settings.ui

import settings.WEAPON_RESPAWN_RATE
import settings.PISTOLS_ON_MAP
import settings.MACHINE_GUNS_ON_MAP
import settings.SHOTGUNS_ON_MAP
import settings.BAZOOKAS_ON_MAP
import settings.PISTOL_PROJECTILE_DAMAGE
import settings.MACHINE_GUN_PROJECTILE_DAMAGE
import settings.SHOTGUN_PROJECTILE_DAMAGE
import settings.BAZOOKA_EXPLOSION_DAMAGE
import settings.BARREL_EXPLOSION_DAMAGE
import settings.ZOMBIE_RESPAWN_RATE
import settings.ZOMBIES_PER_RESPAWN
import settings.MAX_ZOMBIES_ON_MAP
import settings.ZOMBIE_ATTACK
import settings.ZOMBIE_ATTACK_SPEED
import settings.ZOMBIE_MOVEMENT_SPEED
import settings.EXPLOSIVE_BARREL_RESPAWN_RATE
import settings.EXPLOSIVE_BARRELS_PER_RESPAWN
import settings.PLAYER_BASE_HEALTH
import settings.ZOMBIE_BASE_HEALTH
import settings.INVINCIBILITY_DURATION
import util.BodyParser
import util.Express
import util.jsObject

fun configureRoutes(app: dynamic) {
    app.set("view engine", "ejs")
    app.use(BodyParser.json())
    app.use(BodyParser.urlencoded(jsObject {
        extended = true
    }))
    app.get("/") { _, res, _ ->
        res.render("index", jsObject {
            weaponRespawnRate = WEAPON_RESPAWN_RATE
            pistolsOnMap = PISTOLS_ON_MAP
            machineGunsOnMap = MACHINE_GUNS_ON_MAP
            shotgunsOnMap = SHOTGUNS_ON_MAP
            bazookasOnMap = BAZOOKAS_ON_MAP

            pistolDamage = PISTOL_PROJECTILE_DAMAGE
            machineGunDamage = MACHINE_GUN_PROJECTILE_DAMAGE
            shotgunDamage = SHOTGUN_PROJECTILE_DAMAGE
            bazookaDamage = 0
            bazookaExplosionDamage = BAZOOKA_EXPLOSION_DAMAGE
            barrelExplosionDamage = BARREL_EXPLOSION_DAMAGE

            zombieRespawnRate = ZOMBIE_RESPAWN_RATE
            zombiesPerRespawn = ZOMBIES_PER_RESPAWN
            zombiesOnMap = MAX_ZOMBIES_ON_MAP
            zombieAttackDamage = ZOMBIE_ATTACK
            zombieAttackSpeed = ZOMBIE_ATTACK_SPEED
            zombieMovementSpeed = ZOMBIE_MOVEMENT_SPEED

            barrelRespawnRate = EXPLOSIVE_BARREL_RESPAWN_RATE
            barrelsPerRespawn = EXPLOSIVE_BARRELS_PER_RESPAWN

            playerHealth = PLAYER_BASE_HEALTH
            zombieHealth = ZOMBIE_BASE_HEALTH
            invincibilityDuration = INVINCIBILITY_DURATION
        })
    }

    app.post("/") { req, res, _ ->



        res.redirect("/")
    }
}