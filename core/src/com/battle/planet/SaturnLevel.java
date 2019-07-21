package com.battle.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.battle.planet.enemies.Saturn;
import com.battle.planet.projectiles.Projectile;

import java.util.Iterator;

public class SaturnLevel extends BattleLevel {
    public SaturnLevel(PlanetBattle g, int abilitySelection) {
        super(g, abilitySelection);
        enemies.add(new Saturn(this, 600, 600));
        LEVEL_HEIGHT = 1200;
        LEVEL_WIDTH = 1200;
        player.setPosition(600, 300);
        camera.position.x = player.getCenterX();
        camera.position.y = player.getCenterY();
    }

    @Override
    public void prepareValues() {
        frame = Gdx.graphics.getDeltaTime();
        if (frame > 0.2f) {
            frame = 0.2f;
        }

        frame *= ((Saturn) enemies.get(0)).timeMultiplier;
        mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        player.updatePosition();
    }

    public void removeBullets() {
        //Remove offscreen player bullets
        Iterator<Projectile> iterE = enemyBullets.iterator();
        while (iterE.hasNext()) {
            Projectile p = iterE.next();
            if (p.isDestroyed || p.hitbox.x < -300 || p.hitbox.x > LEVEL_WIDTH + 300 || p.hitbox.y < -300 || p.hitbox.y > LEVEL_HEIGHT + 300) {
                iterE.remove();
            }
        }

        Iterator<Projectile> iterF = playerBullets.iterator();
        while (iterF.hasNext()) {
            Projectile p = iterF.next();
            if (p.isDestroyed || p.hitbox.x < -300 || p.hitbox.x > LEVEL_WIDTH + 300 || p.hitbox.y < -300 || p.hitbox.y > LEVEL_HEIGHT + 300) {
                iterF.remove();
            }
        }
    }
}
