package com.battle.planet;

public class MercuryLevel extends BattleLevel {

    public MercuryLevel(final PlanetBattle g, int abilitySelection) {
        super(g, abilitySelection);
        enemies.add(new Mercury(this, 300, 500));
    }

}
