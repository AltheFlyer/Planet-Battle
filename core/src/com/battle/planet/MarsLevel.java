package com.battle.planet;

import com.battle.planet.enemies.Mars;

public class MarsLevel extends BattleLevel {

	public MarsLevel(final PlanetBattle g, int abilitySelection) {
		super(g, abilitySelection);
		enemies.add(new Mars(this, 300, 300));
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

