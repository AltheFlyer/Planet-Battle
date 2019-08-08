package com.battle.planet.enemies;

import com.badlogic.gdx.graphics.Color;
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
            if (dst2 < 600 * 600) {
                //Note: 9000000 = 300 * 300 * 300
                //the force of 'gravity' is 300 pixels/s/s at a distance of 300
                p.velocity.add(new Vector2(MathUtils.cos(theta) * Math.min(500, 27000000 / (dst2)) * frame, MathUtils.sin(theta) * Math.min(500, 9000000 / (dst2)) * frame));
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
        r.setColor(Color.WHITE);
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public void drawHealthBars(ShapeRenderer r) {
    }

    /**
     * Collides with ALL bullets
     * @param projectiles The array of player bullets in the level.
     * @return
     */
    @Override
    public Array<Projectile> collide(Array<Projectile> projectiles) {
        for (Projectile p : getLevel().enemyBullets) {
            if (!p.isDestroyed && p.overlaps(hitbox)) {
                p.isDestroyed = true;
            }
        }
        for (Projectile p : projectiles) {
            if (!p.isDestroyed && p.overlaps(hitbox)) {
                p.isDestroyed = true;
            }
        }
        return projectiles;
    }
}
