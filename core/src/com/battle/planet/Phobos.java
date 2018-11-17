package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Phobos extends Enemy {

    //Bolt attack, creates stationary bolts that
    //aim at the player after some time.
    final float MAX_BOLT_COOLDOWN = 2f;
    float boltCooldown;

    //Angular values, in radians
    //Position is based on the player
    float angularPosition = 0f;
    //~90 degrees
    float angularVelocity = 1.5708f;

    public Phobos(float x, float y) {
        super(x, y, 30, 50);
        health = 100;
        boltCooldown = MAX_BOLT_COOLDOWN;
    }

    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GRAY);
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public Array<Projectile> attack(float x, float y, float frame) {
        bullets.clear();
        boltCooldown -= frame;
        if (boltCooldown <= 0) {
            bullets.add(new DelayProjectile(new Rectangle(hitbox.x, hitbox.y, 15, 15), 2f));
            boltCooldown = MAX_BOLT_COOLDOWN;
        }
        //Movement
        hitbox.x = x + MathUtils.cos(angularPosition) * 170;
        hitbox.y = y + MathUtils.sin(angularPosition) * 170;
        angularPosition += angularVelocity * frame;

        return bullets;
    }
}
