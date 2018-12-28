package com.battle.planet;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BorderScatterProjectile extends Projectile {

    //Amount of projectiles to scatter into
    int amount;
    //Which border to check impact with:
    int impact;

    //In radians
    float angle = 120 * MathUtils.degreesToRadians;

    /**
     * A Projectile that move and scatters into Basic Projectiles on impact with a screen edge
     * @param lev
     * @param x
     * @param y
     * @param vx
     * @param vy
     * @param scatter Amount of projectiles to burst into
     * @param wall The wall that impact is checked with: 0: Top, 1: Right, 2: Bottom, 3: Left
     */
    public BorderScatterProjectile(BattleLevel lev, float x, float y, float vx, float vy, int scatter, int wall) {
        super(lev, x, y, vx, vy);
        amount = scatter;
        impact = wall;
    }

    public BorderScatterProjectile(BattleLevel lev, Rectangle r, Vector2 v, int scatter, int wall) {
        super(lev, r, v);
        amount = scatter;
        impact = wall;
    }

    @Override
    public void move(float frame) {
        hitbox.x += velocity.x * frame;
        hitbox.y += velocity.y * frame;

        if (impact == 0 && hitbox.y > level.LEVEL_HEIGHT) {
            isDestroyed = true;
            for (int i = 0; i < amount; ++i) {
                float theta = (float) (i / amount) * angle - (angle / 2) - (MathUtils.PI / 2);
                level.enemyBullets.add(new BasicProjectile(level, hitbox.x, hitbox.y, MathUtils.cos(theta) * 200, MathUtils.sin(theta) * 200));
            }
        } else if (impact == 1 && hitbox.x > level.LEVEL_WIDTH) {
            isDestroyed = true;
            for (int i = 0; i < amount; ++i) {
                float theta = MathUtils.PI + ((float) i / (amount - 1)) * angle - (angle / 2);;
                level.enemyBullets.add(new BasicProjectile(level, hitbox.x, hitbox.y, MathUtils.cos(theta) * 200, MathUtils.sin(theta) * 200));
            }
        } else if (impact == 2 && hitbox.y < 0) {
            isDestroyed = true;
            for (int i = 0; i < amount; ++i) {
                float theta = (-MathUtils.PI / 2) + ((float) i / (amount - 1)) * angle - (angle / 2);;
                level.enemyBullets.add(new BasicProjectile(level, hitbox.x, hitbox.y, MathUtils.cos(theta) * 200, MathUtils.sin(theta) * 200));
            }
            for (int i = 0; i < amount; ++i) {
                float theta = (MathUtils.PI / 2) + ((float) i / (amount - 1)) * angle - (angle / 2);;
                level.enemyBullets.add(new BasicProjectile(level, hitbox.x, hitbox.y, MathUtils.cos(theta) * 200, MathUtils.sin(theta) * 200));
            }
        } else if (impact == 3 && hitbox.x < 0) {
            isDestroyed = true;
            for (int i = 0; i < amount; ++i) {
                float theta = ((float) i / (amount - 1)) * angle - (angle / 2);;
                level.enemyBullets.add(new BasicProjectile(level, hitbox.x, hitbox.y, MathUtils.cos(theta) * 200, MathUtils.sin(theta) * 200));
            }
        }
    }
}
