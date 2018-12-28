package com.battle.planet;

public class UranusLevel extends BattleLevel {

    public UranusLevel(PlanetBattle g) {
        super(g);
        LEVEL_HEIGHT = 1200;
        LEVEL_WIDTH = 1200;
        enemies.add(new Uranus(this, 600, 800));
        player.hitbox.x = 600 - player.hitbox.width / 2;
        player.hitbox.y = 600 - player.hitbox.height / 2;
    }
}
