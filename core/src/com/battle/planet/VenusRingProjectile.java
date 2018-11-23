package com.battle.planet;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class VenusRingProjectile extends OrbitalProjectile {

    final Venus venus;
    boolean orbiting = true;

    public VenusRingProjectile(float dst, float cx, float cy, float omega, float angle, Venus v) {
        super(dst, cx, cy, omega, angle);
        center = new Vector2(cx, cy);
        angularVelocity = omega;
        angularPosition = angle;
        distance = dst;
        venus = v;
    }

    @Override
    public void move(float x, float y, float frame) {
        if (orbiting) {
            angularPosition += angularVelocity * frame;
            hitbox.x = center.x + MathUtils.cos(angularPosition) * distance;
            hitbox.y = center.y + MathUtils.sin(angularPosition) * distance;
        } else {
            hitbox.x += velocity.x * frame;
            hitbox.y += velocity.y * frame;
        }
        if (venus.phase != 1) {
            float newAngle = MathUtils.atan2(hitbox.y - (300 + 200 * MathUtils.sin(angularPosition)), hitbox.x - (300 + 200 * MathUtils.cos(angularPosition)));
            velocity = new Vector2(MathUtils.cos(newAngle) * 300, MathUtils.sin(newAngle) * 300);
            orbiting = false;
        }
    }
}
