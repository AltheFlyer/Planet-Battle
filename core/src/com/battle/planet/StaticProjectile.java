package com.battle.planet;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class StaticProjectile extends Projectile {

    float lifespan;

    public StaticProjectile(Rectangle r, float life) {
        super(r, new Vector2(0, 0));
        lifespan = life;
    }

    public StaticProjectile(float x, float y, float life) {
        super(x, y, 0, 0);
        lifespan = life;
    }

    @Override
    public void move(float x, float y, float frame) {
        lifespan -= frame;
        if (lifespan <= 0) {
            isDestroyed = true;
        }
    }
}
