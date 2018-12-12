package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Uranus extends Enemy {

    int phase = 0;

    float teleportCooldown;
    final float TELE_MAX_COOLDOWN = 1f;

    /**
     * @param x  The x coordinate to start at
     * @param y  The y coordinate to start at
     */
    public Uranus(float x, float y) {
        super(x, y, 100, 120, 1500);

        teleportCooldown = TELE_MAX_COOLDOWN;
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        r.setColor(Color.valueOf("#12e8b9"));
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public Array<Projectile> attack(Player player, float frame) {
        bullets.clear();
        teleportCooldown -= frame;
        if (teleportCooldown <= 0) {
            teleportCooldown = TELE_MAX_COOLDOWN;
            float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
            hitbox.x = player.hitbox.x + MathUtils.cos(angle) * 300;
            hitbox.y = player.hitbox.y + MathUtils.sin(angle) * 300;

            angle = MathUtils.atan2(player.hitbox.y - hitbox.y, player.hitbox.x - hitbox.x);
            bullets.add(new BasicProjectile(hitbox.x, hitbox.y, MathUtils.cos(angle) * 200, MathUtils.sin(angle) * 200));
        }

        return bullets;
    }
}
