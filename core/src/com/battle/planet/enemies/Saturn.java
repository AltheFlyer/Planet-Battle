package com.battle.planet.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.BattleLevel;
import com.battle.planet.projectiles.BasicProjectile;
import com.battle.planet.projectiles.ControlledAccelerateProjectile;
import com.battle.planet.projectiles.Projectile;
import com.battle.planet.projectiles.TimeProjectile;

public class Saturn extends Enemy {

    private int moonsLeft = 53;//or 62 including unnamed.
    private int phase = 0;

    //float ringCooldown = 0;
    //float RING_MAX_COOLDOWN = 10;


    final float BEAM_MAX_COOLDOWN = 2f;
    float beamCooldown = BEAM_MAX_COOLDOWN * 2;

    float ringAngle = 0;

    float timeDelta;
    float timeMultiplier = 1;

    float clockTimer = 0;

    public Saturn(BattleLevel lev, float x, float y) {
        super(lev, x, y, 100, 120, 2500);
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GOLDENROD);
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public Array<Projectile> attack(float frame) {
        clearProjectiles();
        timeDelta = frame * timeMultiplier;
        clockTimer -= frame;
        if (clockTimer <= 0) {
            if (timeMultiplier == 2) {
                timeMultiplier = 1;
                clockTimer = 10;
            } else {
                timeMultiplier = 2;
                clockTimer = 3;
            }

        }

        //Start of battle initializer
        if (phase == 0) {
            setCanSpawn(true);
            //Outer rings
            for (int k = 0; k < 240; k += 20) {
                for (int i = 0; i < 360; ++i) {
                    float theta = i * MathUtils.degreesToRadians;
                    addProjectile(new BasicProjectile(getLevel(), 600 + (600 + k) * MathUtils.cos(theta), 600 + (600 + k) * MathUtils.sin(theta), 0, 0));
                }
            }
        }

        if (phase == 1) {
            //Rotating rings
            ringAngle += frame * 0.4;
            for (int i = 0; i < 24; ++i) {
                float theta = i * 15 * MathUtils.degreesToRadians;

                addProjectile(new TimeProjectile(getLevel(), hitbox.x + 360 * MathUtils.cos(ringAngle) + 240 * MathUtils.cos(theta), hitbox.y + 360 * MathUtils.sin(ringAngle) + 240 * MathUtils.sin(theta), 0.07f));
                addProjectile(new TimeProjectile(getLevel(), hitbox.x + 360 * MathUtils.cos(ringAngle + (2/3.0f) * MathUtils.PI) + 240 * MathUtils.cos(theta), hitbox.y + 360 * MathUtils.sin(ringAngle + (2/3.0f) * MathUtils.PI) + 240 * MathUtils.sin(theta), 0.07f));
                addProjectile(new TimeProjectile(getLevel(), hitbox.x + 360 * MathUtils.cos(ringAngle + (4/3.0f) * MathUtils.PI) + 240 * MathUtils.cos(theta), hitbox.y + 360 * MathUtils.sin(ringAngle + (4/3.0f) * MathUtils.PI) + 240 * MathUtils.sin(theta), 0.07f));
            }

            beamCooldown -= frame;
            //Pre beam shot cue:
            if (beamCooldown <= 0.4f) {
                for (int i = 0; i < 10; ++i) {
                    addProjectile(new TimeProjectile(getLevel(), hitbox.x + MathUtils.random(-30, 30), hitbox.y + MathUtils.random(-30, 30), 0.07f));
                }
            }
            //Creates a 'beam' - projectiles that slow down to form a line with openings
            if (beamCooldown <= 0) {
                float theta = MathUtils.atan2(getPlayer().getCenterY() - hitbox.y, getPlayer().getCenterX() - hitbox.x);
                beamCooldown = BEAM_MAX_COOLDOWN;
                //Creates enough projectiles for difficult openings to form
                for (int i = 0; i < 7; ++i) {
                    addProjectile(new ControlledAccelerateProjectile(getLevel(),
                            hitbox.x, hitbox.y,
                            (300 + i * 45) * MathUtils.cos(theta), (300 + i * 45) * MathUtils.sin(theta),
                            -300,
                            0, 10));
                }
            }

        }

        return getBullets();
    }

    @Override
    public Array<Enemy> spawn(float frame) {
        Array<Enemy> enemies = new Array<Enemy>();
        if (phase == 0) {
            enemies.add(new SaturnMoonA(this, getLevel(), hitbox.x, hitbox.y + 300));
            enemies.add(new SaturnMoonB(this, getLevel(), hitbox.x, hitbox.y + 300));
            enemies.add(new RingMoon(this, getLevel(), hitbox.x, hitbox.y));
            enemies.add(new SeekerMoon(this, getLevel(), hitbox.x, hitbox.y));
            setCanSpawn(false);
            phase = 1;
        }
        return enemies;
    }


    public class SaturnMoonA extends SaturnMoon {

        float cooldown = 0;
        float MAX_COOLDOWN = 1.0f;

        public SaturnMoonA(Saturn s, BattleLevel lev, float x, float y) {
            super(s, lev, x, y, 50, 60, 100);
            setVelocity(120, 120);
        }

        @Override
        public void drawBody(ShapeRenderer r) {
            r.setColor(Color.GRAY);
            r.circle(hitbox.x, hitbox.y, hitbox.radius);
        }

        @Override
        public Array<Projectile> attack(float frame) {
            frame = saturn.timeDelta;
            this.clearProjectiles();
            //Shoot
            cooldown -= frame;

            if (cooldown <= 0) {
                createSpread(0, 10, MathUtils.PI * 2);
                cooldown = MAX_COOLDOWN;
            }

            //Move
            spinMove(frame, 480, 0.6f);
            return getBullets();
        }
    }

    public class SaturnMoonB extends SaturnMoon {

        float cooldown = 0.25f;
        float MINI_COOLDOWN = 0.25f;
        float MAX_COOLDOWN = 1.0f;
        float shots = 4;

        public SaturnMoonB(Saturn s, BattleLevel lev, float x, float y) {
            super(s, lev, x, y, 50, 60, 100);
            setVelocity(-120, 120);
        }

        @Override
        public void drawBody(ShapeRenderer r) {
            r.setColor(Color.GRAY);
            r.circle(hitbox.x, hitbox.y, hitbox.radius);
        }

        @Override
        public Array<Projectile> attack(float frame) {
            this.clearProjectiles();
            //Shoot
            cooldown -= frame;

            if (cooldown <= 0 && shots > 0) {
                float angle = MathUtils.atan2(getPlayer().getCenterY() - this.hitbox.y, getPlayer().getCenterX() - this.hitbox.x);
                this.addProjectile(new BasicProjectile(getLevel(), hitbox.x, hitbox.y, MathUtils.cos(angle) * 260, MathUtils.sin(angle) * 260));
                cooldown = MINI_COOLDOWN;
                --shots;
            } else if (shots == 0) {
                cooldown = MAX_COOLDOWN;
                shots = 4;
            }

            //Move
            spinMove(frame,240,0.6f);
            return getBullets();
        }
    }

    public class RingMoon extends SaturnMoon {

        public RingMoon(Saturn s, BattleLevel lev, float x, float y) {
            super(s, lev, x, y, 50, 60, 100);
            setVelocity(100, -100);
        }

        @Override
        public void drawBody(ShapeRenderer r) {
            r.setColor(Color.GRAY);
            r.circle(this.hitbox.x, this.hitbox.y, this.hitbox.radius);
        }

        @Override
        public Array<Projectile> attack(float frame) {
             this.clearProjectiles();

             for (int i = 0; i < 36; ++i) {
                 float theta = i * 10 * MathUtils.degreesToRadians;
                 this.addProjectile(new TimeProjectile(getLevel(),
                         this.hitbox.x + 90 * MathUtils.cos(theta),
                         this.hitbox.y + 90 * MathUtils.sin(theta),
                         frame * 2));
             }

             spinMove(frame, 240, 0.9f);
             return getBullets();
        }
    }

    public class SeekerMoon extends SaturnMoon {

        boolean inCharge;
        float acceleration;
        float absVelocity;
        float chargeAngle;
        boolean chargeShot = false;

        final float START_VELOCITY = 300;

        float chargeCooldown;
        final float MAX_CHARGE_COOLDOWN = 3.0f;

        public SeekerMoon(Saturn s, BattleLevel lev, float x, float y) {
            super(s, lev, x, y, 50, 60, 40);
            inCharge = false;
        }

        @Override
        public Array<Projectile> attack(float frame) {
            clearProjectiles();

            if (chargeCooldown <= 0) {
                float dst2 = (hitbox.x - getPlayer().getCenterX()) * (hitbox.x - getPlayer().getCenterX()) +
                        (hitbox.y - getPlayer().getCenterY()) * (hitbox.y - getPlayer().getCenterY());
                if (dst2 > 10000) {
                    dst2 = (float) Math.sqrt(dst2) - 100;
                    float t = dst2 / 100;

                    chargeCooldown = MAX_CHARGE_COOLDOWN;
                    inCharge = true;
                    absVelocity = START_VELOCITY;

                    acceleration = (START_VELOCITY) / t;

                    float theta = MathUtils.atan2(getPlayer().getCenterY() - hitbox.y, getPlayer().getCenterX() - hitbox.x);
                    velocity.x = MathUtils.cos(theta);
                    velocity.y = MathUtils.sin(theta);
                    chargeAngle = theta;
                    chargeShot = true;
                }
            }

            if (inCharge) {
                absVelocity -= acceleration * frame;

                hitbox.x += velocity.x * absVelocity * frame;
                hitbox.y += velocity.y * absVelocity * frame;

                if (chargeShot && absVelocity < 50) {
                    for (int i = -10; i <= 10; i += 5) {
                        float ang = i * MathUtils.degreesToRadians + chargeAngle;
                        addProjectile(new BasicProjectile(getLevel(), hitbox.x, hitbox.y, MathUtils.cos(ang) * 200, MathUtils.sin(ang) * 200));
                    }
                    chargeShot = false;
                }

                if (absVelocity < 0) {
                    inCharge = false;
                }
            } else {
                chargeCooldown -= frame;
            }

            return getBullets();
        }
    }

    public class SaturnMoon extends Enemy {

        public final Saturn saturn;
        public Vector2 velocity;
        //Position is based on the player
        float angularPosition = 0f;

        public SaturnMoon(Saturn s, BattleLevel lev, float x, float y, float r0, float r1, int hp) {
            super(lev, x, y, r0, r1, hp);
            saturn = s;
            velocity = new Vector2();
        }

        @Override
        public void drawBody(ShapeRenderer r) {
            r.setColor(Color.GRAY);
            r.circle(this.hitbox.x, this.hitbox.y, this.hitbox.radius);
        }


        public void setVelocity(float x, float y) {
            velocity.set(x, y);
        }

        public void spinMove(float frame, float distance, float angularVelocity) {
            float x = saturn.hitbox.x;
            float y = saturn.hitbox.y;
            hitbox.x = x + MathUtils.cos(angularPosition) * distance;
            hitbox.y = y + MathUtils.sin(angularPosition) * distance;
            angularPosition += angularVelocity * frame;

            /*
            this.hitbox.x += this.velocity.x * frame;
            this.hitbox.y += this.velocity.y * frame;
            if (this.hitbox.x - this.hitbox.radius < 0) {
                this.hitbox.x = this.hitbox.radius;
                this.velocity.x *= -1;
            }
            if (this.hitbox.x + this.hitbox.radius > getLevel().LEVEL_WIDTH) {
                this.hitbox.x = getLevel().LEVEL_WIDTH - this.hitbox.radius;
                this.velocity.x *= -1;
            }
            if (this.hitbox.y - this.hitbox.radius < 0) {
                this.hitbox.y = this.hitbox.radius;
                this.velocity.y *= -1;
            }
            if (this.hitbox.y + this.hitbox.radius > getLevel().LEVEL_HEIGHT) {
                this.hitbox.y = getLevel().LEVEL_HEIGHT - this.hitbox.radius;
                this.velocity.y *= -1;
            }
            */
        }
    }

}
