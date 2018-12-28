package com.battle.planet;

public class VenusLevel extends BattleLevel {

    public VenusLevel(PlanetBattle g) {
        super(g);
        enemies.add(new Venus(this, 300, 300));
        player.hitbox.setPosition(150, 300);
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
