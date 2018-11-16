package com.battle.planet;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class DelayProjectile extends Projectile {

    float delay;

    public DelayProjectile(Rectangle r, Vector2 v) {
        super(r, v);
    }

    public DelayProjectile(float x, float y, float vx, float vy) {
        this(new Rectangle(x, y, 5, 5), new Vector2(vx, vy));
    }

}
