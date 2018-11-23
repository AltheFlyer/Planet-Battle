package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Venus extends Enemy {

    int phase = 0;

    float spawnCooldown;
    final float SPAWN_MAX_COOLDOWN = 3f;
    int spawned = 0;

    public Venus(float x, float y) {
        super(x, y, 70, 80, 500);

        spawnCooldown = SPAWN_MAX_COOLDOWN;
        phaseMarkers.add(400);
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GOLDENROD);
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public Array<Projectile> attack(float x, float y, float frame) {
        bullets.clear();
        if (phase == 1) {
            spawnCooldown -= frame;
            if (spawnCooldown <= 0 && spawned < 3) {
                spawnCooldown = SPAWN_MAX_COOLDOWN;
                canSpawn = true;
            }
        }
        if (phase == 0) {
            //Create orbital ring
            for (int i = 0; i < 72; ++i) {
                float angle = (float) (i/72.0) * 6.28f;
                bullets.add(new VenusRingProjectile(150,300 + 200 * MathUtils.cos(angle), 300 + 200 * MathUtils.sin(angle), 1.5f, 3.14f, this));
            }
            phase = 1;
        }
        if (phase == 1 && health <= 400) {
            phase = 2;
        }
        return bullets;
    }

    @Override
    public Array<Enemy> spawn(float x, float y, float frame) {
        Array<Enemy> enemies = new Array<Enemy>();
        int rand = MathUtils.random(0, 3);
        //Spawn from random sides
        if (rand == 0) {
            enemies.add(new AcidCloud(MathUtils.random(0, 600), 670, this));
        } else if (rand == 1) {
            enemies.add(new AcidCloud(MathUtils.random(0, 600), -70, this));
        } else if (rand == 2) {
            enemies.add(new AcidCloud(670, MathUtils.random(0, 600), this));
        } else if (rand == 3) {
            enemies.add(new AcidCloud(-70, MathUtils.random(0, 600), this));
        }
        canSpawn = false;
        spawned += 1;
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

}
