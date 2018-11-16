package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class Phobos extends Enemy {

    final float MAX_BOLT_COOLDOWN = 2f;
    float boltCooldown;

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
        boltCooldown -= frame;
        if (boltCooldown <= 0) {

            boltCooldown = MAX_BOLT_COOLDOWN;
        }
        return bullets;
    }
}
