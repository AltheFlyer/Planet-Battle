package com.battle.planet;

import com.battle.planet.enemies.Uranus;

public class UranusLevel extends BattleLevel {

    public UranusLevel(PlanetBattle g, int abilitySelection) {
        super(g, abilitySelection);
        LEVEL_HEIGHT = 1200;
        LEVEL_WIDTH = 1200;
        enemies.add(new Uranus(this, 600, 800));
        player.setPosition(600, 600);
    }
}
