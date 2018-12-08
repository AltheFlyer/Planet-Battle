package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class VenusShield extends Enemy {

    final Venus venus;
    float angularPosition; // Relative to venus
    float angularVelocity;
    float distance; //Relative to venus

    float aimCooldown;
    final float MAX_AIM_COOLDOWN = 1f;

    public VenusShield(float r, float theta, float omega, Venus v) {
        super(0, 0, 20, 20, 25);
        venus = v;

        distance = r;
        angularPosition = theta;
        angularVelocity = omega;

        aimCooldown = MAX_AIM_COOLDOWN;
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GOLDENROD);
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public Array<Projectile> attack(final Player player, float frame) {
        float x = player.hitboxCenter.x;
        float y = player.hitboxCenter.y;
        bullets.clear();
        aimCooldown -= frame;
        if (aimCooldown <= 0) {
            float angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);
            bullets.add(new BasicProjectile(hitbox.x, hitbox.y, MathUtils.cos(angle) * 200, MathUtils.sin(angle) * 200));
            aimCooldown = MAX_AIM_COOLDOWN;
        }

        angularPosition += angularVelocity * frame;
        hitbox.x = venus.hitbox.x + MathUtils.cos(angularPosition) * distance;
        hitbox.y = venus.hitbox.y + MathUtils.sin(angularPosition) * distance;

        return bullets;
    }

    @Override
    public Array<Projectile> collide(Array<Projectile> projectiles) {
        for (Projectile p: projectiles) {
            if (!p.isDestroyed && this.hitbox.contains(p.hitbox.x, p.hitbox.y)) {
                p.isDestroyed = true;
                this.health -= 1;
            }
        }
        if (health <= 0) {
            venus.spawned -= 1;
        }
        return projectiles;
    }
}
