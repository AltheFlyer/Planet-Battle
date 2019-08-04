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

    //The list of secondary abilities
    Array<Rectangle> secondaries;
    //The selected secondary
    int abilitySelection;

    String[] abilityTitles = {
        "Shotgun",
        "Mars' Spear",
        "Mercurial Dash",
        "Peace of Mind"
    };

    String[] abilityText = {
            "Creates a short range burst of projectiles.",
            "Creates a slow, but powerful spear.",
            "Do a short range teleport.",
            "Slows down time."
    };

    public LevelSelectScreen(final PlanetBattle g) {
        game = g;

        abilitySelection = game.selectedPlayerAbility;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 600, 600);
        mouse = new Vector3();

        buttons = new Array<Rectangle>();
        buttons.add(new Rectangle(50, 500, 50, 50));
        buttons.add(new Rectangle(150, 500, 50, 50));
        buttons.add(new Rectangle(250, 500, 50, 50));

        buttons.add(new Rectangle(50, 350, 50, 50));
        buttons.add(new Rectangle(150, 350, 50, 50));
        buttons.add(new Rectangle(250, 350, 50, 50));

        secondaries = new Array<Rectangle>();

        secondaries.add(new Rectangle(50, 50, 50, 50));
        secondaries.add(new Rectangle(150, 50, 50, 50));
        secondaries.add(new Rectangle(250, 50, 50, 50));
        secondaries.add(new Rectangle(350, 50, 50, 50));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.render.setProjectionMatrix(camera.combined);
        game.batch.setProjectionMatrix(camera.combined);

        game.render.begin();
        game.render.set(ShapeRenderer.ShapeType.Filled);
        game.render.setColor(Color.WHITE);
        //Draw Level Buttons
        for (Rectangle button: buttons) {
            game.render.rect(button.x, button.y, button.width, button.height);
        }

        for (int i = 0; i < secondaries.size; ++i) {
            game.render.rect(secondaries.get(i).x, secondaries.get(i).y, secondaries.get(i).width, secondaries.get(i).height);
        }

        game.render.end();


        game.batch.begin();

        //Level Titles
        game.font.draw(game.batch, LevelText.MARS_TITLE, 50, 500);
        game.font.draw(game.batch, LevelText.MERCURY_TITLE, 150, 500);
        game.font.draw(game.batch, LevelText.VENUS_TITLE, 250, 500);

        game.font.draw(game.batch, LevelText.SATURN_TITLE, 50, 350);
        game.font.draw(game.batch, LevelText.URANUS_TITLE, 150, 350);
        game.font.draw(game.batch, LevelText.NEPTUNE_TITLE, 250, 350);

        mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        //Check for button press to load levels
        for (int i=0; i < buttons.size; ++i) {
            if (buttons.get(i).contains(mouse.x, mouse.y)) {
                //Level selection info/text display
                switch (i) {
                    case 0:
                        game.font.draw(game.batch, LevelText.MARS_TITLE, 400, 500, 190, 1, true);
                        game.font.draw(game.batch, LevelText.MARS_SUBTITLE, 400, 470, 190, 1, true);
                        game.font.draw(game.batch, LevelText.MARS_TEXT, 400, 400, 190, 1, true);
                        break;
                    case 1:
                        game.font.draw(game.batch, LevelText.MERCURY_TITLE, 400, 500, 190, 1, true);
                        game.font.draw(game.batch, LevelText.MERCURY_SUBTITLE, 400, 470, 190, 1, true);
                        game.font.draw(game.batch, LevelText.MERCURY_TEXT, 400, 400, 190, 1, true);
                        break;
                    case 2:
                        game.font.draw(game.batch, LevelText.VENUS_TITLE, 400, 500, 190, 1, true);
                        game.font.draw(game.batch, LevelText.VENUS_SUBTITLE, 400, 470, 190, 1, true);
                        game.font.draw(game.batch, LevelText.VENUS_TEXT, 400, 400, 190, 1, true);
                        break;
                }
                //Level selection
                if (Gdx.input.isTouched()) {
                    switch (i) {
                        case 0:
                            game.setScreen(new MarsLevel(game, abilitySelection));
                            break;
                        case 1:
                            game.setScreen(new MercuryLevel(game, abilitySelection));
                            break;
                        case 2:
                            game.setScreen(new VenusLevel(game, abilitySelection));
                            break;
                        case 3:
                            game.setScreen(new SaturnLevel(game, abilitySelection));
                            break;
                        case 4:
                            game.setScreen(new UranusLevel(game, abilitySelection));
                            break;
                    }
                    dispose();
                }
            }
        }

        //Check for button press for player secondaries
        for (int i = 0; i < secondaries.size; ++i) {
            if (secondaries.get(i).contains(mouse.x, mouse.y)) {
                game.font.draw(game.batch, abilityTitles[i], 400, 500, 190, 1, true);
                game.font.draw(game.batch, abilityText[i], 400, 400, 190, 1, true);
                if (Gdx.input.isTouched()) {
                    abilitySelection = i;
                    game.selectedPlayerAbility = i;
                }
            }
        }

        //Display selected secondary
        game.font.draw(game.batch, abilityTitles[abilitySelection], 100, 200, 400, -1, true);

        game.batch.end();
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
