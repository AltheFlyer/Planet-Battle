package com.battle.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class LevelSelectScreen implements Screen {

    final PlanetBattle game;
    Rectangle marsButton;
    Rectangle mercuryButton;

    OrthographicCamera camera;
    Vector3 mouse;

    public LevelSelectScreen(final PlanetBattle g) {
        game = g;
        marsButton = new Rectangle(50, 50, 50, 50);
        mercuryButton = new Rectangle(150, 50, 50, 50);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 600, 600);
        mouse = new Vector3();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.render.begin();
        game.render.set(ShapeRenderer.ShapeType.Filled);
        game.render.rect(50, 50, 50, 50);
        game.render.rect(150, 50, 50, 50);
        game.render.end();

        mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        if (Gdx.input.isTouched() && marsButton.contains(mouse.x, mouse.y)) {
            game.setScreen(new MarsLevel(game));
            dispose();
        } else if (Gdx.input.isTouched() && mercuryButton.contains(mouse.x, mouse.y)) {
            game.setScreen(new MercuryLevel(game));
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
