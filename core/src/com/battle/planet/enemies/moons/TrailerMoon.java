package com.battle.planet.enemies.moons;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.BattleLevel;
import com.battle.planet.enemies.Saturn;
import com.battle.planet.projectiles.Projectile;
import com.battle.planet.projectiles.TimeProjectile;

public class TrailerMoon extends SaturnMoon {

    float velocity = 200f;
    float orbitRadius;

    public TrailerMoon(Saturn s, BattleLevel lev, float x, float y) {
        super(s, lev, x, y, 50, 60, 50);
        orbitRadius = (float) Math.sqrt((s.getHitbox().x - x) * (s.getHitbox().x - x) + (s.getHitbox().y - y) * (s.getHitbox().y - y));
    }

    @Override
    public Array<Projectile> attack(float frame) {
        this.clearProjectiles();

        //Passive Trail
        for (int i = 0;i < 5; ++i) {
            addProjectile(new TimeProjectile(
                    getLevel(),
                    getHitbox().x + MathUtils.random(-10, 10),
                    getHitbox().y + MathUtils.random(-10, 10),
                    1
            ));
        }

        //Move, find angle to add
        float currentAngle = MathUtils.atan2(getHitbox().y - saturn.getHitbox().y, getHitbox().x - saturn.getHitbox().x);
        float theta = currentAngle + (velocity * frame) / orbitRadius;
        getHitbox().setX(saturn.getHitbox().x + orbitRadius * MathUtils.cos(theta));
        getHitbox().setY(saturn.getHitbox().y + orbitRadius * MathUtils.sin(theta));

        return getBullets();
    }
}
