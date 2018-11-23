package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Venus extends Enemy {

    int phase = 0;

    float spawnCooldown;
    final float SPAWN_MAX_COOLDOWN = 3f;

    public Venus(float x, float y) {
        super(x, y, 70, 80, 500);

        spawnCooldown = SPAWN_MAX_COOLDOWN;
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
            if (spawnCooldown <= 0) {
                spawnCooldown = SPAWN_MAX_COOLDOWN;
                canSpawn = true;
            }
        }
        if (phase == 0) {
            //Create orbital ring
            for (int i = 0; i < 36; ++i) {
                float angle = (float) (i/36.0) * 6.28f;
                System.out.println(angle * MathUtils.radiansToDegrees);
                bullets.add(new OrbitalProjectile(150,300 + 200 * MathUtils.cos(angle), 300 + 200 * MathUtils.sin(angle), 0.03f, 3.14f));
            }
            phase = 1;
        }
        return bullets;
    }

    @Override
    public Array<Enemy> spawn(float x, float y, float frame) {
        Array<Enemy> enemies = new Array<Enemy>();
        int rand = MathUtils.random(0, 3);
        //Spawn from random sides
        if (rand == 0) {
            enemies.add(new AcidCloud(MathUtils.random(0, 600), 670));
        } else if (rand == 1) {
            enemies.add(new AcidCloud(MathUtils.random(0, 600), -70));
        } else if (rand == 2) {
            enemies.add(new AcidCloud(670, MathUtils.random(0, 600)));
        } else if (rand == 3) {
            enemies.add(new AcidCloud(-70, MathUtils.random(0, 600)));
        }
        canSpawn = false;
        return enemies;
    }

}
