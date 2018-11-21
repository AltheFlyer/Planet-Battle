package com.battle.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class BattleLevel implements Screen {

    final PlanetBattle game;

    //Graphics
    SpriteBatch batch;
    ShapeRenderer render;
    OrthographicCamera camera;

    //Player values
    Rectangle playerHitbox;
    Vector2 hitboxCenter;
    final float MAX_COOLDOWN = 0.08f;
    float cooldown;
    float playerSpeed = 200;

    //Other level things
    Array<Projectile> playerBullets;
    Array<Projectile> enemyBullets;
    Array<Enemy> enemies;

    //Arena values
    float LEVEL_WIDTH = 600;
    float LEVEL_HEIGHT = 600;
    float SCREEN_WIDTH = 600;
    float SCREEN_HEIGHT = 600;

    //Render function things
    float frame;
    Vector3 mouse;

    //Special controls
    boolean areHealthBarsVisible;

    public BattleLevel(final PlanetBattle g) {
        game = g;

        render = game.render;

        //initialize renders
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        //Initialize player
        playerHitbox = new Rectangle(0, 0, 10, 10);
        playerBullets = new Array<Projectile>();
        hitboxCenter = new Vector2(0, 0);

        //Initialize enemies
        enemyBullets = new Array<Projectile>();
        //enemyBullets.add(new WaveProjectile(0, 200, 60, 0, 10));
        enemies = new Array<Enemy>();

        //Initialize other
        frame = 0;
        mouse = new Vector3(0, 0, 0);

        //Special controls
        areHealthBarsVisible = true;
    }

    @Override
    public void render(float delta) {
        prepareGraphics();

        prepareValues();

        draw();

        movePlayer();

        playerShoot();

        enemyActions();

        removeBullets();

        removeEnemies();

        checkWin();

        specialControls();
    }

    public void prepareGraphics() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        render.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void prepareValues() {
        frame = Gdx.graphics.getDeltaTime();
        if (frame > 0.2f) {
            frame = 0.2f;
        }
        mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        playerHitbox.getCenter(hitboxCenter);
    }

    public void draw() {
        batch.begin();
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        render.begin();
        render.set(ShapeRenderer.ShapeType.Filled);
        render.setColor(Color.RED);

        //Draw all enemies
        for (Enemy e: enemies) {
            e.drawBody(render);
            e.drawObjects(hitboxCenter.x, hitboxCenter.y, render);
            if (areHealthBarsVisible) {
                e.drawHealthBars(render);
            }
        }

        //Check collisions (FOR NOW)
        render.setColor(Color.BLUE);
        for (Projectile p: enemyBullets) {
            if (playerHitbox.overlaps(p.hitbox)) {
                render.setColor(Color.RED);
            }
        }

        //Draw player
        drawRectangle(new Rectangle(playerHitbox.x - 5, playerHitbox.y - 5, 20, 20));
        //render.setColor(Color.GREEN);
        //drawRectangle(playerHitbox);

        //Draw player bullets, then enemy bullets
        render.setColor(Color.YELLOW);
        for (Projectile p: playerBullets) {
            p.move(hitboxCenter.x, hitboxCenter.y, frame);
            drawRectangle(p.hitbox);
            p.drawSpecial(render);
        }

        for (Projectile p: enemyBullets) {
            render.setColor(Color.RED);
            p.move(hitboxCenter.x, hitboxCenter.y, frame);
            drawRectangle(p.hitbox);
            p.drawSpecial(render);
        }

        render.end();

    }

    public void playerShoot() {
        //Player shooting
        if (/*Gdx.input.isTouched() && */cooldown <= 0) {
            float theta = MathUtils.atan2(mouse.y - playerHitbox.y, mouse.x - playerHitbox.x);
            float vx = MathUtils.cos(theta) * 400;
            float vy = MathUtils.sin(theta) * 400;

            playerBullets.add(new BasicProjectile(hitboxCenter.x, hitboxCenter.y, vx, vy));
            cooldown = MAX_COOLDOWN;
        }
        cooldown -= frame;
        if (cooldown < 0) {
            cooldown = 0;
        }
    }

    public void enemyActions() {
        //Enemy actions
        for (Enemy e: enemies) {
            enemyBullets.addAll(e.attack(hitboxCenter.x, hitboxCenter.y, frame));
            if (e.canSpawn) {
                enemies.addAll(e.spawn(hitboxCenter.x, hitboxCenter.y, frame));
            }
            e.collide(playerBullets);
        }
    }

    public void movePlayer() {
        //Player movements
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            playerHitbox.y += playerSpeed * frame;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerHitbox.x -= playerSpeed * frame;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playerHitbox.y -= playerSpeed * frame;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerHitbox.x += playerSpeed * frame;
        }

        //Keep player in bounds
        if (playerHitbox.x - 5 < 0) {
            playerHitbox.x = 5;
        } else if (playerHitbox.x + playerHitbox.height + 5 > LEVEL_WIDTH) {
            playerHitbox.x = LEVEL_WIDTH - playerHitbox.width - 5;
        }
        if (playerHitbox.y - 5 < 0) {
            playerHitbox.y = 5;
        } else if (playerHitbox.y + playerHitbox.height + 5 > LEVEL_HEIGHT) {
            playerHitbox.y = LEVEL_HEIGHT - playerHitbox.height - 5;
        }
    }

    public void removeBullets() {
        //Remove offscreen player bullets
        Iterator<Projectile> iterE = enemyBullets.iterator();
        while (iterE.hasNext()) {
            Projectile p = iterE.next();
            if (p.isDestroyed || p.hitbox.x < 0 || p.hitbox.x > SCREEN_WIDTH || p.hitbox.y < 0 || p.hitbox.y > SCREEN_HEIGHT) {
                iterE.remove();
            }
        }

        Iterator<Projectile> iterF = playerBullets.iterator();
        while (iterF.hasNext()) {
            Projectile p = iterF.next();
            if (p.isDestroyed || p.hitbox.x < 0 || p.hitbox.x > SCREEN_WIDTH || p.hitbox.y < 0 || p.hitbox.y > SCREEN_HEIGHT) {
                iterF.remove();
            }
        }
    }

    public void removeEnemies() {
        //Remove dead enemies
        Iterator<Enemy> iterEnemy = enemies.iterator();
        while (iterEnemy.hasNext()) {
            Enemy e = iterEnemy.next();
            if (e.health <= 0) {
                iterEnemy.remove();
            }
        }
    }

    public void checkWin() {
        if (enemies.size == 0) {
            game.setScreen(new LevelSelectScreen(game));
            dispose();
        }
    }

    public void specialControls() {
        //Toggle Health Bars
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            areHealthBarsVisible = !areHealthBarsVisible;
        }
        //Press either shift to toggle slow movement
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)) {
            //Toggles the speed
            if (playerSpeed == 200) {
                playerSpeed = 150;
            } else {
                playerSpeed = 200;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new LevelSelectScreen(game));
            dispose();
        }
        //Deal damage (go to Next phase)
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            for (Enemy e: enemies) {
                e.health -= 50;
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

    public void drawRectangle(Rectangle r) {
        render.box(r.x, r.y, 0, r.width, r.height, 0);
    }
}
