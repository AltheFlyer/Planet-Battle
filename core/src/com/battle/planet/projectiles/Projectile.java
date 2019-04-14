package com.battle.planet.projectiles;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.battle.planet.BattleLevel;

abstract public class Projectile {

    public Rectangle hitbox;
    public Vector2 velocity;
    public float time;
    public boolean isDestroyed;
    public final BattleLevel level;

    public Projectile(final BattleLevel lev, Rectangle r, Vector2 v) {
        hitbox = r;
        velocity = v;
        isDestroyed = false;
        time = 0f;
        level = lev;
    }

    public Projectile(final BattleLevel lev, float x, float y, float vx, float vy) {
        hitbox = new Rectangle(x, y, 5, 5);
        velocity = new Vector2(vx, vy);
        isDestroyed = false;
        level = lev;
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

    public void move(float x, float y, float frame) {
        this.move(frame);
    }

    /**
     * Draws any special graphics for the projectile
     * @param r
     */
    public void drawSpecial(ShapeRenderer r) {

    }
}