package com.battle.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class LevelSelectScreen implements Screen {

    final PlanetBattle game;
    Array<Rectangle> buttons;
    OrthographicCamera camera;
    Vector3 mouse;

    public LevelSelectScreen(final PlanetBattle g) {
        game = g;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 600, 600);
        mouse = new Vector3();

        buttons = new Array<Rectangle>();
        buttons.add(new Rectangle(50, 500, 50, 50));
        buttons.add(new Rectangle(150, 500, 50, 50));
        buttons.add(new Rectangle(250, 500, 50, 50));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.render.begin();
        game.render.set(ShapeRenderer.ShapeType.Filled);
        game.render.setColor(Color.WHITE);
        //Draw Level Buttons
        for (Rectangle button: buttons) {
            game.render.rect(button.x, button.y, button.width, button.height);
        }
        game.render.end();

        mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        //Check for button press to load levels
        for (int i=0; i < buttons.size; ++i) {
            if (Gdx.input.isTouched() && buttons.get(i).contains(mouse.x, mouse.y)) {
                switch(i) {
                    case 0:
                        game.setScreen(new MarsLevel(game));
                        break;
                    case 1:
                        game.setScreen(new MercuryLevel(game));
                        break;
                    case 2:
                        game.setScreen(new VenusLevel(game));
                        break;
                }
                dispose();
            }
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
