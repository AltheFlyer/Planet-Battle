package com.battle.planet.projectiles;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.battle.planet.BattleLevel;

public class OrbitalProjectile extends Projectile {

    public Vector2 center;
    public Vector2 velocity;
    public float angularPosition;
    public float angularVelocity;
    public float distance;

    public OrbitalProjectile(final BattleLevel lev, float dst, float cx, float cy, float omega, float angle) {
        super(lev, 0, 0, 0, 0);
        center = new Vector2(cx, cy);
        velocity = new Vector2(0, 0);
        angularVelocity = omega;
        angularPosition = angle;
        distance = dst;
        time = 1000;
    }

    public OrbitalProjectile(final BattleLevel lev, float dst, float cx, float cy, float omega, float angle, float life) {
        super(lev, 0, 0, 0, 0);
        center = new Vector2(cx, cy);
        velocity = new Vector2(0, 0);
        angularVelocity = omega;
        angularPosition = angle;
        distance = dst;
        time = life;
    }

    public OrbitalProjectile(final BattleLevel lev, float dst, float cx, float cy, float vx, float vy, float omega, float angle) {
        super(lev, 0, 0, 0, 0);
        center = new Vector2(cx, cy);
        velocity = new Vector2(vx, vy);
        angularVelocity = omega;
        angularPosition = angle;
        distance = dst;
        time = 1000;
    }

    @Override
    public void move(float frame) {
        angularPosition += angularVelocity * frame;
        center.x += velocity.x * frame;
        center.y += velocity.y * frame;
        hitbox.x = center.x + MathUtils.cos(angularPosition) * distance;
        hitbox.y = center.y + MathUtils.sin(angularPosition) * distance;
        if (time < 0) {
            isDestroyed = true;
        }
        time -= frame;
    }

}
