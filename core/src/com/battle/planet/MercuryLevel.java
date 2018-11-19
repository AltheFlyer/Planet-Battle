package com.battle.planet;

public class MercuryLevel extends BattleLevel {

    public MercuryLevel(final PlanetBattle g) {
        super(g);
        enemies.add(new Mercury(300, 500));
    }

    @Override
    public void render(float delta) {
        prepareGraphics();

        prepareValues();

        draw();

        movePlayer();

        playerShoot();

        enemyActions();

        removeBullets();

        removeEnemies();

        checkWin();
    }

}
