package com.battle.planet.enemies.moons;

import com.battle.planet.enemies.Enemy;
import com.battle.planet.enemies.Saturn;

public class MoonFactory {

    private int moonsLeft = 53;
    private String[] moonNames = {

    };

    public Enemy generateMoon(Saturn s, float x, float y) {
        moonsLeft--;
        if (moonsLeft % 2 == 0) {
            return new SaturnMoonA(s, s.getLevel(), x, y);
        } else {
            return new ChaserMoon(s, s.getLevel(), x, y);
        }

    }

    public boolean canGenerateMoon() {
        return moonsLeft > 0;
    }
}
