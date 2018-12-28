package com.battle.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Mercury extends Enemy {

    Vector2 velocity;
    int phase;

    //Controls the speed of Mercury
    final float SPEED = 282.84f;
    float chaseSpeed = 0;

    //For 3rd phase to control transitions to/from chase mode
    int bulletHits = 0;
    int bulletHitsNeeded = 50;
    float startVel = 0;

    //Passive Attack Cooldown:
    //First Phase: Leaves behind a temporary static trail of bullets
    //Second Phase: Spews out temporary bullets in a short range
    //Third Phase: Does nothing
    float passiveCooldown;
    final float PASSIVE_MAX_COOLDOWN = 0.04f;

    //For attacks that are aimed
    //First Phase: Aimed wave projectiles, sometimes launches 2 more in random directions
    //Second Phase: Launches projectiles perpendicular to Mercury's path
    //Third Phase: Leaves behind a trail of delay bullets in chase mode
    //Launches caduceus shots when not in chase mode
    float aimCooldown;
    final float AIM_MAX_COOLDOWN = 0.3f;

    //Controls 'attack mode' for third phase
    float attackModeCooldown;
    final float ATTACK_MODE_MAX_COOLDOWN = 2.0f;

    //Controls whether Mercury is chasing or attacking in phase 3
    boolean inChaseMode = true;

    //Trail for visual effects during chase mode
    Array<Vector2> trail;

    public Mercury(final BattleLevel lev, float x, float y) {
        super(lev, x, y, 50, 60, 700);
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

    @Override
    public void drawObjects(ShapeRenderer r) {
        float x = player.hitboxCenter.x;
        float y = player.hitboxCenter.y;

        //Indicators for where Mercury is charging from
        if (phase == 2) {
            r.setColor(255, 165, 0, 0.2f);
            //Coming from left side
            for (int i = 0; i < 600; i += 50) {
                if (hitbox.x < 600 && velocity.x > 0) {
                    r.triangle(i, hitbox.y + 50, i, hitbox.y - 50, i + 50, hitbox.y);
                    //From the right
                } else if (hitbox.x > 0 && velocity.x < 0) {
                    r.triangle(600 - i, hitbox.y + 50, 600 - i, hitbox.y - 50, 550 - i, hitbox.y);
                    //From the bottom
                } else if (hitbox.y < 600 && velocity.y > 0) {
                    r.triangle(hitbox.x + 50, i, hitbox.x - 50, i, hitbox.x, i + 50);
                    //From the top
                } else if (hitbox.y > 0 && velocity.y < 0) {
                    r.triangle(hitbox.x + 50, 600 - i, hitbox.x - 50, 600 - i, hitbox.x, 550 - i);
                }
            }
        //Tracer trail
        } else if (phase == 3) {
            r.setColor(50, 50, 50, 0.05f);
            for (Vector2 v: trail) {
                r.circle(v.x, v.y, hitbox.radius);
            }
        }
    }

    @Override
    public Array<Projectile> attack(float frame) {
        float x = player.hitboxCenter.x;
        float y = player.hitboxCenter.y;
        bullets.clear();
        if (phase == 1) {
            passiveCooldown -= frame;
            aimCooldown -= frame;
            //Leave behind trail of static bullets
            if (passiveCooldown <= 0) {
                bullets.add(new TimeProjectile(
                        level,
                        hitbox.x + MathUtils.random(0, hitbox.radius) * MathUtils.cos(MathUtils.random(0, MathUtils.PI * 2)),
                        hitbox.y + MathUtils.random(0, hitbox.radius) * MathUtils.sin(MathUtils.random(0, MathUtils.PI * 2)),
                        2.0f));
                passiveCooldown = PASSIVE_MAX_COOLDOWN;
            }
            //Launch wave projectiles
            if (aimCooldown <= 0) {
                float angle;
                //Normally shoot at player,
                //Occasionally create 2 extra random projectiles
                if (MathUtils.random(0, 2) == 2) {
                    for (int i = 0; i < 2; ++i) {
                        angle = MathUtils.random(0, MathUtils.PI * 2);
                        bullets.add(new WaveProjectile(level, hitbox.x, hitbox.y, MathUtils.cos(angle) * 300, MathUtils.sin(angle) * 300, 20));
                    }
                }
                angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);

                bullets.add(new WaveProjectile(level, hitbox.x, hitbox.y, MathUtils.cos(angle) * 300, MathUtils.sin(angle) * 300, 20));
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
            //Spew out short range projectiles
            if (passiveCooldown <= 0) {
                float angle = MathUtils.random(0, MathUtils.PI * 2);
                bullets.add(new TimeProjectile(
                        level,
                        hitbox.x,
                        hitbox.y,
                        MathUtils.cos(angle) * 200,
                        MathUtils.sin(angle) * 200,
                        0.5f));

                passiveCooldown = PASSIVE_MAX_COOLDOWN;
            }
            //Create trail of perpendicular projectiles
            if (aimCooldown <= 0) {
                //Swap x and y velocities to make bullets perpendicular to Mercury's path
                if (MathUtils.randomBoolean()) {
                    bullets.add(new BasicProjectile(level, new Rectangle(hitbox.x, hitbox.y, 8, 8), new Vector2(velocity.y, velocity.x)));
                } else {
                    bullets.add(new BasicProjectile(level, new Rectangle(hitbox.x, hitbox.y, 8, 8), new Vector2(-velocity.y, -velocity.x)));
                }
                //Increases frequency of shots compared to phase 1
                aimCooldown = AIM_MAX_COOLDOWN / 4;
            }
            //Movement
            //Speed scales based on missing health
            hitbox.x += (1.5 - ((health - 350) / 200.0) * 0.5) * velocity.x * frame;
            hitbox.y += (1.5 - ((health - 350) / 200.0) * 0.5) * velocity.y * frame;
            //Off-screen looping
            if (hitbox.x > 750) {
                setDirection(false, true, x, y);
            } else if (hitbox.x < -150) {
                setDirection(true, true, x, y);
            } else if (hitbox.y > 750) {
                setDirection(false, false, x, y);
            } else if (hitbox.y < -150) {
                setDirection(true, false, x, y);
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
                //Slight homing effect
                velocity.x += MathUtils.cos(angle) * chaseSpeed / 15;
                velocity.y += MathUtils.sin(angle) * chaseSpeed / 15;
                //Prevent overspeeding
                velocity.nor().scl(chaseSpeed);

                //Gradually increase maximum speed
                if (chaseSpeed < SPEED * 1.5) {
                    chaseSpeed += (SPEED / 4) * frame;
                }

                //Takes knockback!
                if (bulletHits == bulletHitsNeeded) {
                    bulletHitsNeeded += 10;
                    //Reverse velocity
                    velocity.x = MathUtils.cos(angle + 3.14159f) * chaseSpeed * 0.5f;
                    velocity.y = MathUtils.sin(angle + 3.14159f) * chaseSpeed * 0.5f;
                    chaseSpeed = velocity.dst(0, 0);
                    //This value is used to save the original speed at the beginning of attack mode
                    startVel = chaseSpeed;
                    bulletHits = 0;
                    inChaseMode = false;
                    attackModeCooldown = ATTACK_MODE_MAX_COOLDOWN;
                    //Surprise bullet wave
                    createSpread(0, 36, 360 * MathUtils.degreesToRadians);
                }
                //Constantly shoot delay projectiles
                if (aimCooldown <= 0) {
                    bullets.add(new DelayProjectile(level, new Rectangle(hitbox.x, hitbox.y, 10, 10), 1));
                    aimCooldown = AIM_MAX_COOLDOWN;
                }
            } else {
                attackModeCooldown -= frame;
                hitbox.x += velocity.x * frame;
                hitbox.y += velocity.y * frame;
                //Decelerate Mercury to a halt
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
                            level,
                            new Rectangle(hitbox.x, hitbox.y, 5, 5),
                            new Vector2(MathUtils.cos(angle) * 300, MathUtils.sin(angle) * 300),
                            30,
                            true));
                    bullets.add(new WaveProjectile(
                            level,
                            new Rectangle(hitbox.x, hitbox.y, 5, 5),
                            new Vector2(MathUtils.cos(angle) * 300, MathUtils.sin(angle) * 300),
                            30,
                            false));
                    for (int i = -20; i <= 20; i+=5) {
                        bullets.add(new BasicProjectile(level, hitbox.x + i * MathUtils.cos(angle), hitbox.y + i * MathUtils.sin(angle), 300 * MathUtils.cos(angle), 300 * MathUtils.sin(angle)));
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
    public void setDirection(boolean isNegative, boolean isX, float x, float y) {
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
            if (hitbox.y - y < 30) {
                hitbox.y += 30;
            } else if (hitbox.y - y > -30) {
                hitbox.y -= 30;
            }
        } else {
            hitbox.y = placement;
            velocity.y = newSpeed;
            velocity.x = 0;
            hitbox.x = MathUtils.random(100, 500);
            if (hitbox.x - x < 30) {
                hitbox.x += 30;
            } else if (hitbox.x - x > -30) {
                hitbox.x -= 30;
            }
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
