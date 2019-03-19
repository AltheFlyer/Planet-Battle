package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.projectiles.BasicProjectile;
import com.battle.planet.projectiles.Projectile;

public class Mars extends Enemy {

    //Controls how the boss acts
    int phase = 1;

    //Attack cooldowns and other related values
    //Cooldown for spear based attacks:
    //First phase: Aimed spears
    //Second phase: Downward spears
    final float SPEAR_MAX_COOLDOWN = 2.0f;
    float spearCooldown;
    int spearCount = 0; //For Third phase

    //Cooldown for charging attack:
    //First phase: Aimed charge
    //First->Second phase: Charge towards top of screen
    final float CHARGE_MAX_COOLDOWN = 4.0f;
    float chargeCooldown;
    boolean inCharge = false; //If Mars is charging or not
    Vector2 chargeVelocity; //Direction of the charge
    Vector2 chargeDestination; //Where to stop charging
    int bounces = 0; //For Third phase

    //Cooldown for burst attack:
    //First phase: Radial burst
    //Second phase: Wavy projectile wave shot downwards
    final float BURST_MAX_COOLDOWN = 2.0f;
    float burstCooldown;

    //For phase two direction
    boolean isGoingLeft;

    //EXPERIMENTAL
    boolean deimosDead = false;
    boolean phobosDead = false;

    public Mars(final BattleLevel lev, float x, float y) {
        super(lev, x, y, 70, 80, 500);
        spearCooldown = SPEAR_MAX_COOLDOWN;
        chargeCooldown = CHARGE_MAX_COOLDOWN;
        burstCooldown = BURST_MAX_COOLDOWN;
        phaseMarkers.add(300);
        chargeDestination = new Vector2(x, y);
        chargeVelocity = new Vector2(0, 0);
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        if (phase == 2 || phase == -1) {
            r.setColor(Color.valueOf("6C1F1EFF"));
        } else if (bounces == 2) {
            r.setColor(Color.valueOf("FF4D00"));
        } else if (bounces == 3) {
            r.setColor(Color.valueOf("FF7700"));
        } else if (bounces == 4) {
            r.setColor(Color.ORANGE);
        } else {
            r.setColor(Color.RED);
        }
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public void drawObjects(ShapeRenderer r) {
        r.setColor(Color.YELLOW);
        if (inCharge) {
            r.rectLine(hitbox.x, hitbox.y, chargeDestination.x, chargeDestination.y, 5f);
        }
    }

    @Override
    public Array<Projectile> attack(float frame) {
        float x = player.hitboxCenter.x;
        float y = player.hitboxCenter.y;
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
                    chargeDestination.set(x, y);
                    float angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);
                    chargeVelocity = new Vector2(MathUtils.cos(angle) * 200, MathUtils.sin(angle) * 200);
                    inCharge = true;
                }
            } else {
                //Charge forward normally, unless it would move Mars past the target destination
                //The charge velocities are modified to account for trigonometric extremes
                if (Math.abs(chargeDestination.x - hitbox.x) >= (Math.abs(chargeVelocity.x) - 1f) * frame && Math.abs(chargeDestination.y - hitbox.y) >= (Math.abs(chargeVelocity.y) - 1f) * frame) {
                    hitbox.x += chargeVelocity.x * frame*1.2;
                    hitbox.y += chargeVelocity.y * frame*1.2;
                } else {
                    hitbox.x = chargeDestination.x;
                    hitbox.y = chargeDestination.y;
                    chargeCooldown = CHARGE_MAX_COOLDOWN/4;
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

            if (deimosDead && phobosDead) {
                phase =-2;
            }
        } else if (phase == 3) {
            spearCooldown -= frame;
            if (spearCooldown <= 0) {
                createSpear(MathUtils.atan2(y - hitbox.y, x - hitbox.x), 275);
                spearCount += 1;
                if (spearCount > MathUtils.random(5, 8)) {
                    spearCooldown = SPEAR_MAX_COOLDOWN;
                    spearCount = 0;
                } else {
                    spearCooldown = MathUtils.random(0.6f, 0.8f);
                }
            }
            hitbox.x += chargeVelocity.x * frame*0.9;
            hitbox.y += chargeVelocity.y * frame*0.9;
            if (hitbox.x < 0) {
                hitbox.x = 1;
                chargeVelocity.x *= -1.2;
                chargeVelocity.y *= 1.2;
                bounces += 1;
                createSpread(0, 30, 360 * MathUtils.degreesToRadians);
            }
            if (hitbox.x > 600) {
                hitbox.x = 599;
                chargeVelocity.x *= -1.2;
                chargeVelocity.y *= 1.2;
                bounces += 1;
                createSpread(3.14159f, 30, 360 * MathUtils.degreesToRadians);
            }
            if (hitbox.y < 0) {
                hitbox.y = 1;
                chargeVelocity.x *= 1.2;
                chargeVelocity.y *= -1.2;
                bounces += 1;
                createSpread(1.5708f, 30, 360 * MathUtils.degreesToRadians);
            }
            if (hitbox.y > 600) {
                hitbox.y = 599;
                chargeVelocity.x *= 1.2;
                chargeVelocity.y *= -1.2;
                bounces += 1;
                createSpread(4.71239f, 30, 360 * MathUtils.degreesToRadians);
            }
            if (bounces >= 5) {
                bounces = 0;
                float angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);
                chargeVelocity.set(MathUtils.cos(angle) * 250, MathUtils.sin(angle) * 250);
                createSpread(4.71239f, 25, 360 * MathUtils.degreesToRadians, 210);
                createSpread(4.71239f, 35, 360 * MathUtils.degreesToRadians, 150);
            }
        }
        //Transition to 2nd phase
        if (phase == 1 && health <= 300) {
            phase = -1;
            //Enters charge to get to top of screen
            chargeDestination.set(hitbox.x, 550);
            chargeVelocity.set(0, 200);
            inCharge = true;
            canSpawn = true;
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
        //Transition to phase 3
        if (phase == -2) {
            phase = 3;
            spearCooldown = SPEAR_MAX_COOLDOWN;
            chargeCooldown = CHARGE_MAX_COOLDOWN;
            float angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);
            chargeVelocity.set(MathUtils.cos(angle) * 250, MathUtils.sin(angle) * 250);
        }
        return bullets;
    }

    @Override
    public Array<Enemy> spawn(float frame) {
        float x = player.hitboxCenter.x;
        float y = player.hitboxCenter.y;
        Array<Enemy> e = new Array<Enemy>();
        e.add(new Phobos(level, this, x, y));
        e.add(new Deimos(level, this, x, y));
        canSpawn = false;
        return e;
    }

    @Override
    public void drawHealthBars(ShapeRenderer r) {
        //Invincibility gets different colored healthbar
        if (phase == 2 || phase == -1) {
            r.setColor(Color.DARK_GRAY);
            r.rect(hitbox.x - hitbox.radius, hitbox.y - hitbox.radius, hitbox.radius * 2, 10);
            r.setColor(Color.valueOf("#68d9ff"));
            r.rect(hitbox.x - hitbox.radius, hitbox.y - hitbox.radius, hitbox.radius * 2 * (health / MAX_HEALTH), 10);
        } else {
            super.drawHealthBars(r);
        }
    }

    public void createSpear(float theta) {
        //Shaft
        for (int i = 0; i <= 80; i+=5) {
            bullets.add(new BasicProjectile(level, hitbox.x + i * MathUtils.cos(theta), hitbox.y + i * MathUtils.sin(theta), 180 * MathUtils.cos(theta), 180 * MathUtils.sin(theta)));
        }

        //Sides
        for (int i = 5; i <= 25; i+=5) {
            bullets.add(new BasicProjectile(level, hitbox.x + (80 - i) * MathUtils.cos(theta + i * 0.0174533f), hitbox.y + (80 - i) * MathUtils.sin(theta + i * 0.0174533f), 180 * MathUtils.cos(theta), 180 * MathUtils.sin(theta)));
            bullets.add(new BasicProjectile(level, hitbox.x + (80 - i) * MathUtils.cos(theta - i * 0.0174533f), hitbox.y + (80 - i) * MathUtils.sin(theta - i * 0.0174533f), 180 * MathUtils.cos(theta), 180 * MathUtils.sin(theta)));
        }
    }

    public void createSpear(float theta, float speed) {
        //Shaft
        for (int i = 0; i <= 80; i+=5) {
            bullets.add(new BasicProjectile(level, hitbox.x + i * MathUtils.cos(theta), hitbox.y + i * MathUtils.sin(theta), speed * MathUtils.cos(theta), speed * MathUtils.sin(theta)));
        }

        //Sides
        for (int i = 5; i <= 25; i+=5) {
            bullets.add(new BasicProjectile(level, hitbox.x + (80 - i) * MathUtils.cos(theta + i * 0.0174533f), hitbox.y + (80 - i) * MathUtils.sin(theta + i * 0.0174533f), speed * MathUtils.cos(theta), speed * MathUtils.sin(theta)));
            bullets.add(new BasicProjectile(level, hitbox.x + (80 - i) * MathUtils.cos(theta - i * 0.0174533f), hitbox.y + (80 - i) * MathUtils.sin(theta - i * 0.0174533f), speed * MathUtils.cos(theta), speed * MathUtils.sin(theta)));
        }
    }

    @Override
    public Array<Projectile> collide(Array<Projectile> projectiles) {
        if (phase != 2 && phase != -1) {
            for (Projectile p : projectiles) {
                if (!p.isDestroyed && this.hitbox.contains(p.hitbox.x, p.hitbox.y)) {
                    p.isDestroyed = true;
                    this.health -= 1;
                }
            }
        }
        return projectiles;
    }
}
