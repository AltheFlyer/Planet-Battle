package com.battle.planet.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.battle.planet.PlanetBattle;

public class DesktopLauncher {
	//If this file dies, re-download jdk (looking at you kevin) hello allen
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 600;
		config.height = 600;
		new LwjglApplication(new PlanetBattle(), config);
	}
}
