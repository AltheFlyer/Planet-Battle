package com.battle.planet.enemies.moons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.battle.planet.BattleLevel;
import com.battle.planet.enemies.Enemy;
import com.battle.planet.enemies.Saturn;

public class SaturnMoon extends Enemy {

    public final Saturn saturn;
    public Vector2 velocity;
    //Position is based on the player
    float angularPosition = 0f;

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
            */
    }
}