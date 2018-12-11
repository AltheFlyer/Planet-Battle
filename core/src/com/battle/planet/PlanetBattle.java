package com.battle.planet;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PlanetBattle extends Game {

    public SpriteBatch batch;
    public ShapeRenderer render;
    public BitmapFont font;

    @Override
    public void create() {
        render = new ShapeRenderer();
        render.setAutoShapeType(true);

        font = new BitmapFont(Gdx.files.internal("white_20px.fnt"), Gdx.files.internal("white_20px.png"), false);

        batch = new SpriteBatch();
        this.setScreen(new LevelSelectScreen(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        render.dispose();
    }
}
