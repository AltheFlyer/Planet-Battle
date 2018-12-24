package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Uranus extends Enemy {

    int phase = 0;

    int ability = 0;
    float abilityTimer = 0f;
    float abilityCooldown = 0f;
    float targetX = 0, targetY = 0;
    float modifier = 0;

    final float[] ABILITY_TIMERS = {
            0, 10, 4
    };
    final float[] ABILITY_COOLDOWNS = {
            0, 0.5f, 0.05f
    };

    float teleportCooldown;
    final float TELE_MAX_COOLDOWN = 1f;

    /**
     * @param x  The x coordinate to start at
     * @param y  The y coordinate to start at
     */
    public Uranus(float x, float y) {
        super(x, y, 100, 120, 1500);

        teleportCooldown = TELE_MAX_COOLDOWN;
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.valueOf("#12e8b9"));
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public Array<Projectile> attack(Player player, float frame) {
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
        if (ability == 0) {
            ability = MathUtils.random(1, 2);
            if (ability == 1) {
                abilityTimer = ABILITY_TIMERS[1];
                abilityCooldown = 0;
                targetX = player.hitboxCenter.x;
                targetY = player.hitboxCenter.y;
                modifier = 0;
                System.out.println("ENTERING ABILITY 1");
                for (int i = 0; i < 360; ++i) {
                    float angle = MathUtils.degreesToRadians * i;
                    bullets.add(new TimeProjectile(targetX + MathUtils.cos(angle) * 400, targetY + MathUtils.sin(angle) * 400, 10));
                }
                do {
                    float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
                    hitbox.x = targetX + MathUtils.cos(angle) * (400 + hitbox.radius);
                    hitbox.y = targetY + MathUtils.sin(angle) * (400 + hitbox.radius);
                } while (hitbox.x > 1100 && hitbox.y > 1100 && hitbox.x < 100 && hitbox.y < 100);
            } else if (ability == 2) {
                abilityTimer = ABILITY_TIMERS[2];
                abilityCooldown = ABILITY_COOLDOWNS[2];
                targetX = player.hitboxCenter.x;
                targetY = player.hitboxCenter.y;
                System.out.println("ENTERING ABILITY 2");
                for (int i = 0; i < 360; ++i) {
                    float angle = MathUtils.degreesToRadians * i;
                    bullets.add(new TimeProjectile(targetX + MathUtils.cos(angle) * 400, targetY + MathUtils.sin(angle) * 400, 4));
                }
                do {
                    float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
                    hitbox.x = targetX + MathUtils.cos(angle) * (400 + hitbox.radius);
                    hitbox.y = targetY + MathUtils.sin(angle) * (400 + hitbox.radius);
                } while (hitbox.x > 1100 && hitbox.y > 1100 && hitbox.x < 100 && hitbox.y < 100);
            }
        }

        if (ability == 1) {
            abilityCooldown -= frame;
            if (abilityCooldown <= 0 && abilityTimer > 3) {
                abilityCooldown = ABILITY_COOLDOWNS[1];
                for (int i = 0; i < 12; ++i) {
                    float angle = (i / 12.0f) * MathUtils.PI2 + modifier;
                    bullets.add(new BasicProjectile(targetX + MathUtils.cos(angle) * 400, targetY + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 150, -MathUtils.sin(angle) * 150));
                }
                modifier += MathUtils.PI / 48;
            }
        }

        if (ability == 2) {
            abilityCooldown -= frame;
            if (abilityCooldown <= 0) {
                abilityCooldown = ABILITY_COOLDOWNS[2];
                float angle = 0;
                boolean canShoot = false;
                if (abilityTimer > 3.0f) {
                    angle = MathUtils.PI / 4.0f;
                    canShoot = true;
                } else if (abilityTimer > 2.0f) {
                    angle = (3 * MathUtils.PI) / 4.0f;
                    canShoot = true;
                } else if (abilityTimer > 1.0f) {
                    angle = (5 * MathUtils.PI) / 4.0f;
                    canShoot = true;
                } else if (abilityTimer > 0.0f) {
                    angle = (7 * MathUtils.PI) / 4.0f;
                    canShoot = true;
                }
                if (canShoot) {
                    float nAngle = angle + MathUtils.PI / 2;
                    float nAngle2 = angle - MathUtils.PI / 2;
                    bullets.add(new BasicProjectile(targetX + 14.14f * MathUtils.cos(nAngle) + MathUtils.cos(angle) * 400, targetY + 14.14f * MathUtils.sin(nAngle) + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 300, -MathUtils.sin(angle) * 300));
                    bullets.add(new BasicProjectile(targetX + 14.14f * MathUtils.cos(nAngle2) + MathUtils.cos(angle) * 400, targetY + 14.14f * MathUtils.sin(nAngle2) + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 300, -MathUtils.sin(angle) * 300));
                }
            }
        }

        //Tick ability time
        abilityTimer -= frame;
        if (abilityTimer <= 0) {
            ability = 0;
            System.out.println("ENTERING ABILITY 0");
        }

        return bullets;
    }
}
