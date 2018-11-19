package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Mercury extends Enemy {

    Vector2 velocity;
    int phase;

    float passiveCooldown;
    final float PASSIVE_MAX_COOLDOWN = 0.04f;

    float aimCooldown;
    final float AIM_MAX_COOLDOWN = 0.3f;

    public Mercury(float x, float y) {
        super(x, y, 50, 60);
        health = 500;
        velocity = new Vector2(200, 200);
        phase = 1;

        passiveCooldown = PASSIVE_MAX_COOLDOWN;
        aimCooldown = AIM_MAX_COOLDOWN;
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GRAY);
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    public void drawObjects(float x, float y, ShapeRenderer r) {

    }

    public Array<Projectile> attack(float x, float y, float frame) {
        bullets.clear();
        if (phase == 1) {
            passiveCooldown -= frame;
            aimCooldown -= frame;
            if (passiveCooldown <= 0) {
                bullets.add(new StaticProjectile(
                        hitbox.x + MathUtils.random(0, hitbox.radius) * MathUtils.cos(MathUtils.random(0, MathUtils.PI * 2)),
                        hitbox.y + MathUtils.random(0, hitbox.radius) * MathUtils.sin(MathUtils.random(0, MathUtils.PI * 2)),
                        2.0f));
                passiveCooldown = PASSIVE_MAX_COOLDOWN;
            }

            if (aimCooldown <= 0) {
                float angle;
                //Normally shoot at player,
                //Occasionally create 2 extra random projectiles
                if (MathUtils.random(0, 2) == 2) {
                    for (int i = 0; i < 4; ++i) {
                        angle = MathUtils.random(0, MathUtils.PI * 2);
                        bullets.add(new WaveProjectile(hitbox.x, hitbox.y, MathUtils.cos(angle) * 300, MathUtils.sin(angle) * 300, 20));
                    }
                }
                angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);

                bullets.add(new WaveProjectile(hitbox.x, hitbox.y, MathUtils.cos(angle) * 300, MathUtils.sin(angle) * 300, 20));
                aimCooldown = AIM_MAX_COOLDOWN;
            }

            //Movement
            hitbox.x += velocity.x * frame;
            hitbox.y += velocity.y * frame;
            //Bounces
            if (hitbox.x < 0) {
                hitbox.x = 1;
                velocity.x *= -1;
                randomBounce();
            }
            if (hitbox.x > 600) {
                hitbox.x = 599;
                velocity.x *= -1;
                randomBounce();
            }
            if (hitbox.y < 0) {
                hitbox.y = 1;
                velocity.y *= -1;
                randomBounce();
            }
            if (hitbox.y > 600) {
                hitbox.y = 599;
                velocity.y *= -1;
                randomBounce();
            }
        }
        return bullets;
    }

    public void randomBounce() {
        if (MathUtils.random(1, 6) == 6) {
            //Prevent angles too close to 0, 90, 180, 270 degrees
            float angle = MathUtils.random(0.523599f, 1.0472f) + (1.5708f * MathUtils.random(0, 3));
            velocity.x = MathUtils.cos(angle) * 282.84f;
            velocity.y = MathUtils.sin(angle) * 282.84f;
        }
    }
}
