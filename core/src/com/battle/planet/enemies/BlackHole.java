package com.battle.planet.enemies;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.BattleLevel;
import com.battle.planet.projectiles.Projectile;

public class BlackHole extends Enemy {


    /**
     * @param lev
     * @param x   The x coordinate to start at
     * @param y   The y coordinate to start at
     */
    public BlackHole(BattleLevel lev, float x, float y) {
        super(lev, x, y, 25, 25, 100);
    }

    @Override
    public Array<Projectile> attack(float frame) {
        Array<Projectile> compositeProjectiles = new Array<Projectile>();
        compositeProjectiles.addAll(getLevel().enemyBullets);
        compositeProjectiles.addAll(getLevel().playerBullets);

        for (Projectile p: compositeProjectiles) {
            float theta = MathUtils.atan2(hitbox.y - p.hitbox.y, hitbox.x - p.hitbox.x);
            float dst2 = Vector2.dst2(hitbox.x, hitbox.y, p.hitbox.x, p.hitbox.y);
            if (dst2 < 400 * 400) {
                p.velocity.add(new Vector2(MathUtils.cos(theta) * Math.max(500, 1500 / (dst2)) * frame, MathUtils.sin(theta) * Math.max(500, 1500 / (dst2)) * frame));
            }
        }

        return new Array<Projectile>();
    }

    /**
     * Draws the planet sprite/shape render.
     *
     * @param r The shape renderer to draw with.
     */
    @Override
    public void drawBody(ShapeRenderer r) {

    }

    @Override
    public void drawHealthBars(ShapeRenderer r) {
    }
}
