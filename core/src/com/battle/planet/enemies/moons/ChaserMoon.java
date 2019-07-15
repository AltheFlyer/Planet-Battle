package com.battle.planet.enemies.moons;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.BattleLevel;
import com.battle.planet.enemies.Saturn;
import com.battle.planet.projectiles.Projectile;

public class ChaserMoon extends SaturnMoon {

    Vector2 velocity;
    Array<Vector2> trail;

    public ChaserMoon(Saturn s, BattleLevel lev, float x, float y) {
        super(s, lev, x, y, 40, 50, 100);
        velocity = new Vector2();

        trail = new Array<Vector2>();
    }

    @Override
    public void drawObjects(ShapeRenderer r) {
        r.setColor(50, 50, 50, 0.05f);
        for (Vector2 v: trail) {
            r.circle(v.x, v.y, hitbox.radius);
        }
    }

    @Override
    public Array<Projectile> attack(float frame) {
        getBullets().clear();
        float theta = MathUtils.atan2(getPlayer().getHitbox().y - getHitbox().y, getPlayer().getHitbox().x - getHitbox().x);

        Vector2 accel = new Vector2(10 * MathUtils.cos(theta), 10 * MathUtils.sin(theta));

        velocity.add(accel);

        if (velocity.dst2(0, 0) > 40000) {
            velocity.clamp(200, 200);
        }

        hitbox.x += velocity.x * frame;
        hitbox.y += velocity.y * frame;

        trail.add(new Vector2(hitbox.x, hitbox.y));
        if (trail.size > 10) {
            trail.removeIndex(0);
        }
        return getBullets();
    }
}
