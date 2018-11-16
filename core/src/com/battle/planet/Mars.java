package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Mars extends Enemy {

    //Controls how the boss acts
    int phase = 1;

    //Attack cooldowns and other related values
    final float SPEAR_MAX_COOLDOWN = 2.0f;
    float spearCooldown;


    final float CHARGE_MAX_COOLDOWN = 4.0f;
    float chargeCooldown;
    boolean inCharge = false; //If Mars is charging or not
    Vector2 chargeVelocity; //Direction of the charge
    Vector2 chargeDestination; //Where to stop charging

    final float BURST_MAX_COOLDOWN = 2.0f;
    float burstCooldown;

    //For phase two direction
    boolean isGoingLeft;

    Array<Projectile> bullets;

    public Mars() {
        collisionBox = new Circle(0, 200, 100);
        hitbox = new Circle(10, 210, 80);
        spearCooldown = SPEAR_MAX_COOLDOWN;
        chargeCooldown = CHARGE_MAX_COOLDOWN;
        burstCooldown = BURST_MAX_COOLDOWN;
        bullets = new Array<Projectile>();
        health = 1000;
    }

    /**
     * Draws the planet sprite/ shape render.
     * @param r
     */
    public void drawBody(ShapeRenderer r) {
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    /**
     * Draws other planet-based objects (warnings, alerts, animations)
     * @param r
     */
    public void drawObjects(ShapeRenderer r) {
        r.setColor(Color.YELLOW);
        if (inCharge) {
            r.rectLine(hitbox.x, hitbox.y, chargeDestination.x, chargeDestination.y, 5f);
        }
    }

    public Array<Projectile> attack(float x, float y, float frame) {
        bullets.clear();
        if (phase == 1) {
            spearCooldown -= frame;
            burstCooldown -= frame;
            //Don't use other attacks when charging
            if (!inCharge) {
                //Charge cooldown only decreases while not charging
                chargeCooldown -= frame;
                //Spear attack
                if (spearCooldown <= 0) {
                    createSpear(MathUtils.atan2(y - hitbox.y, x - hitbox.x));
                    spearCooldown = SPEAR_MAX_COOLDOWN;
                }
                //Burst attack
                if (burstCooldown <= 0) {
                    createSpread(MathUtils.atan2(y - hitbox.y, x - hitbox.x), 36, 360 * MathUtils.degreesToRadians);
                    burstCooldown = BURST_MAX_COOLDOWN;
                }
                //Start up charge attack
                if (chargeCooldown <= 0) {
                    chargeDestination = new Vector2(x, y);
                    float angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);
                    chargeVelocity = new Vector2(MathUtils.cos(angle) * 200, MathUtils.sin(angle) * 200);
                    inCharge = true;
                }
            } else {
                //Charge forward normally, unless it would move Mars past the target destination
                //The charge velocities are modified to account for trigonometric extremes
                if (Math.abs(chargeDestination.x - hitbox.x) >= (Math.abs(chargeVelocity.x) - 1f) * frame && Math.abs(chargeDestination.y - hitbox.y) >= (Math.abs(chargeVelocity.y) - 1f) * frame) {
                    hitbox.x += chargeVelocity.x * frame;
                    hitbox.y += chargeVelocity.y * frame;
                } else {
                    hitbox.x = chargeDestination.x;
                    hitbox.y = chargeDestination.y;
                    chargeCooldown = CHARGE_MAX_COOLDOWN;
                    inCharge = false;
                }
            }
        } else if (phase == 2) {
            //Set Mars y value to near top of screen
            hitbox.y = 550;
            if (isGoingLeft) {
                hitbox.x -= 200 * frame;
            } else {
                hitbox.x += 200 * frame;
            }
            //Change direction at screen edges
            if (hitbox.x < 0) {
                hitbox.x = 0;
                isGoingLeft = false;
            } else if (hitbox.x > 600) {
                hitbox.x = 600;
                isGoingLeft = true;
            }
            spearCooldown -= frame;
            burstCooldown -= frame;
            //Spear attack, always shot directly downwards
            if (spearCooldown <= 0) {
                createSpear(-1.57079633f, 600);
                spearCooldown = SPEAR_MAX_COOLDOWN / 2;
            }
            //Cool wave attack
            if (burstCooldown <= 0) {
                createWaveSpread(-1.57079633f, 7, 1.5708f, 20);
                burstCooldown = BURST_MAX_COOLDOWN * 5;
            }
        }
        //Transition to 2nd phase
        if (phase == 1 && health <= 800) {
            phase = -1;
            //Enters charge to get to top of screen
            chargeDestination.set(hitbox.x, 550);
            chargeVelocity.set(0, 200);
            inCharge = true;
        }
        //Transition between phase 1 and 2
        if (phase == -1) {
            if (Math.abs(chargeDestination.x - hitbox.x) >= (Math.abs(chargeVelocity.x) - 1f) * frame && Math.abs(chargeDestination.y - hitbox.y) >= (Math.abs(chargeVelocity.y) - 1f) * frame) {
                hitbox.x += chargeVelocity.x * frame;
                hitbox.y += chargeVelocity.y * frame;
            } else {
                phase = 2;
                hitbox.x = chargeDestination.x;
                hitbox.y = chargeDestination.y;
                inCharge = false;
            }
        }
        return bullets;
    }

    public void createSpear(float theta) {
        //Shaft
        for (int i = 0; i <= 80; i+=5) {
            bullets.add(new BasicProjectile(hitbox.x + i * MathUtils.cos(theta), hitbox.y + i * MathUtils.sin(theta), 180 * MathUtils.cos(theta), 180 * MathUtils.sin(theta)));
        }

        //Sides
        for (int i = 5; i <= 25; i+=5) {
            bullets.add(new BasicProjectile(hitbox.x + (80 - i) * MathUtils.cos(theta + i * 0.0174533f), hitbox.y + (80 - i) * MathUtils.sin(theta + i * 0.0174533f), 180 * MathUtils.cos(theta), 180 * MathUtils.sin(theta)));
            bullets.add(new BasicProjectile(hitbox.x + (80 - i) * MathUtils.cos(theta - i * 0.0174533f), hitbox.y + (80 - i) * MathUtils.sin(theta - i * 0.0174533f), 180 * MathUtils.cos(theta), 180 * MathUtils.sin(theta)));
        }
    }

    public void createSpear(float theta, float speed) {
        //Shaft
        for (int i = 0; i <= 80; i+=5) {
            bullets.add(new BasicProjectile(hitbox.x + i * MathUtils.cos(theta), hitbox.y + i * MathUtils.sin(theta), speed * MathUtils.cos(theta), speed * MathUtils.sin(theta)));
        }

        //Sides
        for (int i = 5; i <= 25; i+=5) {
            bullets.add(new BasicProjectile(hitbox.x + (80 - i) * MathUtils.cos(theta + i * 0.0174533f), hitbox.y + (80 - i) * MathUtils.sin(theta + i * 0.0174533f), speed * MathUtils.cos(theta), speed * MathUtils.sin(theta)));
            bullets.add(new BasicProjectile(hitbox.x + (80 - i) * MathUtils.cos(theta - i * 0.0174533f), hitbox.y + (80 - i) * MathUtils.sin(theta - i * 0.0174533f), speed * MathUtils.cos(theta), speed * MathUtils.sin(theta)));
        }
    }

    public void createSpread(float theta, int amount, float spread) {
        for (int i = 0; i < amount; ++i) {
            float angle = theta + ((float) i / amount) * spread - (spread / 2);;
            bullets.add(new BasicProjectile(hitbox.x, hitbox.y, MathUtils.cos(angle) * 180, MathUtils.sin(angle) * 180));
        }
    }

    public void createWaveSpread(float theta, int amount, float spread, float mo) {
        for (int i = 0; i < amount; ++i) {
            float angle = theta + ((float) i / amount) * spread - (spread / 2);;
            bullets.add(new WaveProjectile(hitbox.x, hitbox.y, MathUtils.cos(angle) * 180, MathUtils.sin(angle) * 180, mo));
        }
    }

    public Array<Projectile> collide(Array<Projectile> bullets) {
        for (Projectile p: bullets) {
            if (!p.isDestroyed && this.hitbox.contains(p.hitbox.x, p.hitbox.y)) {
                p.isDestroyed = true;
                this.health -= 1;
            }
        }
        return bullets;
    }
}
