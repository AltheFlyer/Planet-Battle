package com.battle.planet.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.BattleLevel;
import com.battle.planet.projectiles.DelayProjectile;
import com.battle.planet.projectiles.Projectile;

public class NewUranus extends Enemy {

    int phase;
    float prePhaseCooldown;
    float phaseZeroClock = 0;
    final float PHASE_ZERO_LENGTH = 8;

    final float MAX_SEEKER_COOLDOWN = 1f;
    float seekerCooldown;

    /**
     * @param x  The x coordinate to start at
     * @param y  The y coordinate to start at
     */
    public NewUranus(final BattleLevel lev, float x, float y) {
        super(lev, x, y, 100, 120, 1500);

        phase = 0;
        seekerCooldown = MAX_SEEKER_COOLDOWN;
    }

    /**
     * Draws the planet sprite/shape render.
     *
     * @param r The shape renderer to draw with.
     */
    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.valueOf("#12e8b9"));
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public Array<Projectile> attack(float frame) {
        getBullets().clear();
        if (phase == 0) {
            phaseZeroClock += frame;
            if (phaseZeroClock > 3) {
                seekerCooldown -= frame;
                if (seekerCooldown <= 0) {
                    seekerCooldown = MAX_SEEKER_COOLDOWN;
                    for (int i = 0; i < 7; ++i) {
                        float size = MathUtils.random(10, 25);
                        addProjectile(new DelayProjectile(
                                        getLevel(),
                                        new Rectangle(hitbox.x, hitbox.y, size, size),
                                        0, MathUtils.random(110, 500),
                                        1, 300
                                )
                        );
                        size = MathUtils.random(10, 25);
                        addProjectile(new DelayProjectile(
                                        getLevel(),
                                        new Rectangle(hitbox.x, hitbox.y, size, size),
                                        0, -MathUtils.random(110, 500),
                                        1, 300
                                )
                        );
                    }
                }
            }
            if (phaseZeroClock > PHASE_ZERO_LENGTH) {
                phase = 1;
            }
        }

        return getBullets();
    }
}
