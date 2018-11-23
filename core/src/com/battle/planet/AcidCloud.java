package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import jdk.nashorn.internal.ir.PropertyKey;

public class AcidCloud extends Enemy {

    //Small enemy, with a larger damage area
    //(Like an acid aura)

    public AcidCloud(float x, float y) {
        super(x, y, 60, 12, 2);
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GRAY);
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
        r.setColor(120, 120, 0, 0.4f);
        r.circle(hitbox.x, hitbox.y, collisionBox.radius);
    }

    @Override
    public Array<Projectile> attack(float x, float y, float frame) {
        float angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);
        hitbox.x += MathUtils.cos(angle) * 150 * frame;
        hitbox.y += MathUtils.sin(angle) * 150 * frame;
        return bullets;
    }
}
