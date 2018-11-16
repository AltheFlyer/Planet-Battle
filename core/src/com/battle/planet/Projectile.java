package com.battle.planet;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

abstract public class Projectile {

    Rectangle hitbox;
    Vector2 velocity;
    float time;
    boolean isDestroyed;

    public Projectile(Rectangle r, Vector2 v) {
        hitbox = r;
        velocity = v;
        isDestroyed = false;
        time = 0f;
    }

    public Projectile(float x, float y, float vx, float vy) {
        hitbox = new Rectangle(x, y, 5, 5);
        velocity = new Vector2(vx, vy);
        isDestroyed = false;
    }

    /**
     * Moves the projectile
     *
     * @param frame
     */
    public void move(float frame) {
        hitbox.x += velocity.x * frame;
        hitbox.y += velocity.y * frame;
        time += frame;
    }

    /**
     * Draws any special graphics for the projectile
     * @param r
     */
    public void drawSpecial(ShapeRenderer r) {

    }
}
