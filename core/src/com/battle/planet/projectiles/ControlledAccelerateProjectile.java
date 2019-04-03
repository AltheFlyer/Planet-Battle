package com.battle.planet.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.battle.planet.BattleLevel;

/**
 * A special projectile that deaccelerates until it reaches 0
 *
 */
public class ControlledAccelerateProjectile extends Projectile {

    public float acceleration;
    public float target;
    public float lifespan;
    public float magnitude;
    //If the starting velocity is greater than the target ending velocity
    public boolean aboveMag;

    public ControlledAccelerateProjectile(BattleLevel lev, float x, float y, float vx, float vy,
                                          float a, float targetA, float time) {
        super(lev, x, y, vx, vy);
        acceleration = a;
        target = targetA;
        lifespan = time;
        aboveMag = vx * vx + vy * vy > target * target;
        magnitude = (float) Math.sqrt(vx * vx + vy * vy);
    }

    @Override
    public void move(float frame) {
        time += frame;
        hitbox.x += velocity.x * frame;
        hitbox.y += velocity.y * frame;

        magnitude += acceleration * frame;
        velocity.clamp(magnitude, magnitude);

        if (aboveMag) {
            if (magnitude < target) {
                acceleration = 0;
                magnitude = target;
            }
        } else {
            if (magnitude > target) {
                acceleration = 0;
                magnitude = target;
            }
        }

        if (time > lifespan) {
            isDestroyed = true;
        }
    }
}
