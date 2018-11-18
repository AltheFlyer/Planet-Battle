package com.battle.planet;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PlanetBattle extends Game {

    public ShapeRenderer render;

    @Override
    public void create() {
        render = new ShapeRenderer();
        render.setAutoShapeType(true);
        this.setScreen(new LevelSelectScreen(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        render.dispose();
    }
}
