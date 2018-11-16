package com.battle.planet;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BasicProjectile extends Projectile {

    public BasicProjectile(Rectangle r, Vector2 v) {
        super(r, v);
    }

    public BasicProjectile(float x, float y, float vx, float vy) {
        this(new Rectangle(x, y, 5, 5), new Vector2(vx, vy));
    }

    public void move(float frame) {
        hitbox.x += velocity.x * frame;
        hitbox.y += velocity.y * frame;
    }

    public void drawSpecial(ShapeRenderer r) {

    }
}
