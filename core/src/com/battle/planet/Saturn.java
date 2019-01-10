package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Saturn extends Enemy {

    int moonsLeft = 53;//or 62 including unnamed.
    int phase = 0;

    public Saturn(BattleLevel lev, float x, float y) {
        super(lev, x, y, 100, 120, 2500);
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.GOLDENROD);
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public Array<Projectile> collide(Array<Projectile> projectiles) {
        if (phase == 0) {
            canSpawn = true;
        }
        if (phase == 1) {

        }

        return bullets;
    }

    @Override
    public Array<Enemy> spawn(float frame) {
        Array<Enemy> enemies = new Array<Enemy>();
        if (phase == 0) {
            enemies.add(new SaturnMoonA(level, hitbox.x, hitbox.y + 300));
            canSpawn = false;
            phase = 1;
        }
        return enemies;
    }


    public class SaturnMoonA extends Enemy {

        float cooldown = 0;
        float MAX_COOLDOWN = 1.0f;

        public SaturnMoonA(BattleLevel lev, float x, float y) {
            super(lev, x, y, 50, 60, 100);
        }

        @Override
        public void drawBody(ShapeRenderer r) {
            r.setColor(Color.GRAY);
            r.circle(hitbox.x, hitbox.y, hitbox.radius);
        }

        @Override
        public Array<Projectile> attack(float frame) {
            this.bullets.clear();
            cooldown -= frame;

            if (cooldown <= 0) {
                createSpread(0, 10, MathUtils.PI * 2);
                cooldown = MAX_COOLDOWN;
            }
            return this.bullets;
        }
    }
}
