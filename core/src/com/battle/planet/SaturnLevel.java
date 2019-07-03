package com.battle.planet;

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
}
