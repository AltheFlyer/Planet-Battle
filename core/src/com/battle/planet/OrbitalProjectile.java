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
    }

    public OrbitalProjectile(final BattleLevel lev, float x, float y, float cx, float cy, float omega, float angle) {
        super(lev, x, y, 0, 0);
        center = new Vector2(cx, cy);
        angularVelocity = omega;
        angularPosition = angle;
        distance = center.dst(x, y);
    }

    @Override
    public void move(float x, float y, float frame) {
        angularPosition += angularVelocity;
        hitbox.x = center.x + MathUtils.cos(angularPosition) * distance;
        hitbox.y = center.y + MathUtils.sin(angularPosition) * distance;
    }

}
