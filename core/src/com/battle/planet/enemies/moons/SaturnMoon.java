package com.battle.planet.enemies.moons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.BattleLevel;
import com.battle.planet.enemies.Enemy;
import com.battle.planet.enemies.Saturn;
import com.battle.planet.projectiles.Projectile;

public class SaturnMoon extends Enemy {

    public final Saturn saturn;
    public Vector2 velocity;
    //Position is based on the player
    float angularPosition = 0f;

    /**
     * @param s The saturn that the moon follows
     * @param lev the level that the moon is in
     * @param x The x coordinate to start at
     * @param y The y coordinate to start at
     * @param r0 The radius of the player-enemy hitbox
     * @param r1 The radius of the bullet-enemy hitbox
     */
    public SaturnMoon(Saturn s, BattleLevel lev, float x, float y, float r0, float r1, int hp) {
        super(lev, x, y, r0, r1, hp);
        saturn = s;
        velocity = new Vector2();
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GRAY);
        r.circle(this.hitbox.x, this.hitbox.y, this.hitbox.radius);
    }

    @Override
    public Array<Projectile> collide(Array<Projectile> projectiles) {
        for (Projectile p: projectiles) {
            if (!p.isDestroyed && this.hitbox.contains(p.hitbox.x, p.hitbox.y)) {
                p.isDestroyed = true;
                modHealth(-1);
            }
        }
        if (getHealth() <= 0) {
            saturn.activeMoons--;
        }
        return projectiles;
    }


    public void setVelocity(float x, float y) {
        velocity.set(x, y);
    }

    public void spinMove(float frame, float distance, float angularVelocity) {
        float x = saturn.getHitbox().x;
        float y = saturn.getHitbox().y;
        hitbox.x = x + MathUtils.cos(angularPosition) * distance;
        hitbox.y = y + MathUtils.sin(angularPosition) * distance;
        angularPosition += angularVelocity * frame;

            /*

            */
    }

    public void bounceMove(float frame) {
        this.hitbox.x += this.velocity.x * frame;
        this.hitbox.y += this.velocity.y * frame;
        if (this.hitbox.x - this.hitbox.radius < 0) {
            this.hitbox.x = this.hitbox.radius;
            this.velocity.x *= -1;
        }
        if (this.hitbox.x + this.hitbox.radius > getLevel().LEVEL_WIDTH) {
            this.hitbox.x = getLevel().LEVEL_WIDTH - this.hitbox.radius;
            this.velocity.x *= -1;
        }
        if (this.hitbox.y - this.hitbox.radius < 0) {
            this.hitbox.y = this.hitbox.radius;
            this.velocity.y *= -1;
        }
        if (this.hitbox.y + this.hitbox.radius > getLevel().LEVEL_HEIGHT) {
            this.hitbox.y = getLevel().LEVEL_HEIGHT - this.hitbox.radius;
            this.velocity.y *= -1;
        }
    }
}