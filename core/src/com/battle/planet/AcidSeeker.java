package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.projectiles.Projectile;

public class AcidSeeker extends Enemy {

    final Venus venus;
    float speed;

    //Small enemy, with a larger damage area
    //(Like an acid aura)

    public AcidSeeker(final BattleLevel lev, Venus v, float x, float y) {
        super(lev, x, y, 60, 20, 2);
        venus = v;
        speed = 150;
    }

    public AcidSeeker(final BattleLevel lev, Venus v, float x, float y, float s) {
        super(lev, x, y, 60, 20, 2);
        venus = v;
        speed = s;
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GRAY);
        r.circle(hitbox.x, hitbox.y, hitbox.radius - 8);
        r.setColor(120, 120, 0, 0.4f);
        r.circle(hitbox.x, hitbox.y, collisionBox.radius);
    }

    @Override
    public void drawObjects(ShapeRenderer r) {
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
        r.setColor(Color.GREEN);
        r.rectLine(hitbox.x, hitbox.y, venus.hitbox.x, venus.hitbox.y, 3);
    }

    @Override
    public Array<Projectile> attack(float frame) {
        float x = getPlayer().getCenterX();
        float y = getPlayer().getCenterY();
        float angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);
        hitbox.x += MathUtils.cos(angle) * speed * frame;
        hitbox.y += MathUtils.sin(angle) * speed * frame;
        return getBullets();
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
            venus.reduceSpawned();
        }
        return projectiles;
    }
}
