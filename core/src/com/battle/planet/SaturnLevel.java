package com.battle.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.battle.planet.enemies.Saturn;

public class SaturnLevel extends BattleLevel {
    public SaturnLevel(PlanetBattle g, int abilitySelection) {
        super(g, abilitySelection);
        enemies.add(new Saturn(this, 600, 600));
        LEVEL_HEIGHT = 1200;
        LEVEL_WIDTH = 1200;
        player.setPosition(600, 400);
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
}
