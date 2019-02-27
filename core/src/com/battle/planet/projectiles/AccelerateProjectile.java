package com.battle.planet.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.battle.planet.BattleLevel;

public class AccelerateProjectile extends Projectile {

    public Vector2 acceleration;
    public float maximum;
    public float lifespan;

    public AccelerateProjectile(final BattleLevel lev, float x, float y, float vx, float vy, float ax, float ay, float maxMag, float maxDuration) {
        super(lev, x, y, vx, vy);
        acceleration = new Vector2(ax, ay);
        maximum = maxMag;
        lifespan = maxDuration;
    }

    @Override
    public void move(float frame) {
        time += frame;
        hitbox.x += velocity.x * frame;
        hitbox.y += velocity.y * frame;

        velocity.x += acceleration.x * frame;
        velocity.y += acceleration.y * frame;

        if (velocity.dst2(new Vector2(0, 0)) > maximum * maximum) {
            velocity.clamp(maximum, maximum);
        }

        if (time > lifespan) {
            isDestroyed = true;
        }
    }
}
