package com.battle.planet;

import com.badlogic.gdx.math.Vector2;

public class AccelerateProjectile extends Projectile {

    Vector2 acceleration;
    float maximum;
    float lifespan;

    public AccelerateProjectile(float x, float y, float vx, float vy, float ax, float ay, float maxMag, float maxDuration) {
        super(x, y, vx, vy);
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
