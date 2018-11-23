package com.battle.planet;

public class VenusLevel extends BattleLevel {

    public VenusLevel(PlanetBattle g) {
        super(g);
        enemies.add(new Venus(300, 300));
    }


}
