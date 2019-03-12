package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.projectiles.DelayProjectile;
import com.battle.planet.projectiles.Projectile;

public class Deimos extends Enemy {

    final Mars mars;
    //Bolt attack, creates a set of stationary bolts that
    //all aim at the player and launch at the same time.
    final float MAX_BOLT_COOLDOWN = 5f;
    float boltCooldown;
    float[] boltSet = {0.5f, 0.7f, 1.0f, 1.5f, 2.0f};

    //Angular values, in radians
    //Position is based on the player
    float angularPosition = 0f;
    //~50 degrees
    float angularVelocity = 0.872665f;

    public Deimos(final BattleLevel lev, Mars m, float x, float y) {
        super(lev, x, y, 30, 40, 80);
        mars = m;
        boltCooldown = MAX_BOLT_COOLDOWN;
    }

    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GRAY);
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public void drawObjects(ShapeRenderer r) {
        float x = player.hitboxCenter.x;
        float y = player.hitboxCenter.y;
        r.setColor(Color.YELLOW);
        r.set(ShapeRenderer.ShapeType.Line);
        r.circle(x, y, 280);
        r.set(ShapeRenderer.ShapeType.Filled);
    }

    @Override
    public Array<Projectile> attack(float frame) {
        float x = player.hitboxCenter.x;
        float y = player.hitboxCenter.y;
        bullets.clear();
        boltCooldown -= frame;
        /*
        for (int i = 0; i < boltSet.length; ++i) {
            if (boltCooldown <= boltSet[i]) {
                bullets.add(new DelayProjectile(level, new Rectangle(hitbox.x, hitbox.y, 15, 15), boltSet[i]));
                boltSet[i] = -1.0f;
            }
        }
        if (boltCooldown <= 0) {
            //Regenerate random bolt set
            float accumulator = 0;
            for (int i = 0; i < boltSet.length; ++i) {
                accumulator += MathUtils.random(0.2f, 0.5f);
                boltSet[i] = accumulator;
            }
            boltCooldown = MAX_BOLT_COOLDOWN;
        }
        */
        if (boltCooldown <= 0) {
            createSpread(MathUtils.atan2(y - hitbox.y, x - hitbox.x), 16, 360 * MathUtils.degreesToRadians);
            boltCooldown = MAX_BOLT_COOLDOWN/2;
        }

        //Movement
        hitbox.x = x + MathUtils.cos(angularPosition) * 280;
        hitbox.y = y + MathUtils.sin(angularPosition) * 280;
        angularPosition += angularVelocity * frame;

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
            mars.deimosDead = true;
        }
        return projectiles;
    }
}
