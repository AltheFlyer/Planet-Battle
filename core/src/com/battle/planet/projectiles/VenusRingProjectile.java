package com.battle.planet.projectiles;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.battle.planet.BattleLevel;
import com.battle.planet.enemies.Venus;

public class VenusRingProjectile extends OrbitalProjectile {

    public final Venus venus;
    public boolean orbiting = true;
    public float wait = 3.0f;

    public VenusRingProjectile(final BattleLevel lev, float dst, float cx, float cy, float omega, float angle, Venus v) {
        super(lev, dst, cx, cy, omega, angle);
        center = new Vector2(cx, cy);
        angularVelocity = omega;
        angularPosition = angle;
        distance = dst;
        venus = v;
        hitbox.x = center.x + MathUtils.cos(angularPosition) * distance;
        hitbox.y = center.y + MathUtils.sin(angularPosition) * distance;
    }

    @Override
    public void move(float x, float y, float frame) {
        if (orbiting && wait <= 0) {
            angularPosition += angularVelocity * frame;
            hitbox.x = center.x + MathUtils.cos(angularPosition) * distance;
            hitbox.y = center.y + MathUtils.sin(angularPosition) * distance;
        } else {
            hitbox.x += velocity.x * frame;
            hitbox.y += velocity.y * frame;
            wait -= frame;
        }
        if (venus.getPhase() != 1) {
            float newAngle = MathUtils.atan2(hitbox.y - (300 + 200 * MathUtils.sin(angularPosition)), hitbox.x - (300 + 200 * MathUtils.cos(angularPosition)));
            velocity = new Vector2(MathUtils.cos(newAngle) * 300, MathUtils.sin(newAngle) * 300);
            orbiting = false;
        }
    }
}
