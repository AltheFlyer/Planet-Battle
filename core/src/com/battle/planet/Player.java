package com.battle.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.projectiles.BasicProjectile;
import com.battle.planet.projectiles.Projectile;
import com.battle.planet.projectiles.TimeProjectile;

public class Player {

    Rectangle hitbox;
    Vector2 hitboxCenter;
    final BattleLevel level;

    final float PRIMARY_COOLDOWN = 0.08f;
    final float SECONDARY_COOLDOWN;
    float cooldown;
    float secondCooldown;
    float speed = 200;
    float MAX_INVINCIBLE = 1f;
    float invincible = 0;

    int specialValue;

    public Player(final BattleLevel lev, float x, float y, int special){
        level = lev;
        hitbox = new Rectangle(x, y, 10, 10);
        hitboxCenter = new Vector2(0, 0);
        float cd = 0;
        if (special == 0) {
            cd = 2.0f;
        } else if (special == 1) {
            cd = 6.0f;
        } else if (special == 2) {
            cd = 5.0f;
        } else if (special == 3) {
            //This works differently from the others.
            cd = 5.0f;
        }
        SECONDARY_COOLDOWN = secondCooldown = cd;
        specialValue = special;
    }

    public void tick(float frame) {
        cooldown -= frame;
        secondCooldown -= frame;
        if (cooldown < 0) {
            cooldown = 0;
        }
        if (secondCooldown < 0) {
            secondCooldown = 0;
        }
    }

    public void special(float x, float y) {
        boolean isPrepared = Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && secondCooldown <= 0;
        if (specialValue == 0 && isPrepared) {
            Array<Projectile> bullets = new Array<Projectile>();
            for (int i = 0; i < 8; ++i) {
                float theta = MathUtils.atan2(y - hitboxCenter.y, x - hitboxCenter.x) + MathUtils.random(-0.5f, 0.5f);
                bullets.add(
                        new TimeProjectile(
                                level,
                                hitboxCenter.x,
                                hitboxCenter.y,
                                400 * MathUtils.cos(theta),
                                400 * MathUtils.sin(theta),
                                0.4f
                        )
                );
            }
            level.playerBullets.addAll(bullets);
            secondCooldown = SECONDARY_COOLDOWN;
        } else if (specialValue == 1 && isPrepared) {
            Array<Projectile> bullets = new Array<Projectile>();
            float theta = MathUtils.atan2(y - hitboxCenter.y, x - hitboxCenter.x);
            float s = 150;
            //Shaft
            for (int i = 0; i <= 80; i += 5) {
                bullets.add(new BasicProjectile(level, hitboxCenter.x + i * MathUtils.cos(theta), hitboxCenter.y + i * MathUtils.sin(theta), s * MathUtils.cos(theta), s * MathUtils.sin(theta)));
            }

            //Sides
            for (int i = 5; i <= 25; i += 5) {
                bullets.add(new BasicProjectile(level, hitboxCenter.x + (80 - i) * MathUtils.cos(theta + i * 0.0174533f), hitboxCenter.y + (80 - i) * MathUtils.sin(theta + i * 0.0174533f), s * MathUtils.cos(theta), s * MathUtils.sin(theta)));
                bullets.add(new BasicProjectile(level, hitboxCenter.x + (80 - i) * MathUtils.cos(theta - i * 0.0174533f), hitboxCenter.y + (80 - i) * MathUtils.sin(theta - i * 0.0174533f), s * MathUtils.cos(theta), s * MathUtils.sin(theta)));
            }
            level.playerBullets.addAll(bullets);
            secondCooldown = SECONDARY_COOLDOWN;
        } else if (specialValue == 2 && isPrepared) {
            float theta = MathUtils.atan2(y - hitboxCenter.y, x - hitboxCenter.x);
            hitbox.x += MathUtils.cos(theta) * 100;
            hitbox.y += MathUtils.sin(theta) * 100;
            secondCooldown = SECONDARY_COOLDOWN;
        } else if (specialValue == 3 && secondCooldown <= SECONDARY_COOLDOWN && Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            secondCooldown += level.frame * 4;
            //Time slow doesn't work when very close to 0 cooldown
            if (secondCooldown > SECONDARY_COOLDOWN) {
                secondCooldown = SECONDARY_COOLDOWN;
            } else {
                level.frame /= 2;
            }
        }
    }

}
