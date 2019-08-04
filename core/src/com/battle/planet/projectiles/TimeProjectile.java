package com.battle.planet.projectiles;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.battle.planet.BattleLevel;

public class TimeProjectile extends Projectile {

    public float lifespan;

    public TimeProjectile(final BattleLevel lev, Rectangle r, float life) {
        super(lev, r, new Vector2(0, 0));
        lifespan = life;
    }

    public TimeProjectile(final BattleLevel lev, float x, float y, float life) {
        super(lev, x, y, 0, 0);
        lifespan = life;
    }

    public TimeProjectile(final BattleLevel lev, Rectangle r, Vector2 v, float life) {
        super(lev, r, v);
        lifespan = life;
    }

    public TimeProjectile(final BattleLevel lev, float x, float y, float vx, float vy, float life) {
        super(lev, x, y, vx, vy);
        lifespan = life;
    }

    @Override
    public void move(float frame) {
        if (lifespan < 0) {
            isDestroyed = true;
        }
        lifespan -= frame;
        hitbox.x += velocity.x * frame;
        hitbox.y += velocity.y * frame;
    }

    @Override
    public void draw(ShapeRenderer r) {
        super.draw(r);
    }
}
