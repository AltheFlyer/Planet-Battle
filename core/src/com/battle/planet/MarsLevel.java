package com.battle.planet;

public class MarsLevel extends BattleLevel {

	public MarsLevel(final PlanetBattle g) {
		super(g);
		enemies.add(new Mars(300, 300, player));
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

