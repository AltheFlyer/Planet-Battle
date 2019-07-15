package com.battle.planet.enemies.moons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.BattleLevel;
import com.battle.planet.enemies.Saturn;
import com.battle.planet.projectiles.Projectile;

public class SaturnMoonA extends SaturnMoon {

    float cooldown;
    float MAX_COOLDOWN = 1.0f;

    public SaturnMoonA(Saturn s, BattleLevel lev, float x, float y) {
        super(s, lev, x, y, 50, 60, 100);
        setVelocity(120, 120);
        cooldown = MAX_COOLDOWN * 2;
    }

    @Override
    public Array<Projectile> attack(float frame) {
        this.clearProjectiles();
        //Shoot
        cooldown -= frame;

        if (cooldown <= 0) {
            createSpread(0, 10, MathUtils.PI * 2);
            cooldown = MAX_COOLDOWN;
        }

        //Move
        bounceMove(frame);
        return getBullets();
    }
}
