package com.battle.planet.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.BattleLevel;
import com.battle.planet.enemies.moons.MoonFactory;
import com.battle.planet.projectiles.*;

public class Saturn extends Enemy {

    private MoonFactory moonGenerator;
    public int activeMoons = 0;
    private Vector2 spawnPosition = new Vector2(0, 0);

    private int phase = 0;

    private float moonSpawnTimer = 3f;
    private final float MAX_MOON_SPAWNER_TIMER = 4f;

    //The time that the moon indicator appears for
    private float moonPrepTime = 0;
    private final float MAX_MOON_PREP_TIME = 3f;

    private float subPhaseTimer = 0;
    private final float PRE_SECOND_PHASE_DURATION = 3;
    private static final float PRE_THIRD_PHASE_TIMER = 5;

    //First Phase
    final float BEAM_MAX_COOLDOWN = 2f;
    float beamCooldown = BEAM_MAX_COOLDOWN * 2;

    //Second Phase
    //Hour hand
    final float HOUR_HAND_ANGULAR_VELOCITY = -8 * (MathUtils.PI / 180f);
    float hourHandRotation = (MathUtils.PI / 2) - (PRE_SECOND_PHASE_DURATION * HOUR_HAND_ANGULAR_VELOCITY);

    final float MINUTE_HAND_ANGULAR_VELOCITY = -25 * (MathUtils.PI / 180f);
    float minuteHandRotation = (MathUtils.PI / 2) - (PRE_SECOND_PHASE_DURATION * HOUR_HAND_ANGULAR_VELOCITY);

    float secondHandRotation = (MathUtils.PI / 2) - (PRE_SECOND_PHASE_DURATION * HOUR_HAND_ANGULAR_VELOCITY);
    final float SECOND_HAND_ANGULAR_VELOCITY = -40 * (MathUtils.PI / 180f);
    final float SECOND_HAND_GAP_WIDTH = 100;
    float secondHandGap = 0;
    float secondHandGapDelta = 50;

    float wiggleCooldown = 0;
    float MAX_WIGGLE_COOLDOWN = 0.15f;

    float ringAngle = 0;

    final float MAX_RANDOM_SCATTER_COOLDOWN = 0.2f;
    float randomScatterCooldown = MAX_RANDOM_SCATTER_COOLDOWN;

    //Third Phase
    float wideRingPosition = 1;
    float wideRingConstant = 400;

    //Continuous bursts
    final float MAX_BURST_COOLDOWN = 1.5f;
    float burstCooldown = MAX_BURST_COOLDOWN;
    //The multiplier for the direction that the burst is aimed at
    int burstDirectionTick = 0;

    //Swinging arms
    float armTheta = 0;
    final float ARM_ANGULAR_VELOCITY = -MathUtils.PI / 6;
    final float ARM_AMPLITUDE = 80;


    //Used to multiply how fast everything is
    public float timeMultiplier = 1;

    //Controls how long periods of time multiplication occur for
    float clockTimer = 0;
    public boolean timeStopped = false;


    public Saturn(BattleLevel lev, float x, float y) {
        super(lev, x, y, 100, 120, 1750);
        clockTimer = 5;
        moonGenerator = new MoonFactory();
        addPhaseMarkers(1750 - 500, 1750 - 500 - 750);
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GOLDENROD);
        r.circle(hitbox.x, hitbox.y, hitbox.radius);

        if (phase == -1) {
            r.setColor(new Color(0.7f, 0.7f, 0, 0.5f));
            r.rectLine(hitbox.x, hitbox.y,
                   hitbox.x + 300 * MathUtils.cos(hourHandRotation),
                   hitbox.y + 300 * MathUtils.sin(hourHandRotation), 3);
            r.rectLine(hitbox.x, hitbox.y,
                    hitbox.x + 500 * MathUtils.cos(minuteHandRotation),
                    hitbox.y + 500 * MathUtils.sin(minuteHandRotation), 3);
            r.rectLine(hitbox.x, hitbox.y,
                    hitbox.x + 600 * MathUtils.cos(secondHandRotation),
                    hitbox.y + 600 * MathUtils.sin(secondHandRotation), 3);
        }

        if (moonPrepTime > 0) {
            r.setColor(new Color(1, 0.647f, 0, 0.5f));
            r.circle(spawnPosition.x, spawnPosition.y, 70);
        }
    }

    @Override
    public Array<Projectile> attack(float frame) {
        clearProjectiles();

        if (moonSpawnTimer == 0) {
            moonPrepTime -= frame;
            if (moonPrepTime <= 0) {
                moonPrepTime = 0;
                moonSpawnTimer = MAX_MOON_SPAWNER_TIMER;
                setCanSpawn(true);
            }
        } else if (!timeStopped) {
            //Moon spawn timer, moons spawn more slowly the more there are
            moonSpawnTimer -= ((MathUtils.random() / (activeMoons)) * frame);

            if (moonSpawnTimer <= 0) {
                moonSpawnTimer = 0;
                moonPrepTime = MAX_MOON_PREP_TIME;
                float theta = MathUtils.random(0, MathUtils.PI2);
                float dist = MathUtils.random(hitbox.radius, 600);
                spawnPosition = new Vector2(
                        getHitbox().x + MathUtils.cos(theta) * dist,
                        getHitbox().y + MathUtils.sin(theta) * dist
                );
            }
        }


        if (phase == 2) {
            clockTimer -= frame / timeMultiplier;
            if (clockTimer <= 0 && timeStopped) {
                timeMultiplier = 1;
                clockTimer = (MathUtils.PI2) /
                        (Math.abs(HOUR_HAND_ANGULAR_VELOCITY));
                timeStopped = false;
            } else if (clockTimer <= 0 && !timeStopped) {
                clockTimer = (MathUtils.PI2) /
                        (4 * Math.abs(HOUR_HAND_ANGULAR_VELOCITY));
                //Burst of bullets from each moon + Saturn
                for (int j = 0; j < 36; ++j) {
                    float theta = (j / 36.0f) * MathUtils.PI2;
                    for (int i = 0; i < getLevel().enemies.size; ++i) {
                        addProjectile(new WaveProjectile(
                                getLevel(),
                                getLevel().enemies.get(i).hitbox.x,
                                getLevel().enemies.get(i).hitbox.y,
                                MathUtils.cos(theta) * 200,
                                MathUtils.sin(theta) * 200,
                                10
                        ));
                    }
                }
                timeStopped = true;
            }
        }

        //Start of battle initializer
        if (phase == 0) {
            //Outer rings
            for (int k = 0; k < 240; k += 20) {
                for (int i = 0; i < 360; ++i) {
                    float theta = i * MathUtils.degreesToRadians;
                    addProjectile(new BasicProjectile(getLevel(), 600 + (600 + k) * MathUtils.cos(theta), 600 + (600 + k) * MathUtils.sin(theta), 0, 0));
                }
            }
            setCanSpawn(true);
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

        } else if (phase == 2) {

            if (!timeStopped) {
                //Spinning clock hands
                //Hour hand

                if (timeMultiplier == 1) {
                    hourHandRotation += HOUR_HAND_ANGULAR_VELOCITY * frame;
                    if (hourHandRotation > MathUtils.PI * 2) {
                        hourHandRotation -= MathUtils.PI * 2;
                    }

                    minuteHandRotation += MINUTE_HAND_ANGULAR_VELOCITY * frame;
                    if (minuteHandRotation > MathUtils.PI * 2) {
                        minuteHandRotation -= MathUtils.PI * 2;
                    }

                    secondHandRotation += SECOND_HAND_ANGULAR_VELOCITY * frame;
                    if (secondHandRotation > MathUtils.PI * 2) {
                        secondHandRotation -= MathUtils.PI * 2;
                    }

                    secondHandGap += secondHandGapDelta * frame;
                    if (secondHandGap < hitbox.radius) {
                        secondHandGapDelta *= -1;
                        secondHandGap = hitbox.radius;
                    } else if (secondHandGap > 600 - SECOND_HAND_GAP_WIDTH) {
                        secondHandGap = 600 - SECOND_HAND_GAP_WIDTH;
                        secondHandGapDelta *= -1;
                    }
                }

                //Creates clock hands
                //Hour hand: constant width, close to saturn
                for (int i = 0; i < 30; ++i) {
                    addProjectile(new TimeProjectile(getLevel(),
                            hitbox.x + MathUtils.cos(hourHandRotation) * (i * 10),
                            hitbox.y + MathUtils.sin(hourHandRotation) * (i * 10),
                            0.00f)
                    );
                }

                //Minute hand: constant width, doesn't quite reach the edge of the circle from phase 0
                //Has gaps inside of it
                for (int i = 0; i < 8; ++i) {
                    addProjectile(new TimeProjectile(getLevel(),
                            hitbox.x + MathUtils.cos(minuteHandRotation) * (i * (60)),
                            hitbox.y + MathUtils.sin(minuteHandRotation) * (i * (60)),
                            0.00f)
                    );
                }

                //Seconds hand: spans the full radius of the arena area, and has a small (moving) gap to go through
                for (int i = 0; i < 30; ++i) {
                    addProjectile(new TimeProjectile(getLevel(),
                            hitbox.x + MathUtils.cos(secondHandRotation) * (i * (secondHandGap / 30)),
                            hitbox.y + MathUtils.sin(secondHandRotation) * (i * (secondHandGap / 30)),
                            0.00f)
                    );
                    addProjectile(new TimeProjectile(getLevel(),
                            hitbox.x + MathUtils.cos(secondHandRotation) * (secondHandGap + SECOND_HAND_GAP_WIDTH + i * ((600 - (secondHandGap + SECOND_HAND_GAP_WIDTH)) / 30)),
                            hitbox.y + MathUtils.sin(secondHandRotation) * (secondHandGap + SECOND_HAND_GAP_WIDTH + i * ((600 - (secondHandGap + SECOND_HAND_GAP_WIDTH)) / 30)),
                            0.00f)
                    );
                }

                //Random projectiles for fun
                randomScatterCooldown -= frame;
                if (randomScatterCooldown <= 0) {
                    randomScatterCooldown = MAX_RANDOM_SCATTER_COOLDOWN;
                    for (int i = 0; i < 2; ++i) {
                        addProjectile(new BasicProjectile(getLevel(), 0, MathUtils.random(0, 1200), 100, 0));
                    }
                }
            } else {
                if (clockTimer > 4f) {
                    wiggleCooldown -= frame;
                    if (wiggleCooldown <= 0) {
                        wiggleCooldown += MAX_WIGGLE_COOLDOWN;
                        createWaveSpread(0, 10, MathUtils.PI2, 10);
                    }
                }
            }
        } else if (phase == 3) {
            //Outer ring
            float dst = wideRingConstant + (MathUtils.cos(wideRingPosition) * 100 + 100);
            wideRingPosition += frame;
            for (int i = 0; i < 360; ++i) {
                addProjectile(new TimeProjectile(
                        getLevel(),
                        getHitbox().x + MathUtils.cos(i / 360.0f * MathUtils.PI2) * dst,
                        getHitbox().y + MathUtils.sin(i / 360.0f * MathUtils.PI2) * dst,
                        0
                ));
            }

            burstCooldown -= frame;
            if (burstCooldown <= 0) {
                burstCooldown += MAX_BURST_COOLDOWN;
                createSpread((burstDirectionTick / 72f) * MathUtils.PI, 24, MathUtils.PI2, 120);
                burstDirectionTick++;
                if (burstDirectionTick >= 72) {
                    burstDirectionTick = 0;
                }
            }

            armTheta += ARM_ANGULAR_VELOCITY * frame;
            /*
            if (armTheta > MathUtils.PI2) {
                armTheta -= MathUtils.PI2;
            }
            */
            for (int i = 0; i < 200; ++i) {
                Vector2 pos = new Vector2(
                        (i / 200f) * 1200 - 600,
                        MathUtils.sin((i / 200f) * MathUtils.PI2) * ARM_AMPLITUDE);
                pos.rotate(armTheta * MathUtils.radiansToDegrees);
                addProjectile(new TimeProjectile(
                        getLevel(),
                        pos.x + 600,
                        pos.y + 600,
                        0
                ));
            }

        } else if (phase == -1) {
            //Prepare cues for phase 2
            if (subPhaseTimer < PRE_SECOND_PHASE_DURATION) {
                subPhaseTimer += frame / timeMultiplier;
                hourHandRotation += HOUR_HAND_ANGULAR_VELOCITY * frame;
                if (hourHandRotation > MathUtils.PI2) {
                    hourHandRotation -= MathUtils.PI2;
                }
                minuteHandRotation += MINUTE_HAND_ANGULAR_VELOCITY * frame;
                if (minuteHandRotation > MathUtils.PI2) {
                    minuteHandRotation -= MathUtils.PI2;
                }
                secondHandRotation += SECOND_HAND_ANGULAR_VELOCITY * frame;
                if (secondHandRotation > MathUtils.PI2) {
                    secondHandRotation -= MathUtils.PI2;
                }
            } else {
                subPhaseTimer = 0;
                phase = 2;
                clockTimer = (MathUtils.PI2) /
                        (Math.abs(HOUR_HAND_ANGULAR_VELOCITY));
            }
        } else if (phase == -2) {
            if (subPhaseTimer < PRE_THIRD_PHASE_TIMER) {
                timeMultiplier = 1;

                subPhaseTimer += frame;
                burstCooldown -= frame;
                if (burstCooldown < 0) {
                    burstCooldown = MAX_BURST_COOLDOWN * 0.3f;
                    for (int i = -12; i < 12; ++i) {
                        addProjectile(new BasicProjectile(getLevel(), hitbox.x, hitbox.y + (i * 10), 200, 0));
                        addProjectile(new BasicProjectile(getLevel(), hitbox.x, hitbox.y + (i * 10), -200, 0));
                    }
                }
            } else {
                phase = 3;
                burstCooldown = MAX_BURST_COOLDOWN;
            }
        }

        if (phase == 1 && getHealth() < 1750 - 500) {
            phase = -1;
        } else if (phase == 2 && getHealth() < 1750 - 500 - 750) {
            phase = -2;
            //Prepare some bullets
            //Calculate distance
            float vel = 50;
            float dst = 600 + vel * PRE_THIRD_PHASE_TIMER;
            for (int i = 0; i < 360; ++i) {
                float theta = (i / 36.0f) * MathUtils.PI2;
                addProjectile(
                        new TimeProjectile(
                                getLevel(),
                                hitbox.x + dst * MathUtils.cos(theta),
                                hitbox.y + dst * MathUtils.sin(theta),
                                -vel * MathUtils.cos(theta),
                                -vel * MathUtils.sin(theta),
                                PRE_THIRD_PHASE_TIMER
                        )
                );
            }
            //If saturn dies during time stop somehow, restart time
            timeStopped = false;
        }

        return getBullets();
    }

    @Override
    public Array<Enemy> spawn(float frame) {
        Array<Enemy> enemies = new Array<Enemy>();
        if (phase == 0) {
            enemies.add(moonGenerator.generateMoon(this, hitbox.x, hitbox.y + hitbox.radius));
            enemies.add(moonGenerator.generateMoon(this, hitbox.x, hitbox.y - hitbox.radius));
            setCanSpawn(false);
            phase = 1;
            activeMoons += 2;
        } else {
            if (moonGenerator.canGenerateMoon()) {
                enemies.add(moonGenerator.generateMoon(this, spawnPosition.x, spawnPosition.y));
                setCanSpawn(false);
                activeMoons++;
            }
        }
        return enemies;
    }

}
