package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.projectiles.BasicProjectile;
import com.battle.planet.projectiles.Projectile;
import com.battle.planet.projectiles.VenusRingProjectile;

public class Venus extends Enemy {

    public int phase = 0;

    /*Controls cooldown for all non-specific spawning abilities
    Phase 1: Summons an acid seeker cloud
    Phase 2: Not Used
    Phase 3: Summons an acid cloud from a random offscreen position, with velocity toward the level center
    Phase 4: Not Used
    */
    float spawnCooldown;
    final float SPAWN_MAX_COOLDOWN = 3f;
    //Counts how many enemies are spawned (for shielding purposes)
    int spawned = 0;

    /*For any aimed abilities.
    Phase 1: Not Used
    Phase 2: Spawns acid clouds from offscreen to straight down toward the player.
    Phase 3: Not Used
    Phase 4: Aims hearts towards the player.
     */
    float aimCooldown;
    final float AIM_MAX_COOLDOWN = 1f;

    /* Controls 'essential' attacks
    Phase 1: Not Used
    Phase 2: Used to periodically drop a bunch of acid clouds
    Phase 3: Controls a constant wave spread pattern
    Phase 4: Controls constant acid rain
     */
    float mainCooldown;
    final float MAIN_MAX_COOLDOWN = 5f;

    float aimDirection = 3 * MathUtils.PI / 2;
    float aimChange = MathUtils.PI / 12;

    public Venus(final BattleLevel lev, float x, float y) {
        super(lev, x, y, 70, 80, 500);

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
    public void drawObjects(ShapeRenderer r) {
        float x = player.hitboxCenter.x;
        float y = player.hitboxCenter.y;
        if (phase == -1) {
            r.setColor(Color.YELLOW);
            r.rectLine(hitbox.x, hitbox.y, hitbox.x, 550, 5);
        }
    }

    @Override
    public Array<Projectile> attack(float frame) {
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
            //spawnCooldown -= frame;
            //Constant acid rain
            if (mainCooldown <= 0) {
                mainCooldown = MAIN_MAX_COOLDOWN * 0.03f;
                bullets.add(new BasicProjectile(level, MathUtils.random(100, 700), 700, -MathUtils.random(10, 70), -MathUtils.random(170, 200)));
                //bullets.add(new BasicProjectile(MathUtils.random(100, 700), 700, -MathUtils.random(10, 70), -MathUtils.random(170, 200)));
            }
            //Hearts
            if (aimCooldown <= 0) {
                float angle = MathUtils.atan2(player.hitbox.y - hitbox.y, player.hitbox.x - hitbox.x);
                createHeart(hitbox.x, hitbox.y, angle, 10, MathUtils.cos(angle) * 150, MathUtils.sin(angle) * 150);
                aimCooldown = AIM_MAX_COOLDOWN * 2;
            }
        }
        if (phase == 5) {
            mainCooldown -= frame;
            aimCooldown -= frame;
            spawnCooldown -= frame;

            //Constant acid rain (still)
            if (mainCooldown <= 0) {
                mainCooldown = MAIN_MAX_COOLDOWN * 0.06f;
                bullets.add(new BasicProjectile(level, MathUtils.random(100, 700), 700, -MathUtils.random(10, 70), -MathUtils.random(170, 200)));
                //bullets.add(new BasicProjectile(MathUtils.random(100, 700), 700, -MathUtils.random(10, 70), -MathUtils.random(170, 200)));
            }

            if (spawnCooldown <= 0) {
                canSpawn = true;
                spawnCooldown = SPAWN_MAX_COOLDOWN * 1.5f;
            }
        }


        if (phase == 0) {
            //Create orbital ring
            for (int i = 0; i < 72; ++i) {
                float angle = (float) (i/72.0) * 6.28f;
                bullets.add(new VenusRingProjectile(level, 150,300 + 200 * MathUtils.cos(angle), 300 + 200 * MathUtils.sin(angle), 1.0f, 3.14f, this));
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
    public Array<Enemy> spawn(float frame) {
        float x = player.hitboxCenter.x;
        float y = player.hitboxCenter.y;
        Array<Enemy> enemies = new Array<Enemy>();
        if (phase == 1) {
            int rand = MathUtils.random(0, 3);
            //Spawn from random sides
            if (rand == 0) {
                enemies.add(new AcidSeeker(level, this, MathUtils.random(0, 600), 670));
            } else if (rand == 1) {
                enemies.add(new AcidSeeker(level, this, MathUtils.random(0, 600), -70));
            } else if (rand == 2) {
                enemies.add(new AcidSeeker(level, this, 670, MathUtils.random(0, 600)));
            } else if (rand == 3) {
                enemies.add(new AcidSeeker(level, this, -70, MathUtils.random(0, 600)));
            }
            canSpawn = false;
            spawned += 1;
        }
        if (phase == 2) {
            if (aimCooldown <= 0) {
                enemies.add(new AcidCloud(level, MathUtils.random(x-150, x+150), 700, 0, -200));
                aimCooldown = AIM_MAX_COOLDOWN/2;
                canSpawn = false;
            }
            if (mainCooldown <= 0) {
                for (int i = 0; i < 25; ++i) {
                    enemies.add(new AcidCloud(level, MathUtils.random(20, 580), MathUtils.random(620, 749), 0, -MathUtils.random(175, 200)));
                }
                mainCooldown = MAIN_MAX_COOLDOWN;
                canSpawn = false;
            }
        }
        if (phase == 3) {
            float angle = MathUtils.random(360) * MathUtils.degreesToRadians;
            enemies.add(new AcidCloud(level, 300 + MathUtils.cos(angle) * 400, 300 + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 150, -MathUtils.sin(angle) * 150));
            canSpawn = false;
        }
        if (phase == 4) {
            enemies.add(new VenusReflector(level, this, hitbox.radius + 40, 1000, 0, MathUtils.PI / 2, true));
            enemies.add(new VenusReflector(level, this, hitbox.radius + 40, 1000, MathUtils.PI / 12, MathUtils.PI / 2, true));
            enemies.add(new VenusReflector(level, this, hitbox.radius + 40, 1000, -MathUtils.PI / 12, MathUtils.PI / 2, true));

            enemies.add(new VenusReflector(level,  this, hitbox.radius + 40, 1000, MathUtils.PI, MathUtils.PI / 2, true));
            enemies.add(new VenusReflector(level,  this, hitbox.radius + 40, 1000, 11 * MathUtils.PI / 12, MathUtils.PI / 2, true));
            enemies.add(new VenusReflector(level,  this, hitbox.radius + 40, 1000, 13 * MathUtils.PI / 12, MathUtils.PI / 2, true));
            canSpawn = false;
        }
        if (phase == 5) {
            float angle = MathUtils.random(360) * MathUtils.degreesToRadians;
            for (int i = 0; i < 6; ++i) {
                angle += MathUtils.PI/3;
                enemies.add(new AcidCloud(level, 300 + MathUtils.cos(angle) * 400, 300 + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 100, -MathUtils.sin(angle) * 100));
            }
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
        if (spawned == 0) {
            for (Projectile p : projectiles) {
                if (!p.isDestroyed && this.hitbox.contains(p.hitbox.x, p.hitbox.y)) {
                    p.isDestroyed = true;
                    this.health -= 1;
                }
            }
        }
        return projectiles;
    }

    @Override
    public void drawHealthBars(ShapeRenderer r) {
        //Invincibility gets different colored healthbar
        if (spawned > 0) {
            r.setColor(Color.DARK_GRAY);
            r.rect(hitbox.x - hitbox.radius, hitbox.y - hitbox.radius, hitbox.radius * 2, 10);
            r.setColor(Color.valueOf("#68d9ff"));
            r.rect(hitbox.x - hitbox.radius, hitbox.y - hitbox.radius, hitbox.radius * 2 * (health / MAX_HEALTH), 10);
        } else {
            super.drawHealthBars(r);
        }
    }

    public void createHeart(float x, float y, float angle, float scale, float vx, float vy) {
        float s = MathUtils.sin(angle + MathUtils.PI / 2);
        float c = MathUtils.cos(angle + MathUtils.PI / 2);

        //Top and bottom
        bullets.add(new BasicProjectile(level, x + rotateX(0, 2 * scale, c, s), y + rotateY(0, 2 * scale, c, s), vx, vy));
        bullets.add(new BasicProjectile(level, x + rotateX(0, -4 * scale, c, s), y + rotateY(0, -4 * scale, c, s), vx, vy));

        //Heart halves, negative for left, positive for right.
        for (int i = -1; i < 2; i += 2) {

            bullets.add(new BasicProjectile(level, x + rotateX(1 * scale * i, 2.5f * scale, c, s), y + rotateY( 1 * scale * i, 2.5f * scale, c, s), vx, vy));
            bullets.add(new BasicProjectile(level, x + rotateX(2 * scale * i, 3 * scale, c, s), y + rotateY(2 * scale * i, 3 * scale, c, s), vx, vy));
            bullets.add(new BasicProjectile(level, x + rotateX(3 * scale * i, 2.5f * scale, c, s), y + rotateY(3 * scale * i, 2.5f * scale, c, s), vx, vy));
            bullets.add(new BasicProjectile(level, x + rotateX(3.5f * scale * i, 2 * scale, c, s), y + rotateY(3.5f * scale * i, 2 * scale, c, s), vx, vy));
            bullets.add(new BasicProjectile(level, x + rotateX(4 * scale * i, 1 * scale, c, s), y + rotateY(4 * scale * i, 1 * scale, c, s), vx, vy));

            bullets.add(new BasicProjectile(level, x + rotateX(4 * scale * i, 0, c, s), y + rotateY(4 * scale * i, 0, c, s), vx, vy));
            bullets.add(new BasicProjectile(level, x + rotateX(3 * scale * i,-1 * scale, c, s), y + rotateY(3 * scale * i, -1 * scale, c, s), vx, vy));
            bullets.add(new BasicProjectile(level, x + rotateX(2 * scale * i,-2 * scale, c, s), y + rotateY(2 * scale * i, -2 * scale, c, s), vx, vy));
            bullets.add(new BasicProjectile(level, x + rotateX(1 * scale * i,-3 * scale, c, s), y + rotateY(1 * scale * i, -3 * scale, c, s), vx, vy));
        }
    }

    public float rotateX(float x, float y, float c, float s) {
        return x * c - y * s;
    }

    public float rotateY(float x, float y, float c, float s) {
        return y * c + x * s;
    }
}
