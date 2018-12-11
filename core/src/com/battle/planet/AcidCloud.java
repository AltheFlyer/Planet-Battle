package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class AcidCloud extends Enemy {

    //Small enemy, with a larger damage area
    //(Like an acid aura)
    Vector2 velocity;

    public AcidCloud(float x, float y, float vx, float vy) {
        super(x, y, 60, 20, 2);
        velocity = new Vector2(vx, vy);
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GRAY);
        r.circle(hitbox.x, hitbox.y, hitbox.radius - 8);
        r.setColor(120, 120, 0, 0.4f);
        r.circle(hitbox.x, hitbox.y, collisionBox.radius);
    }

    @Override
    public void drawObjects(final Player player, ShapeRenderer r) {
        r.setColor(Color.ORANGE);
        //Coming from left side
        if (hitbox.x < 10) {
            r.triangle(0, hitbox.y + 20,0,hitbox.y - 20,10, hitbox.y);
            //From the right
        } else if (hitbox.x > 590) {
            r.triangle(600, hitbox.y + 20,600,hitbox.y - 20,590, hitbox.y);
            //From the bottom
        } else if (hitbox.y < 10) {
            r.triangle(hitbox.x + 20, 0,hitbox.x - 20, 0, hitbox.x, 10);
            //From the top
        } else if (hitbox.y > 590) {
            r.triangle(hitbox.x + 20, 600,hitbox.x - 20, 600, hitbox.x, 590);
        }
    }

    @Override
    public Array<Projectile> attack(final Player player, float frame) {
        hitbox.x += velocity.x * frame;
        hitbox.y += velocity.y * frame;
        //Destroy when too far offscreen
        if (hitbox.x < -150 || hitbox.x >= 750 || hitbox.y < -150 || hitbox.y > 750) {
            health = 0;
        }
        return bullets;
    }

}
