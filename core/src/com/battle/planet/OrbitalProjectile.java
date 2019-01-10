package com.battle.planet;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class OrbitalProjectile extends Projectile {

    Vector2 center;
    float angularPosition;
    float angularVelocity;
    float distance;

    public OrbitalProjectile(final BattleLevel lev, float dst, float cx, float cy, float omega, float angle) {
        super(lev, 0, 0, 0, 0);
        center = new Vector2(cx, cy);
        angularVelocity = omega;
        angularPosition = angle;
        distance = dst;
        time = 1000;
    }

    public OrbitalProjectile(final BattleLevel lev, float dst, float cx, float cy, float omega, float angle, float life) {
        super(lev, 0, 0, 0, 0);
        center = new Vector2(cx, cy);
        angularVelocity = omega;
        angularPosition = angle;
        distance = dst;
        time = life;
    }

    @Override
    public void move(float x, float y, float frame) {
        angularPosition += angularVelocity;
        hitbox.x = center.x + MathUtils.cos(angularPosition) * distance;
        hitbox.y = center.y + MathUtils.sin(angularPosition) * distance;
        if (time < 0) {
            isDestroyed = true;
        }
        time -= frame;
    }

}
