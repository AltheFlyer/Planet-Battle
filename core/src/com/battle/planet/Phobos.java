package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Phobos extends Enemy {

    final Mars mars;
    //Bolt attack, creates stationary bolts that
    //aim at the player after some time.
    final float MAX_BOLT_COOLDOWN = 2f;
    float boltCooldown;

    //Angular values, in radians
    //Position is based on the player
    float angularPosition = 0f;
    //~90 degrees
    float angularVelocity = 1.5708f;

    public Phobos(final BattleLevel lev, Mars m, float x, float y) {
        super(lev, x, y, 30, 50, 100);
        mars = m;
        boltCooldown = MAX_BOLT_COOLDOWN;
    }

    @Override
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
        r.circle(x, y, 170);
        r.set(ShapeRenderer.ShapeType.Filled);
    }

    @Override
    public Array<Projectile> attack(float frame) {
        float x = player.hitboxCenter.x;
        float y = player.hitboxCenter.y;
        bullets.clear();
        boltCooldown -= frame;
        if (boltCooldown <= 0) {
            bullets.add(new DelayProjectile(level, new Rectangle(hitbox.x, hitbox.y, 15, 15), 2f));
            boltCooldown = MAX_BOLT_COOLDOWN;
        }
        //Movement
        hitbox.x = x + MathUtils.cos(angularPosition) * 170;
        hitbox.y = y + MathUtils.sin(angularPosition) * 170;
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
            mars.phobosDead = true;
        }
        return projectiles;
    }
}
