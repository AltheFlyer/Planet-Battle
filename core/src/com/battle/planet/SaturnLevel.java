package com.battle.planet;

public class SaturnLevel extends BattleLevel {
    public SaturnLevel(PlanetBattle g, int abilitySelection) {
        super(g, abilitySelection);
        enemies.add(new Saturn(this, 600, 600));
        LEVEL_HEIGHT = 1200;
        LEVEL_WIDTH = 1200;
    }
}
