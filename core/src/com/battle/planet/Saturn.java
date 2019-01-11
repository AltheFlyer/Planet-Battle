package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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
    public Array<Projectile> attack(float frame) {
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
            enemies.add(new SaturnMoonB(level, hitbox.x, hitbox.y + 300));
            canSpawn = false;
            phase = 1;
        }
        return enemies;
    }


    public class SaturnMoonA extends Enemy {

        float cooldown = 0;
        float MAX_COOLDOWN = 1.0f;
        Vector2 velocity;

        public SaturnMoonA(BattleLevel lev, float x, float y) {
            super(lev, x, y, 50, 60, 100);
            velocity = new Vector2(120, 120);
        }

        @Override
        public void drawBody(ShapeRenderer r) {
            r.setColor(Color.GRAY);
            r.circle(hitbox.x, hitbox.y, hitbox.radius);
        }

        @Override
        public Array<Projectile> attack(float frame) {
            this.bullets.clear();
            //Shoot
            cooldown -= frame;

            if (cooldown <= 0) {
                createSpread(0, 10, MathUtils.PI * 2);
                cooldown = MAX_COOLDOWN;
            }

            //Move
            this.hitbox.x += this.velocity.x * frame;
            this.hitbox.y += this.velocity.y * frame;
            if (this.hitbox.x - this.hitbox.radius < 0) {
                this.hitbox.x = this.hitbox.radius;
                this.velocity.x *= -1;
            }
            if (this.hitbox.x + this.hitbox.radius > level.LEVEL_WIDTH) {
                this.hitbox.x = level.LEVEL_WIDTH - this.hitbox.radius;
                this.velocity.x *= -1;
            }
            if (this.hitbox.y - this.hitbox.radius < 0) {
                this.hitbox.y = this.hitbox.radius;
                this.velocity.y *= -1;
            }
            if (this.hitbox.y + this.hitbox.radius > level.LEVEL_HEIGHT) {
                this.hitbox.y = level.LEVEL_HEIGHT - this.hitbox.radius;
                this.velocity.y *= -1;
            }
            return this.bullets;
        }
    }

    public class SaturnMoonB extends Enemy {

        float cooldown = 0.25f;
        float MINI_COOLDOWN = 0.25f;
        float MAX_COOLDOWN = 1.0f;
        float shots = 4;

        Vector2 velocity;

        public SaturnMoonB(BattleLevel lev, float x, float y) {
            super(lev, x, y, 50, 60, 100);
            velocity = new Vector2(-120, 120);
        }

        @Override
        public void drawBody(ShapeRenderer r) {
            r.setColor(Color.GRAY);
            r.circle(hitbox.x, hitbox.y, hitbox.radius);
        }

        @Override
        public Array<Projectile> attack(float frame) {
            this.bullets.clear();
            //Shoot
            cooldown -= frame;

            if (cooldown <= 0 && shots > 0) {
                float angle = MathUtils.atan2(player.hitboxCenter.y - this.hitbox.y, player.hitboxCenter.x - this.hitbox.x);
                this.bullets.add(new BasicProjectile(level, hitbox.x, hitbox.y, MathUtils.cos(angle) * 260, MathUtils.sin(angle) * 260));
                cooldown = MINI_COOLDOWN;
                --shots;
            } else if (shots == 0) {
                cooldown = MAX_COOLDOWN;
                shots = 4;
            }

            //Move
            this.hitbox.x += this.velocity.x * frame;
            this.hitbox.y += this.velocity.y * frame;
            if (this.hitbox.x - this.hitbox.radius < 0) {
                this.hitbox.x = this.hitbox.radius;
                this.velocity.x *= -1;
            }
            if (this.hitbox.x + this.hitbox.radius > level.LEVEL_WIDTH) {
                this.hitbox.x = level.LEVEL_WIDTH - this.hitbox.radius;
                this.velocity.x *= -1;
            }
            if (this.hitbox.y - this.hitbox.radius < 0) {
                this.hitbox.y = this.hitbox.radius;
                this.velocity.y *= -1;
            }
            if (this.hitbox.y + this.hitbox.radius > level.LEVEL_HEIGHT) {
                this.hitbox.y = level.LEVEL_HEIGHT - this.hitbox.radius;
                this.velocity.y *= -1;
            }
            return this.bullets;
        }
    }

}
