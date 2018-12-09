package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Venus extends Enemy {

    int phase = 0;

    /*Controls cooldown for all non-specific spawning abilities
    Phase 1: Summons an acid seeker cloud
    Phase 2: Not Used
    Phase 3: Summons an acid cloud from a random offscreen position, with velocity toward the level center
    */
    float spawnCooldown;
    final float SPAWN_MAX_COOLDOWN = 3f;
    //Counts how many enemies are spawned (for shielding purposes)
    int spawned = 0;

    /*For any aimed abilities.
    Phase 1: Not Used
    Phase 2: Spawns acid clouds from offscreen to straight down toward the player.
    Phase 3: Not Used
     */
    float aimCooldown;
    final float AIM_MAX_COOLDOWN = 1f;

    /* Controls 'essential' attacks
    Phase 1: Not Used
    Phase 2: Used to periodically drop a bunch of acid clouds
    Phase 3: Controls a constant wave spread pattern
     */
    float mainCooldown;
    final float MAIN_MAX_COOLDOWN = 5f;

    float aimDirection = 3 * MathUtils.PI / 2;
    float aimChange = MathUtils.PI / 12;

    public Venus(float x, float y) {
        super(x, y, 70, 80, 500);

        spawnCooldown = SPAWN_MAX_COOLDOWN;
        aimCooldown = AIM_MAX_COOLDOWN;
        mainCooldown = MAIN_MAX_COOLDOWN;

        phaseMarkers.add(400);
        phaseMarkers.add(300);
        phaseMarkers.add(200);
        phaseMarkers.add(100);
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        if (spawned == 0) {
            r.setColor(Color.GOLDENROD);
        } else {
            r.setColor(Color.valueOf("#c1a868"));
        }
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public void drawObjects(final Player player, ShapeRenderer r) {
        float x = player.hitboxCenter.x;
        float y = player.hitboxCenter.y;
        if (phase == -1) {
            r.setColor(Color.YELLOW);
            r.rectLine(hitbox.x, hitbox.y, hitbox.x, 550, 5);
        }
    }

    @Override
    public Array<Projectile> attack(final Player player, float frame) {
        float x = player.hitboxCenter.x;
        float y = player.hitboxCenter.y;
        bullets.clear();
        if (phase == 1) {
            spawnCooldown -= frame;
            if (spawnCooldown <= 0 && spawned < 3) {
                spawnCooldown = SPAWN_MAX_COOLDOWN;
                canSpawn = true;
            }
        }
        if (phase == 2) {
            if (hitbox.y < 550) {
                float direction = MathUtils.atan2(550 - hitbox.y, 300 - hitbox.x);
                hitbox.x += MathUtils.cos(direction) * 200 * frame;
                hitbox.y += MathUtils.sin(direction) * 200 * frame;
            } else {
                mainCooldown -= frame;
                aimCooldown -= frame;
                if (aimCooldown <= 0) {
                    canSpawn = true;
                }
                if (mainCooldown <= 0) {
                    canSpawn = true;
                }
            }
        }
        if (phase == 3) {
            mainCooldown -= frame;
            spawnCooldown -= frame;
            if (spawnCooldown <= 0) {
                //More frequent spawning based on health
                //~50% cooldown at 201 health.
                spawnCooldown = (SPAWN_MAX_COOLDOWN * ((health - 200) / 200) + 0.5f);
                canSpawn = true;
            }
            //Creates spreads around an open area
            if (mainCooldown <= 0) {
                mainCooldown = MAIN_MAX_COOLDOWN * 0.03f;
                createSpread(aimDirection + MathUtils.PI / 6, 15, MathUtils.PI / 6, 120);
                createSpread(aimDirection - MathUtils.PI / 6, 15, MathUtils.PI / 6, 120);
                //Side spread
                createSpread(MathUtils.PI, 15, MathUtils.PI / 6, 120);
                createSpread(0, 15, MathUtils.PI / 6, 120);
            }
            aimDirection += aimChange * frame;
            if (aimDirection < 4 * MathUtils.PI / 3) {
                aimDirection = 4 * MathUtils.PI / 3;
                aimChange *= -1;
            } else if (aimDirection > 5 * MathUtils.PI / 3) {
                aimDirection = 5 * MathUtils.PI / 3;
                aimChange *= -1;
            }
        }
        if (phase == 4) {
            mainCooldown -= frame;
            aimCooldown -= frame;
            spawnCooldown -= frame;
        }


        if (phase == 0) {
            //Create orbital ring
            for (int i = 0; i < 72; ++i) {
                float angle = (float) (i/72.0) * 6.28f;
                bullets.add(new VenusRingProjectile(150,300 + 200 * MathUtils.cos(angle), 300 + 200 * MathUtils.sin(angle), 1.0f, 3.14f, this));
            }
            phase = 1;
        }
        if (phase == -1) {
            mainCooldown -= frame;
            if (mainCooldown <= 0) {
                //mainCooldown = MAIN_MAX_COOLDOWN;
                phase = 2;
            }
        }

        //Phase transitions
        if (phase == 1 && health <= 400) {
            phase = -1;
            mainCooldown = 0.7f;
        }
        if ((phase == 2 || phase == -1) && health <= 300) {
            mainCooldown = MAIN_MAX_COOLDOWN / 10;
            phase = 3;
        }
        if (phase == 3 && health <= 200) {
            phase = 4;
            canSpawn = true;
        }
        if (phase == 4 && health <= 100) {
            phase = 5;
        }
        return bullets;
    }

    @Override
    public Array<Enemy> spawn(final Player player, float frame) {
        float x = player.hitboxCenter.x;
        float y = player.hitboxCenter.y;
        Array<Enemy> enemies = new Array<Enemy>();
        if (phase == 1) {
            int rand = MathUtils.random(0, 3);
            //Spawn from random sides
            if (rand == 0) {
                enemies.add(new AcidSeeker(MathUtils.random(0, 600), 670, this));
            } else if (rand == 1) {
                enemies.add(new AcidSeeker(MathUtils.random(0, 600), -70, this));
            } else if (rand == 2) {
                enemies.add(new AcidSeeker(670, MathUtils.random(0, 600), this));
            } else if (rand == 3) {
                enemies.add(new AcidSeeker(-70, MathUtils.random(0, 600), this));
            }
            canSpawn = false;
            spawned += 1;
        }
        if (phase == 2) {
            if (aimCooldown <= 0) {
                enemies.add(new AcidCloud(x, 700, 0, -200));
                aimCooldown = AIM_MAX_COOLDOWN;
                canSpawn = false;
            }
            if (mainCooldown <= 0) {
                for (int i = 0; i < 25; ++i) {
                    enemies.add(new AcidCloud(MathUtils.random(20, 580), MathUtils.random(620, 749), 0, -MathUtils.random(175, 200)));
                }
                mainCooldown = MAIN_MAX_COOLDOWN;
                canSpawn = false;
            }
        }
        if (phase == 3) {
            float angle = MathUtils.random(360) * MathUtils.degreesToRadians;
            enemies.add(new AcidCloud(300 + MathUtils.cos(angle) * 400, 300 + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 150, -MathUtils.sin(angle) * 150));
            canSpawn = false;
        }
        if (phase == 4) {
            enemies.add(new VenusReflector(this, hitbox.radius + 40, 100, 0, MathUtils.PI / 2, true));
            enemies.add(new VenusReflector(this, hitbox.radius + 40, 100, MathUtils.PI / 12, MathUtils.PI / 2, true));
            enemies.add(new VenusReflector(this, hitbox.radius + 40, 100, -MathUtils.PI / 12, MathUtils.PI / 2, true));

            enemies.add(new VenusReflector(this, hitbox.radius + 40, 100, MathUtils.PI, MathUtils.PI / 2, true));
            enemies.add(new VenusReflector(this, hitbox.radius + 40, 100, 11 * MathUtils.PI / 12, MathUtils.PI / 2, true));
            enemies.add(new VenusReflector(this, hitbox.radius + 40, 100, 13 * MathUtils.PI / 12, MathUtils.PI / 2, true));
            canSpawn = false;
        }
        /*
        if (phase == -1) {
            for (int i = 0; i < 8; ++i) {
                enemies.add(new VenusShield(130, 0.785398f * i, 0.785398f, this));
                enemies.add(new VenusSniper(100, 0.785398f * i, 0.785398f, this));
            }
            spawned += 16;
            canSpawn = false;
            phase = 2;
        }
         */
        return enemies;
    }

    @Override
    public Array<Projectile> collide(Array<Projectile> projectiles) {
        if (spawned == 0 || phase != 1) {
            for (Projectile p : projectiles) {
                if (!p.isDestroyed && this.hitbox.contains(p.hitbox.x, p.hitbox.y)) {
                    p.isDestroyed = true;
                    this.health -= 1;
                }
            }
        }
        return projectiles;
    }

}
