package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.projectiles.*;

public class Uranus extends Enemy {

    int phase = 0;

    int chosenAbility = 0;

    Ability ability;

    float abilityCooldown;
    final float ABILITY_MAX_COOLDOWN = 1f;

    /**
     * @param x  The x coordinate to start at
     * @param y  The y coordinate to start at
     */
    public Uranus(final BattleLevel lev, float x, float y) {
        super(lev, x, y, 100, 120, 1500);

        abilityCooldown = ABILITY_MAX_COOLDOWN;
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.valueOf("#12e8b9"));
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public void drawObjects(ShapeRenderer r) {
        r.setColor(Color.ORANGE);
        //TODO Make the offscreen indicator better
        r.rectLine(player.hitboxCenter.x, player.hitboxCenter.y, hitbox.x, hitbox.y, 2);
    }

    @Override
    public Array<Projectile> attack(float frame) {
        bullets.clear();

        if (chosenAbility == 0 && abilityCooldown <= 0) {
            chosenAbility = MathUtils.random(1, 8);
            if (chosenAbility == 1) {
                ability = new SwirlAbility(player, this);
            }
            if (chosenAbility == 2) {
                ability = new IndirectAbility(player, this);
            }
            if (chosenAbility == 3) {
                ability = new TelespamAbility(player, this);
            }
            if (chosenAbility == 4) {
                ability = new LineWaveAbility(player, this);
            }
            if (chosenAbility == 5) {
                ability = new SwirlAbility2(player, this);
            }
            if (chosenAbility == 6) {
                ability = new MeteorAbility(player, this);
            }
            if (chosenAbility == 7) {
                ability = new SeekerAbility(player, this);
            }
            if (chosenAbility == 8) {
                ability = new MethaneAbility(player,this);
            }
        }

        if (chosenAbility != 0) {
            ability.run(frame);
            ability.timer -= frame;
            if (ability.timer <= 0) {
                chosenAbility = 0;
                System.out.println("ENTERING ABILITY 0");
                abilityCooldown = ABILITY_MAX_COOLDOWN;
            }
        }

        abilityCooldown -= frame;

        return bullets;
    }

    class Ability {
        float timer;
        final Uranus uranus;
        final Player player;

        public Ability(final Player p, final Uranus u) {
            uranus = u;
            player = p;
        }

        public void run(float frame) {

        }
    }

    class SwirlAbility extends Ability {

        float targetX, targetY, modifier;
        float cooldown;
        final float MAX_COOLDOWN = 0.5f;
        float dist = 400;

        public SwirlAbility(final Player p, final Uranus u) {
            super(p, u);
            timer = 10;
            targetX = p.hitboxCenter.x;
            targetY = p.hitboxCenter.y;
            modifier = 0;
            System.out.println("ENTERING ABILITY 1");
            for (int i = 0; i < 360; ++i) {
                float angle = MathUtils.degreesToRadians * i;
                uranus.bullets.add(new TimeProjectile(level, targetX + MathUtils.cos(angle) * 400, targetY + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 20, -MathUtils.sin(angle) * 20, 10));
            }
            do {
                float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
                uranus.hitbox.x = targetX + MathUtils.cos(angle) * (400 + uranus.hitbox.radius);
                uranus.hitbox.y = targetY + MathUtils.sin(angle) * (400 + uranus.hitbox.radius);
            } while (uranus.hitbox.x > 1100 || uranus.hitbox.y > 1100 || uranus.hitbox.x < 100 || uranus.hitbox.y < 100);
        }

        @Override
        public void run(float frame) {
            cooldown -= frame;
            dist -= 20 * frame;
            if (cooldown <= 0 && timer > 3) {
                cooldown = MAX_COOLDOWN;
                for (int i = 0; i < 12; ++i) {
                    float angle = (i / 12.0f) * MathUtils.PI2 + modifier;
                    uranus.bullets.add(new BasicProjectile(level, targetX + MathUtils.cos(angle) * dist, targetY + MathUtils.sin(angle) * dist, -MathUtils.cos(angle) * 150, -MathUtils.sin(angle) * 150));
                }
                modifier += MathUtils.PI / 48;
            }
        }
    }

    class IndirectAbility extends Ability{

        float targetX, targetY, cooldown;
        final float MAX_COOLDOWN = 0.05f;
        boolean angleUnset[] = {true, true, true, true};
        float angle, nAngle, nAngle2;

        public IndirectAbility(final Player p, final Uranus u) {
            super(p, u);
            timer = 4;
            cooldown = 0;
            targetX = player.hitboxCenter.x;
            targetY = player.hitboxCenter.y;
            System.out.println("ENTERING ABILITY 2");
            for (int i = 0; i < 360; ++i) {
                float angle = MathUtils.degreesToRadians * i;
                uranus.bullets.add(new TimeProjectile(level, targetX + MathUtils.cos(angle) * 400, targetY + MathUtils.sin(angle) * 400, 4));
            }
            do {
                float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
                uranus.hitbox.x = targetX + MathUtils.cos(angle) * (400 + uranus.hitbox.radius);
                uranus.hitbox.y = targetY + MathUtils.sin(angle) * (400 + uranus.hitbox.radius);
            } while (uranus.hitbox.x > 1100 || uranus.hitbox.y > 1100 || uranus.hitbox.x < 100 || uranus.hitbox.y < 100);
        }

        @Override
        public void run(float frame) {
            cooldown -= frame;
            if (cooldown <= 0) {
                cooldown = MAX_COOLDOWN;
                for (int i = 0; i < 4; ++i) {
                    if (timer <= 4 - i && angleUnset[i]) {
                        angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
                        nAngle = angle + MathUtils.PI / 2;
                        nAngle2 = angle - MathUtils.PI / 2;
                        angleUnset[i] = false;
                    }
                }
                uranus.bullets.add(new BasicProjectile(level, targetX + 25 * MathUtils.cos(nAngle) + MathUtils.cos(angle) * 400, targetY + 25 * MathUtils.sin(nAngle) + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 300, -MathUtils.sin(angle) * 300));
                uranus.bullets.add(new BasicProjectile(level, targetX + 25 * MathUtils.cos(nAngle2) + MathUtils.cos(angle) * 400, targetY + 25 * MathUtils.sin(nAngle2) + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 300, -MathUtils.sin(angle) * 300));
            }
        }
    }

    class TelespamAbility extends Ability {

        int bursts;
        float burstLeft = 2.0f;
        float cooldown;
        final float TP_DELAY = 0.4f;

        public TelespamAbility(final Player p, final Uranus u) {
            super(p, u);
            timer = 9;
            cooldown = 0;
        }

        @Override
        public void run(float frame) {
            if (burstLeft > 0) {
                if (cooldown <= 0) {
                    cooldown = TP_DELAY;
                    float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
                    hitbox.x = player.hitbox.x + MathUtils.cos(angle) * 300;
                    hitbox.y = player.hitbox.y + MathUtils.sin(angle) * 300;

                    bullets.add(new DelayProjectile(level, new Rectangle(hitbox.x, hitbox.y, 15, 15), burstLeft, 250));

                    angle = MathUtils.atan2(player.hitboxCenter.y - hitbox.y, player.hitboxCenter.x - hitbox.x);
                    for (int i = -30; i <= 30; i += 15) {
                        bullets.add(new BasicProjectile(level, hitbox.x, hitbox.y, MathUtils.cos(angle + i * MathUtils.degreesToRadians) * 150, MathUtils.sin(angle + i * MathUtils.degreesToRadians) * 150));
                    }
                }
                burstLeft -= frame;
            } else {
                burstLeft = 3.0f;
                cooldown = 1.0f;
                bursts += 1;
            }
            cooldown -= frame;
            if (bursts >= 3) {
                timer = 0;
            }
        }

    }

    class LineWaveAbility extends Ability {

        boolean leftSide = true;
        final float DELAY = 0.8f;
        float cooldown = 0;
        float offset = 0;

        public LineWaveAbility(Player p, Uranus u) {
            super(p, u);
            timer = 10;
        }

        @Override
        public void run(float frame) {
            cooldown -= frame;
            if (cooldown <= 0 && timer > 2) {
                cooldown = DELAY;
                if (leftSide) {
                    for (int i = 0; i < 150; i += 3) {
                        bullets.add(new BasicProjectile(level, 0, i, 180, 0));
                        bullets.add(new BasicProjectile(level, 0, 300 + i, 180, 0));
                        bullets.add(new BasicProjectile(level, 0, 600 + i, 180, 0));
                        bullets.add(new BasicProjectile(level, 0, 900 + i, 180, 0));
                    }
                } else {
                    for (int i = 0; i < 150; i += 3) {
                        bullets.add(new BasicProjectile(level, 1200, 150 + i, -180, 0));
                        bullets.add(new BasicProjectile(level, 1200, 450 + i, -180, 0));
                        bullets.add(new BasicProjectile(level, 1200, 750 + i, -180, 0));
                        bullets.add(new BasicProjectile(level, 1200, 1050 + i, -180, 0));
                    }
                }
                for (int i = 0; i < 18; ++i) {
                    float angle = i * 20 * MathUtils.degreesToRadians + offset * MathUtils.degreesToRadians;
                    bullets.add(new BasicProjectile(level, hitbox.x, hitbox.y, MathUtils.cos(angle) * 200, MathUtils.sin(angle) * 200));
                }
                offset += 6;
                leftSide = !leftSide;
            }
        }
    }

    class SwirlAbility2 extends Ability {

        float angle = 0;
        float cooldown = 0;
        final float MAX_COOLDOWN = 0.05f;

        public SwirlAbility2(Player p, Uranus u) {
            super(p, u);
            timer = 16;
            do {
                float theta = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
                uranus.hitbox.x = player.hitboxCenter.x + MathUtils.cos(theta) * (150 + uranus.hitbox.radius);
                uranus.hitbox.y = player.hitboxCenter.y + MathUtils.sin(theta) * (150 + uranus.hitbox.radius);
            } while (uranus.hitbox.x > 1100 || uranus.hitbox.y > 1100 || uranus.hitbox.x < 100 || uranus.hitbox.y < 100);

            for (int i = 0; i < 360; ++i) {
                float theta = MathUtils.degreesToRadians * i;
                uranus.bullets.add(new TimeProjectile(level, hitbox.x + MathUtils.cos(theta) * 400, hitbox.y + MathUtils.sin(theta) * 400, 10));
            }
        }

        @Override
        public void run(float frame) {
            cooldown -= frame;
            angle += MathUtils.PI * frame;
            if (cooldown <= 0 && timer > 8) {
                cooldown = MAX_COOLDOWN;
                bullets.add(new AccelerateProjectile(level, hitbox.x, hitbox.y,
                        MathUtils.cos(angle) * 200, MathUtils.sin(angle) * 200,
                        -MathUtils.cos(angle) * 50, -MathUtils.sin(angle) * 50,
                        400, 8));
                if (timer < 12) {
                    bullets.add(new AccelerateProjectile(level, hitbox.x, hitbox.y,
                            MathUtils.cos(angle + MathUtils.PI) * 200, MathUtils.sin(angle + MathUtils.PI) * 200,
                            -MathUtils.cos(angle + MathUtils.PI) * 50, -MathUtils.sin(angle + MathUtils.PI) * 50,
                            400, 8));
                }
                if (timer < 10) {
                    bullets.add(new AccelerateProjectile(level, hitbox.x, hitbox.y,
                            MathUtils.cos(angle + MathUtils.PI / 2) * 200, MathUtils.sin(angle + MathUtils.PI / 2) * 200,
                            -MathUtils.cos(angle + MathUtils.PI / 2) * 50, -MathUtils.sin(angle + MathUtils.PI / 2) * 50,
                            400, 8));
                    bullets.add(new AccelerateProjectile(level, hitbox.x, hitbox.y,
                            MathUtils.cos(angle - MathUtils.PI / 2) * 200, MathUtils.sin(angle - MathUtils.PI / 2) * 200,
                            -MathUtils.cos(angle - MathUtils.PI / 2) * 50, -MathUtils.sin(angle - MathUtils.PI / 2) * 50,
                            400, 8));
                }
            }
        }

    }

    class MeteorAbility extends Ability {

        float cooldown = 0;
        final float MAX_COOLDOWN = 0.5f;
        boolean goingLeft = true;

        public MeteorAbility(Player p, Uranus u) {
            super(p, u);
            timer = 10;
        }

        public void run(float frame) {
            cooldown -= frame;
            if (cooldown <= 0 && timer > 4) {
                cooldown = MAX_COOLDOWN;
                bullets.addAll(new BorderScatterProjectile(
                                level,
                                new Rectangle(MathUtils.random(0, level.LEVEL_WIDTH),
                                        level.LEVEL_HEIGHT + 10,
                                        20, 20),
                                new Vector2(0, -200),
                                5, 2),
                        new BorderScatterProjectile(
                                level,
                                new Rectangle(MathUtils.random(0, level.LEVEL_WIDTH),
                                        level.LEVEL_HEIGHT + 10,
                                        20, 20),
                                new Vector2(0, -200),
                                5, 2)
                );
            }
            if (goingLeft) {
                hitbox.x -= 150* frame;
            } else {
                hitbox.x += 150 * frame;
            }
            if (hitbox.x < 0 + hitbox.radius) {
                hitbox.x = hitbox.radius;
                goingLeft = false;
            } else if (hitbox.x > level.LEVEL_WIDTH - hitbox.radius) {
                hitbox.x = level.LEVEL_WIDTH - hitbox.radius;
                goingLeft = true;
            }
        }
    }

    class SeekerAbility extends Ability {

        float cooldown = 0;
        final float MAX_COOLDOWN = 1;
        boolean goingLeft = true;

        public SeekerAbility(Player p, Uranus u) {
            super(p, u);
            timer = 10;
        }

        public void run(float frame) {
            cooldown -= frame;
            if (cooldown <= 0) {
                cooldown = MAX_COOLDOWN;
                for (int i = 0; i < 7; ++i) {
                    float size = MathUtils.random(10, 25);
                    bullets.add(new DelayProjectile(
                                    level,
                                    new Rectangle(hitbox.x, hitbox.y, size, size),
                                    0, MathUtils.random(110, 500),
                                    1, 300
                            )
                    );
                    size = MathUtils.random(10, 25);
                    bullets.add(new DelayProjectile(
                                    level,
                                    new Rectangle(hitbox.x, hitbox.y, size, size),
                                    0, -MathUtils.random(110, 500),
                                    1, 300
                            )
                    );
                }
            }
            if (goingLeft) {
                hitbox.x -= 150* frame;
            } else {
                hitbox.x += 150 * frame;
            }
            if (hitbox.x < 0 + hitbox.radius) {
                hitbox.x = hitbox.radius;
                goingLeft = false;
            } else if (hitbox.x > level.LEVEL_WIDTH - hitbox.radius) {
                hitbox.x = level.LEVEL_WIDTH - hitbox.radius;
                goingLeft = true;
            }
        }
    }

    class MethaneAbility extends Ability {

        float cooldown;
        final float MAX_COOLDOWN = 1.0f;
        float speed = 200;

        public MethaneAbility(Player p, Uranus u) {
            super(p, u);
            timer = 20;
        }

        @Override
        public void run(float frame) {
            cooldown -= frame;
            //Increase projectile speed over time.
            speed += frame * 10;
            if (cooldown <= 0) {
                float angle = MathUtils.atan2(player.hitboxCenter.y - hitbox.y, player.hitboxCenter.x - hitbox.x);
                cooldown = MAX_COOLDOWN - 0.025f * (20 - timer);
                bullets.add(
                        new BasicProjectile(level, new Rectangle(hitbox.x - 15, hitbox.y - 15, 30, 30), new Vector2(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed))
                );
                bullets.add(
                        new OrbitalProjectile(level, 50, hitbox.x, hitbox.y, MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed, MathUtils.PI, 0)
                );
                bullets.add(
                        new OrbitalProjectile(level, 50, hitbox.x, hitbox.y, MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed, MathUtils.PI, (2 * MathUtils.PI) / 3)
                );
                bullets.add(
                        new OrbitalProjectile(level, 50, hitbox.x, hitbox.y, MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed, MathUtils.PI, (4 * MathUtils.PI) / 3)
                );
                if (timer < 10) {
                    for (int i = 0; i < 3; ++i) {
                        float offset = MathUtils.random(0, 2 * MathUtils.PI);
                        float sOffset = MathUtils.random(-50, 50);
                        bullets.add(
                                new BasicProjectile(level, new Rectangle(hitbox.x - 15, hitbox.y - 15, 30, 30), new Vector2(MathUtils.cos(angle + offset) * (speed + sOffset), MathUtils.sin(angle + offset) * (speed + sOffset)))
                        );
                        bullets.add(
                                new OrbitalProjectile(level, 50, hitbox.x, hitbox.y, MathUtils.cos(angle + offset) * (speed + sOffset), MathUtils.sin(angle + offset) * (speed + sOffset), MathUtils.PI, 0)
                        );
                        bullets.add(
                                new OrbitalProjectile(level, 50, hitbox.x, hitbox.y, MathUtils.cos(angle + offset) * (speed + sOffset), MathUtils.sin(angle + offset) * (speed + sOffset), MathUtils.PI, (2 * MathUtils.PI) / 3)
                        );
                        bullets.add(
                                new OrbitalProjectile(level, 50, hitbox.x, hitbox.y, MathUtils.cos(angle + offset) * (speed + sOffset), MathUtils.sin(angle + offset) * (speed + sOffset), MathUtils.PI, (4 * MathUtils.PI) / 3)
                        );
                    }
                }
            }
        }

    }
}
