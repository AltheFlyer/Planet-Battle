package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

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
    public Uranus(float x, float y, final Player p) {
        super(x, y, 100, 120, 1500, p);

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
        /*
        teleportCooldown -= frame;
        if (teleportCooldown <= 0) {
            teleportCooldown = TELE_MAX_COOLDOWN;
            float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
            hitbox.x = player.hitbox.x + MathUtils.cos(angle) * 300;
            hitbox.y = player.hitbox.y + MathUtils.sin(angle) * 300;

            angle = MathUtils.atan2(player.hitbox.y - hitbox.y, player.hitbox.x - hitbox.x);
            bullets.add(new BasicProjectile(hitbox.x, hitbox.y, MathUtils.cos(angle) * 200, MathUtils.sin(angle) * 200));
        }
        */
        if (chosenAbility == 0 && abilityCooldown <= 0) {
            chosenAbility = MathUtils.random(1, 4);
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

        public SwirlAbility(final Player p, final Uranus u) {
            super(p, u);
            timer = 10;
            targetX = p.hitboxCenter.x;
            targetY = p.hitboxCenter.y;
            modifier = 0;
            System.out.println("ENTERING ABILITY 1");
            for (int i = 0; i < 360; ++i) {
                float angle = MathUtils.degreesToRadians * i;
                uranus.bullets.add(new TimeProjectile(targetX + MathUtils.cos(angle) * 400, targetY + MathUtils.sin(angle) * 400, 10));
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
            if (cooldown <= 0 && timer > 3) {
                cooldown = MAX_COOLDOWN;
                for (int i = 0; i < 12; ++i) {
                    float angle = (i / 12.0f) * MathUtils.PI2 + modifier;
                    uranus.bullets.add(new BasicProjectile(targetX + MathUtils.cos(angle) * 400, targetY + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 150, -MathUtils.sin(angle) * 150));
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
                uranus.bullets.add(new TimeProjectile(targetX + MathUtils.cos(angle) * 400, targetY + MathUtils.sin(angle) * 400, 4));
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
                uranus.bullets.add(new BasicProjectile(targetX + 25 * MathUtils.cos(nAngle) + MathUtils.cos(angle) * 400, targetY + 25 * MathUtils.sin(nAngle) + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 300, -MathUtils.sin(angle) * 300));
                uranus.bullets.add(new BasicProjectile(targetX + 25 * MathUtils.cos(nAngle2) + MathUtils.cos(angle) * 400, targetY + 25 * MathUtils.sin(nAngle2) + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 300, -MathUtils.sin(angle) * 300));
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

                    bullets.add(new DelayProjectile(new Rectangle(hitbox.x, hitbox.y, 15, 15), burstLeft, 250));

                    angle = MathUtils.atan2(player.hitboxCenter.y - hitbox.y, player.hitboxCenter.x - hitbox.x);
                    for (int i = -30; i <= 30; i += 15) {
                        bullets.add(new BasicProjectile(hitbox.x, hitbox.y, MathUtils.cos(angle + i * MathUtils.degreesToRadians) * 150, MathUtils.sin(angle + i * MathUtils.degreesToRadians) * 150));
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
                        bullets.add(new BasicProjectile(0, i, 180, 0));
                        bullets.add(new BasicProjectile(0, 300 + i, 180, 0));
                        bullets.add(new BasicProjectile(0, 600 + i, 180, 0));
                        bullets.add(new BasicProjectile(0, 900 + i, 180, 0));
                    }
                } else {
                    for (int i = 0; i < 150; i += 3) {
                        bullets.add(new BasicProjectile(1200, 150 + i, -180, 0));
                        bullets.add(new BasicProjectile(1200, 450 + i, -180, 0));
                        bullets.add(new BasicProjectile(1200, 750 + i, -180, 0));
                        bullets.add(new BasicProjectile(1200, 1050 + i, -180, 0));
                    }
                }
                for (int i = 0; i < 18; ++i) {
                    float angle = i * 20 * MathUtils.degreesToRadians + offset * MathUtils.degreesToRadians;
                    bullets.add(new BasicProjectile(hitbox.x, hitbox.y, MathUtils.cos(angle) * 200, MathUtils.sin(angle) * 200));
                }
                offset += 6;
                leftSide = !leftSide;
            }
        }
    }
}
