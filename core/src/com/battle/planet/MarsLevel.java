package com.battle.planet;

public class MarsLevel extends BattleLevel {

	public MarsLevel(final PlanetBattle g) {
		super(g);
		enemies.add(new Mars(300, 300));
	}

	@Override
	public void render (float delta) {
		prepareGraphics();

		prepareValues();

		draw();

		movePlayer();

		playerShoot();

		enemyActions();
		//Enemy actions
		/*
		for (Enemy e: enemies) {
			enemyBullets.addAll(e.attack(hitboxCenter.x, hitboxCenter.y, frame));
			if (e.canSpawn) {
				enemies.addAll(e.spawn(hitboxCenter.x, hitboxCenter.y, frame));
			}
			e.move();
			if (e instanceof Mars && (((Mars) e).phase == 2)&& enemies.size == 1) {
				((Mars) e).phase = -2;
			} else {
				if (e instanceof Mars && (((Mars) e).phase == 2 || ((Mars) e).phase == -1)) {

				} else {
					e.collide(playerBullets);
				}
			}
		}
		*/

		playerCollisions();

		removeBullets();

		removeEnemies();

		checkWin();

		specialControls();
	}

	@Override
	public void show() {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose () {
		batch.dispose();
	}

}

