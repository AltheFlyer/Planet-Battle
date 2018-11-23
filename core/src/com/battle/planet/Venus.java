package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Venus extends Enemy {

    int phase = 1;

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
        spawnCooldown -= frame;
        if (spawnCooldown <= 0) {
            spawnCooldown = SPAWN_MAX_COOLDOWN;
            canSpawn = true;
        }
        return bullets;
    }

    @Override
    public Array<Enemy> spawn(float x, float y, float frame) {
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.add(new AcidCloud(MathUtils.random(0, 600), MathUtils.random(600, 670)));
        canSpawn = false;
        return enemies;
    }

}
