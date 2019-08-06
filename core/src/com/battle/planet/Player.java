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

    private Rectangle hitbox;
    private Vector2 hitboxCenter;
    private final BattleLevel level;

    private final float PRIMARY_COOLDOWN = 0.08f;
    private final float SECONDARY_COOLDOWN;
    private float cooldown;
    private float secondCooldown;
    private float speed = 200;
    private float MAX_INVINCIBLE = 1f;
    private float invincible = 0;

    private int specialValue;

    public Player(final BattleLevel lev, float x, float y, int special) {
        level = lev;
        hitbox = new Rectangle(x, y, 5, 5);
        hitboxCenter = new Vector2(0, 0);
        float cd = 0;
        if (special == 0) {
            cd = 2.0f;
        } else if (special == 1) {
            cd = 6.0f;
        } else if (special == 2) {
            cd = 4.0f;
        } else if (special == 3) {
            //This works differently from the others.
            cd = 6.0f;
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
            for (int i = 0; i < 9; ++i) {
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

    public float getCenterX() {
        return hitboxCenter.x;
    }

    public float getCenterY() {
        return hitboxCenter.y;
    }

    public void setPosition(float x, float y) {
        hitbox.setPosition(x - hitbox.width / 2, y - hitbox.height / 2);
        hitboxCenter.set(x, y);
    }

    /**
     * Updates hitbox center to center of hitbox position
     */
    public void updatePosition() {
        hitbox.getCenter(hitboxCenter);
    }

    public float getX() {
        return hitbox.x;
    }

    public float getY() {
        return hitbox.y;
    }

    public void setX(float x) {
        hitbox.x = x;
        updatePosition();
    }

    public void setY(float y) {
        hitbox.y = y;
        updatePosition();
    }


    public float getWidth() {
        return hitbox.width;
    }

    public float getHeight() {
        return hitbox.height;
    }

    public void moveX(float x) {
        hitbox.x += x;
        updatePosition();
    }

    public void moveY(float y) {
        hitbox.y += y;
        updatePosition();
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float spd) {
        speed = spd;
    }

    public float getCooldown() {
        return cooldown;
    }

    public float getSecondCooldown() {
        return secondCooldown;
    }

    public float getMaxSecondaryCooldown() {
        return SECONDARY_COOLDOWN;
    }

    public void tickCooldown(float frame) {

    }

    public void resetCooldown() {
        cooldown = PRIMARY_COOLDOWN;
    }

    public float getInvincible() {
        return invincible;
    }

    public void setInvincible() {
        invincible = MAX_INVINCIBLE;
    }

    public void tickInvincible(float frame) {
        invincible -= frame;
    }

    public int getSpecialValue() {
        return specialValue;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public Vector2 getHitboxCenter() {
        return hitboxCenter;
    }
}
