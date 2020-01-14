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
        WEAPON_RESPAWN_RATE = (req.body.weaponRespawnRate as String).toInt()
        PISTOLS_ON_MAP = (req.body.pistolsOnMap as String).toInt()
        MACHINE_GUNS_ON_MAP = (req.body.machineGunsOnMap as String).toInt()
        SHOTGUNS_ON_MAP = (req.body.shotgunsOnMap as String).toInt()
        BAZOOKAS_ON_MAP = (req.body.bazookasOnMap as String).toInt()

        PISTOL_PROJECTILE_DAMAGE = (req.body.pistolDamage as String).toFloat()
        MACHINE_GUN_PROJECTILE_DAMAGE = (req.body.machineGunDamage as String).toFloat()
        SHOTGUN_PROJECTILE_DAMAGE = (req.body.shotgunDamage as String).toFloat()
        BAZOOKA_EXPLOSION_DAMAGE = (req.body.bazookaExplosionDamage as String).toInt()
        BARREL_EXPLOSION_DAMAGE = (req.body.barrelExplosionDamage as String).toInt()

        ZOMBIE_RESPAWN_RATE = (req.body.zombieRespawnRate as String).toInt()
        ZOMBIES_PER_RESPAWN = (req.body.zombiesPerRespawn as String).toInt()
        MAX_ZOMBIES_ON_MAP = (req.body.zombiesOnMap as String).toInt()
        ZOMBIE_ATTACK = (req.body.zombieAttackDamage as String).toInt()
        ZOMBIE_ATTACK_SPEED = (req.body.zombieAttackSpeed as String).toInt()
        ZOMBIE_MOVEMENT_SPEED = (req.body.zombieMovementSpeed as String).toFloat()

        EXPLOSIVE_BARREL_RESPAWN_RATE = (req.body.barrelRespawnRate as String).toInt()
        EXPLOSIVE_BARREL_RESPAWN_RATE = (req.body.barrelsPerRespawn as String).toInt()

        PLAYER_BASE_HEALTH = (req.body.playerHealth as String).toInt()
        ZOMBIE_BASE_HEALTH = (req.body.zombieHealth as String).toInt()
        INVINCIBILITY_DURATION = (req.body.invincibilityDuration as String).toInt()

        res.redirect("/")
    }
}