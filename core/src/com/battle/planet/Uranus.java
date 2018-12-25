package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Uranus extends Enemy {

    int phase = 0;

    int chosenAbility = 0;

    Ability ability;

    float teleportCooldown;
    final float TELE_MAX_COOLDOWN = 1f;

    /**
     * @param x  The x coordinate to start at
     * @param y  The y coordinate to start at
     */
    public Uranus(float x, float y, final Player p) {
        super(x, y, 100, 120, 1500, p);

        teleportCooldown = TELE_MAX_COOLDOWN;
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
        if (chosenAbility == 0) {
            chosenAbility = MathUtils.random(1, 2);
            if (chosenAbility == 1) {
                ability = new SwirlAbility(player, this);
            }
            if (chosenAbility == 2) {
                ability = new IndirectAbility(player, this);
            }
        }

        if (chosenAbility != 0) {
            ability.run(frame);
            ability.timer -= frame;
        }

        if (ability.timer <= 0) {
            chosenAbility = 0;
            System.out.println("ENTERING ABILITY 0");
        }

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
                float angle = 0;
                boolean canShoot = false;
                if (timer > 3.0f) {
                    angle = MathUtils.PI / 4.0f;
                    canShoot = true;
                } else if (timer > 2.0f) {
                    angle = (3 * MathUtils.PI) / 4.0f;
                    canShoot = true;
                } else if (timer > 1.0f) {
                    angle = (5 * MathUtils.PI) / 4.0f;
                    canShoot = true;
                } else if (timer > 0.0f) {
                    angle = (7 * MathUtils.PI) / 4.0f;
                    canShoot = true;
                }
                if (canShoot) {
                    float nAngle = angle + MathUtils.PI / 2;
                    float nAngle2 = angle - MathUtils.PI / 2;
                    uranus.bullets.add(new BasicProjectile(targetX + 20 * MathUtils.cos(nAngle) + MathUtils.cos(angle) * 400, targetY + 20 * MathUtils.sin(nAngle) + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 300, -MathUtils.sin(angle) * 300));
                    uranus.bullets.add(new BasicProjectile(targetX + 20 * MathUtils.cos(nAngle2) + MathUtils.cos(angle) * 400, targetY + 20 * MathUtils.sin(nAngle2) + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 300, -MathUtils.sin(angle) * 300));
                }
            }
        }
    }




}
