package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class VenusReflector extends Enemy {

    final Venus venus;

    float angularPosition;
    float angularVelocity;

    float distance;
    boolean immune;

    /**
     * @param dist
     * @param hp
     */
    public VenusReflector(final BattleLevel lev, Venus v, float dist, float hp, float theta, float omega, boolean i) {
        super(lev, 0, 0, 20, 20, hp);
        venus = v;
        distance = dist;
        angularPosition = theta;
        angularVelocity = omega;
        hitbox.x = venus.hitbox.x + MathUtils.cos(theta) * distance;
        hitbox.y = venus.hitbox.y + MathUtils.sin(theta) * distance;
        immune = i;
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GOLDENROD);
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public Array<Projectile> attack(float frame) {
        angularPosition += angularVelocity * frame;
        hitbox.x = venus.hitbox.x + MathUtils.cos(angularPosition) * distance;
        hitbox.y = venus.hitbox.y + MathUtils.sin(angularPosition) * distance;

        return bullets;
    }

    @Override
    public Array<Projectile> collide(Array<Projectile> projectiles) {
        bullets.clear();
        for (Projectile p: projectiles) {
            if (!p.isDestroyed && this.hitbox.contains(p.hitbox.x, p.hitbox.y)) {
                p.isDestroyed = true;
                if (!immune) {
                    this.health -= 1;
                }
                bullets.add(new BasicProjectile(level, p.hitbox.x, p.hitbox.y, -p.velocity.x, -p.velocity.y));
            }
        }
        return projectiles;
    }

    @Override
    public void drawHealthBars(ShapeRenderer r) {
        r.setColor(Color.valueOf("#68d9ff"));
        r.rect(hitbox.x - hitbox.radius, hitbox.y - hitbox.radius, hitbox.radius * 2, 10);
    }

}
