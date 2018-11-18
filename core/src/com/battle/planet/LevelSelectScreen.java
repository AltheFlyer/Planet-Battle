package com.battle.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class LevelSelectScreen implements Screen {

    final PlanetBattle game;
    Rectangle marsButton;

    public LevelSelectScreen(final PlanetBattle g) {
        game = g;
        marsButton = new Rectangle(50, 50, 50, 50);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.render.begin();
        game.render.set(ShapeRenderer.ShapeType.Filled);
        game.render.rect(50, 50, 50, 50);
        game.render.end();

        float cX = Gdx.input.getX();
        float cY = Gdx.input.getX();

        if (Gdx.input.isTouched() &&  marsButton.contains(cX, cY)) {
            game.setScreen(new MarsLevel(game));
            dispose();
        }
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
    public void dispose() {

    }
}
