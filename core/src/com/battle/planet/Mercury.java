package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Mercury extends Enemy {

    Vector2 velocity;
    int phase;

    final float SPEED = 282.84f;
    float chaseSpeed = 0;
    //For 3rd phase
    int bulletHits = 0;
    int bulletHitsNeeded = 50;
    float startVel = 0;

    float passiveCooldown;
    final float PASSIVE_MAX_COOLDOWN = 0.04f;

    float aimCooldown;
    final float AIM_MAX_COOLDOWN = 0.3f;

    float attackModeCooldown;
    final float ATTACK_MODE_MAX_COOLDOWN = 2.0f;

    boolean inChaseMode = true;

    Array<Vector2> trail;

    public Mercury(float x, float y) {
        super(x, y, 50, 60, 700);
        velocity = new Vector2(200, 200);
        phase = 1;

        passiveCooldown = PASSIVE_MAX_COOLDOWN;
        aimCooldown = AIM_MAX_COOLDOWN;
        attackModeCooldown = ATTACK_MODE_MAX_COOLDOWN;

        trail = new Array<Vector2>();

        phaseMarkers.add(550);
        phaseMarkers.add(350);
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GRAY);
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    public void drawObjects(float x, float y, ShapeRenderer r) {
        //Indicators for where Mercury is charging from
        if (phase == 2) {
            r.setColor(Color.ORANGE);
            //Coming from left side
            if (hitbox.x < 100 && velocity.x > 0) {
                r.triangle(0, hitbox.y + 50,0,hitbox.y - 50,50, hitbox.y);
            //From the right
            } else if (hitbox.x > 500 && velocity.x < 0) {
                r.triangle(600, hitbox.y + 50,600,hitbox.y - 50,550, hitbox.y);
            //From the bottom
            } else if (hitbox.y < 100 && velocity.y > 0) {
                r.triangle(hitbox.x + 50, 0,hitbox.x - 50, 0, hitbox.x, 50);
            //From the top
            } else if (hitbox.y > 500 && velocity.y < 0) {
                r.triangle(hitbox.x + 50, 600,hitbox.x - 50, 600, hitbox.x, 550);
            }
        //Tracer trail
        } else if (phase == 3) {
            r.setColor(50, 50, 50, 0.05f);
            for (Vector2 v: trail) {
                r.circle(v.x, v.y, hitbox.radius);
            }
        }
    }

    public Array<Projectile> attack(float x, float y, float frame) {
        bullets.clear();
        if (phase == 1) {
            passiveCooldown -= frame;
            aimCooldown -= frame;
            if (passiveCooldown <= 0) {
                bullets.add(new TimeProjectile(
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
                    for (int i = 0; i < 2; ++i) {
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
        } else if (phase == 2) {
            passiveCooldown -= frame;
            aimCooldown -= frame;
            if (passiveCooldown <= 0) {
                float angle = MathUtils.random(0, MathUtils.PI * 2);
                bullets.add(new TimeProjectile(
                        hitbox.x,
                        hitbox.y,
                        MathUtils.cos(angle) * 200,
                        MathUtils.sin(angle) * 200,
                        0.5f));

                passiveCooldown = PASSIVE_MAX_COOLDOWN;
            }
            if (aimCooldown <= 0) {
                //Swap x and y velocities to make bullets perpendicular to Mercury's path
                if (MathUtils.randomBoolean()) {
                    bullets.add(new BasicProjectile(new Rectangle(hitbox.x, hitbox.y, 10, 10), new Vector2(velocity.y, velocity.x)));
                } else {
                    bullets.add(new BasicProjectile(new Rectangle(hitbox.x, hitbox.y, 10, 10), new Vector2(-velocity.y, -velocity.x)));
                }
                //Increase frequency of shots
                aimCooldown = AIM_MAX_COOLDOWN / 4;
            }
            //Movement
            hitbox.x += velocity.x * frame;
            hitbox.y += velocity.y * frame;
            //Off-screen looping
            if (hitbox.x > 750) {
                setDirection(false, true);
            } else if (hitbox.x < -150) {
                setDirection(true, true);
            } else if (hitbox.y > 750) {
                setDirection(false, false);
            } else if (hitbox.y < -150) {
                setDirection(true, false);
            }
        //Third phase: Chase mode
        } else if (phase == 3) {
            aimCooldown -= frame;
            //Trail additions
            trail.add(new Vector2(hitbox.x, hitbox.y));
            if (trail.size > 10) {
                trail.removeIndex(0);
            }

            float angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);
            if (inChaseMode) {
                hitbox.x += velocity.x * frame;
                hitbox.y += velocity.y * frame;
                velocity.x += MathUtils.cos(angle) * chaseSpeed / 15;
                velocity.y += MathUtils.sin(angle) * chaseSpeed / 15;
                velocity.nor().scl(chaseSpeed);

                if (chaseSpeed < SPEED * 1.5) {
                    chaseSpeed += (SPEED / 4) * frame;
                }
                //Takes knockback!
                if (bulletHits == bulletHitsNeeded) {
                    bulletHitsNeeded += 10;
                    velocity.x = MathUtils.cos(angle + 3.14159f) * chaseSpeed * 0.5f;
                    velocity.y = MathUtils.sin(angle + 3.14159f) * chaseSpeed * 0.5f;
                    chaseSpeed = velocity.dst(0, 0);
                    startVel = chaseSpeed;
                    bulletHits = 0;
                    inChaseMode = false;
                    attackModeCooldown = ATTACK_MODE_MAX_COOLDOWN;
                    createSpread(0, 36, 360 * MathUtils.degreesToRadians);
                }

                if (aimCooldown <= 0) {
                    bullets.add(new DelayProjectile(new Rectangle(hitbox.x, hitbox.y, 10, 10), 1));
                    aimCooldown = AIM_MAX_COOLDOWN;
                }

            } else {
                attackModeCooldown -= frame;
                hitbox.x += velocity.x * frame;
                hitbox.y += velocity.y * frame;
                if (chaseSpeed > 0) {
                    chaseSpeed -= startVel * frame;
                    velocity.nor().scl(chaseSpeed);
                } else {
                    chaseSpeed = 0;
                    velocity.x = 0;
                    velocity.y = 0;
                }
                //Enter chase mode again
                if (attackModeCooldown < 0) {
                    inChaseMode = true;
                    chaseSpeed = SPEED * 0.7f;
                    angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);
                    velocity.x  = MathUtils.cos(angle) * chaseSpeed;
                    velocity.y = MathUtils.sin(angle) * chaseSpeed;
                }
                //Shoots Caduceus-like projectiles
                if (aimCooldown <= 0) {
                    bullets.add(new WaveProjectile(
                            new Rectangle(hitbox.x, hitbox.y, 5, 5),
                            new Vector2(MathUtils.cos(angle) * 300, MathUtils.sin(angle) * 300),
                            30,
                            true));
                    bullets.add(new WaveProjectile(
                            new Rectangle(hitbox.x, hitbox.y, 5, 5),
                            new Vector2(MathUtils.cos(angle) * 300, MathUtils.sin(angle) * 300),
                            30,
                            false));
                    for (int i = -20; i <= 20; i+=5) {
                        bullets.add(new BasicProjectile(hitbox.x + i * MathUtils.cos(angle), hitbox.y + i * MathUtils.sin(angle), 300 * MathUtils.cos(angle), 300 * MathUtils.sin(angle)));
                    }
                    aimCooldown = AIM_MAX_COOLDOWN;
                }
            }
        }
        //Phase 1->2 transition
        if (phase == 1 && health <= 550) {
            phase = 2;
            velocity.x = SPEED;
            velocity.y = 0f;
        }
        if (phase == 2 && health <= 350) {
            phase = 3;
            chaseSpeed = SPEED / 3;
            float angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);
            velocity.x  = MathUtils.cos(angle) * chaseSpeed;
            velocity.y = MathUtils.sin(angle) * chaseSpeed;
        }

        return bullets;
    }

    public void randomBounce() {
        if (MathUtils.random(1, 6) == 6) {
            //Prevent angles too close to 0, 90, 180, 270 degrees
            float angle = MathUtils.random(0.523599f, 1.0472f) + (1.5708f * MathUtils.random(0, 3));
            velocity.x = MathUtils.cos(angle) * SPEED;
            velocity.y = MathUtils.sin(angle) * SPEED;
        }
    }

    /**
     *
     * @param isNegative Whether the ending position is negative or not
     * @param isX Whether the velocity is on the X axis or not.
     */
    public void setDirection(boolean isNegative, boolean isX) {
        float newSpeed = -SPEED;
        float placement = 700;

        boolean isExitNegative = MathUtils.randomBoolean();
        boolean isExitX = MathUtils.randomBoolean();

        //Prevent Mercury from coming out the same side they leave the screen
        if (isExitNegative == isNegative && isExitX == isX) {
            if (MathUtils.randomBoolean()) {
                isExitNegative = !isExitNegative;
            } else {
                isExitX = !isExitX;
            }
        }

        if (isExitNegative) {
            newSpeed *= -1;
            placement -= 800;
        }
        if (isExitX) {
            hitbox.x = placement;
            velocity.x = newSpeed;
            velocity.y = 0;
            hitbox.y = MathUtils.random(100, 500);
        } else {
            hitbox.y = placement;
            velocity.y = newSpeed;
            velocity.x = 0;
            hitbox.x = MathUtils.random(100, 500);
        }

    }

    @Override
    public Array<Projectile> collide(Array<Projectile> projectiles) {
        if (phase == 3) {
            for (Projectile p: projectiles) {
                if (!p.isDestroyed && this.hitbox.contains(p.hitbox.x, p.hitbox.y)) {
                    p.isDestroyed = true;
                    this.health -= 1;
                    bulletHits += 1;
                }
            }
            return projectiles;
        } else {
            return super.collide(projectiles);
        }
    }
}
