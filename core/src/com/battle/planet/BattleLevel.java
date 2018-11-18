package com.battle.planet;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class BattleLevel implements Screen {

    final PlanetBattle game;

    SpriteBatch batch;
    ShapeRenderer render;
    OrthographicCamera camera;

    Rectangle playerHitbox;
    Array<Projectile> playerBullets;
    Array<Projectile> enemyBullets;
    Vector2 hitboxCenter;
    final float MAX_COOLDOWN = 0.08f;
    float cooldown;
    Array<Enemy> enemies;
    float LEVEL_WIDTH = 600;
    float LEVEL_HEIGHT = 600;
    float SCREEN_WIDTH = 600;
    float SCREEN_HEIGHT = 600;

    public BattleLevel(final PlanetBattle g) {
        game = g;

        render = game.render;

        //initialize renders
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        //Initialize player
        playerHitbox = new Rectangle(0, 0, 20, 20);
        playerBullets = new Array<Projectile>();
        hitboxCenter = new Vector2(0, 0);

        //Initialize enemies
        enemyBullets = new Array<Projectile>();
        //enemyBullets.add(new WaveProjectile(0, 200, 60, 0, 10));
        enemies = new Array<Enemy>();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

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
