package com.battle.planet;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class TimeProjectile extends Projectile {

    float lifespan;

    public TimeProjectile(Rectangle r, float life) {
        super(r, new Vector2(0, 0));
        lifespan = life;
    }

    public TimeProjectile(float x, float y, float life) {
        super(x, y, 0, 0);
        lifespan = life;
    }

    public TimeProjectile(Rectangle r, Vector2 v, float life) {
        super(r, v);
        lifespan = life;
    }

    public TimeProjectile(float x, float y, float vx, float vy, float life) {
        super(x, y, vx, vy);
        lifespan = life;
    }

    @Override
    public void move(float x, float y, float frame) {
        lifespan -= frame;
        if (lifespan <= 0) {
            isDestroyed = true;
        }
        hitbox.x += velocity.x * frame;
        hitbox.y += velocity.y * frame;
    }
}
