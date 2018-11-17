package com.battle.planet;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class DelayProjectile extends Projectile {

    float delay;
    boolean moving = false;

    public DelayProjectile(Rectangle r, float d) {
        super(r, new Vector2(0, 0));
        delay = d;
    }

    public DelayProjectile(float x, float y, float d) {
        this(new Rectangle(x, y, 5, 5), d);
    }

    @Override
    public void move(float x, float y, float frame) {
        if (delay > 0) {
            delay -= frame;
        }
        if (delay <= 0 && !moving) {
            moving = true;
            float angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);
            velocity = new Vector2(MathUtils.cos(angle) * 200, MathUtils.sin(angle) * 200);
        }
        if (moving) {
            super.move(frame);
        }

    }
}
