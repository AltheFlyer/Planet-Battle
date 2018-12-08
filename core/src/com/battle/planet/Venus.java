package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Venus extends Enemy {

    int phase = 0;

    float spawnCooldown;
    final float SPAWN_MAX_COOLDOWN = 3f;
    int spawned = 0;

    float aimCooldown;
    final float AIM_MAX_COOLDOWN = 1f;

    float mainCooldown;
    final float MAIN_MAX_COOLDOWN = 5f;

    public Venus(float x, float y) {
        super(x, y, 70, 80, 500);

        spawnCooldown = SPAWN_MAX_COOLDOWN;
        aimCooldown = AIM_MAX_COOLDOWN;
        mainCooldown = MAIN_MAX_COOLDOWN;

        phaseMarkers.add(400);
        phaseMarkers.add(300);
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
            mainCooldown = 1.5f;
        }
        if (phase == 2 && health <= 300) {
            phase = 3;
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
