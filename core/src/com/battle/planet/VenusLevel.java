package com.battle.planet;

public class VenusLevel extends BattleLevel {

    public VenusLevel(PlanetBattle g) {
        super(g);
        enemies.add(new Venus(300, 300));
        playerHitbox.setPosition(150, 300);
    }

    @Override
    public void render (float delta) {
        prepareGraphics();

        prepareValues();

        draw();

        movePlayer();

        playerShoot();

        //Enemy actions
        for (Enemy e: enemies) {
            enemyBullets.addAll(e.attack(hitboxCenter.x, hitboxCenter.y, frame));
            if (e.canSpawn) {
                //Prevent over spawning of enemies
                //Max of 3 non Venus enemies
                if (enemies.size < 4) {
                    enemies.addAll(e.spawn(hitboxCenter.x, hitboxCenter.y, frame));
                } else {
                    e.canSpawn = false;
                    if (e instanceof Venus) {
                        ((Venus) e).spawnCooldown = ((Venus) e).SPAWN_MAX_COOLDOWN;
                    }
                }
            }
            e.move();
            if (e instanceof Venus && enemies.size == 1) {
                e.collide(playerBullets);
            } else {
                if (e instanceof Venus) {

                } else {
                    e.collide(playerBullets);
                }
            }
        }

        playerCollisions();

        removeBullets();

        removeEnemies();

        checkWin();

        specialControls();
    }

}
