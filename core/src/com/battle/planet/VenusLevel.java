package com.battle.planet;

import com.battle.planet.enemies.Enemy;
import com.battle.planet.enemies.Venus;

public class VenusLevel extends BattleLevel {

    public VenusLevel(PlanetBattle g, int abilitySelection) {
        super(g, abilitySelection);
        enemies.add(new Venus(this, 300, 300));
        player.setPosition(150, 300);
    }

    @Override
    public void checkWin() {
        boolean win = true;
        for (Enemy e: enemies) {
            if (e.getClass() == Venus.class) {
                win = false;
            }
        }
        if (win) {
            game.setScreen(new LevelSelectScreen(game));
            dispose();
        }
    }
}
