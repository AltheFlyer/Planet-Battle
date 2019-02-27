package com.battle.planet.projectiles;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.battle.planet.BattleLevel;

public class DelayProjectile extends Projectile {

    public float delay;
    public boolean moving = false;
    public float speed;

    public DelayProjectile(final BattleLevel lev, Rectangle r, float d) {
        super(lev, r, new Vector2(0, 0));
        delay = d;
        speed = 200;
    }

    public DelayProjectile(final BattleLevel lev, Rectangle r, float d, float v) {
        super(lev, r, new Vector2(0, 0));
        delay = d;
        speed = v;
    }

    public DelayProjectile(final BattleLevel lev, float x, float y, float d) {
        this(lev, new Rectangle(x, y, 5, 5), d, 200);
    }

    public DelayProjectile(final BattleLevel lev, Rectangle r, float vx, float vy, float d, float v) {
        super(lev, r, new Vector2(vx, vy));
        delay = d;
        speed = v;
    }

    @Override
    public void move(float x, float y, float frame) {
        if (delay > 0) {
            delay -= frame;
            hitbox.x += velocity.x * frame;
            hitbox.y += velocity.y * frame;
        }
        if (delay <= 0 && !moving) {
            moving = true;
            float angle = MathUtils.atan2(y - hitbox.y, x - hitbox.x);
            velocity = new Vector2(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed);
        }
        if (moving) {
            super.move(frame);
        }

    }
}
