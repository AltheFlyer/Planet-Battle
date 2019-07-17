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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.enemies.Enemy;
import com.battle.planet.projectiles.BasicProjectile;
import com.battle.planet.projectiles.Projectile;

import java.util.Iterator;

public class BattleLevel implements Screen {

    final PlanetBattle game;

    //Graphics
    SpriteBatch batch;
    ShapeRenderer render;
    OrthographicCamera camera;

    //Player values
    public Player player;

    //Other level things
    public Array<Projectile> playerBullets;
    public Array<Projectile> enemyBullets;
    Array<Enemy> enemies;

    //Arena values
    public float LEVEL_WIDTH = 600;
    public float LEVEL_HEIGHT = 600;

    //Camera values
    public float SCREEN_WIDTH = 600;
    public float SCREEN_HEIGHT = 600;

    //Render function things
    public float frame;
    Vector3 mouse;

    //Special controls
    boolean areHealthBarsVisible;
    boolean autoShoot = true;

    public BattleLevel(final PlanetBattle g, int abilitySelection) {
        game = g;

        render = game.render;

        //initialize renders
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        //Initialize player
        player = new Player(this, 0, 0,abilitySelection);
        playerBullets = new Array<Projectile>();

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

        playerShoot();

        movePlayer();

        enemyActions();

        moveBullets();

        playerCollisions();

        removeBullets();

        removeEnemies();

        checkWin();

        specialControls();

        draw();

        player.tick(frame);
    }

    public void prepareGraphics() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        render.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Initialize frame delta, mouse, and player hitbox center
     */
    public void prepareValues() {
        frame = Gdx.graphics.getDeltaTime();
        if (frame > 0.2f) {
            frame = 0.2f;
        }
        mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        player.updatePosition();
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
            e.drawObjects(render);
            if (areHealthBarsVisible) {
                e.drawHealthBars(render);
            }
        }

        //Draw player
        if (player.getInvincible() <= 0) {
            render.setColor(Color.BLUE);
        } else {
            render.setColor(Color.RED);
        }
        drawRectangle(new Rectangle(player.getX() - 5, player.getY() - 5, 20, 20));
        //Draw secondary recharge
        //Back bar
        render.setColor(Color.GRAY);
        render.rect(
                player.getCenterX() - 20,
                player.getCenterY() - 20,
                40,
                10
        );
        //Charge amount
        render.setColor(Color.YELLOW);
        render.rect(
                player.getCenterX() - 20,
                player.getCenterY() - 20,
                40 * (1 - (player.getSecondCooldown() / player.getMaxSecondaryCooldown())),
                10
        );

        //Draw teleport range
        if (player.getSpecialValue() == 2) {
            render.set(ShapeRenderer.ShapeType.Line);
            render.setColor(Color.YELLOW);
            render.circle(player.getCenterX(), player.getCenterY(), 100);
        }
        //render.setColor(Color.GREEN);
        //drawRectangle(playerHitbox);

        //Draw player bullets, then enemy bullets
        render.set(ShapeRenderer.ShapeType.Filled);
        render.setColor(Color.YELLOW);
        for (Projectile p: playerBullets) {
            p.draw(render);
            //drawRectangle(p.hitbox);
            p.drawSpecial(render);
        }

        for (Projectile p: enemyBullets) {
            render.setColor(Color.RED);
            p.draw(render);
            //drawRectangle(p.hitbox);
            p.drawSpecial(render);
        }

        render.end();

    }

    /**
     * This is where mouse clicks (left, right) are checked to allow for
     * player primary and secondary fire/abilities
     */
    public void playerShoot() {
        //Player shooting
        if ((Gdx.input.isButtonPressed(Input.Buttons.LEFT) ^ autoShoot) && player.getCooldown() <= 0) {
            float theta = MathUtils.atan2(mouse.y - player.getY(), mouse.x - player.getX());
            float vx = MathUtils.cos(theta) * 400;
            float vy = MathUtils.sin(theta) * 400;

            playerBullets.add(new BasicProjectile(this, player.getCenterX(), player.getCenterY(), vx, vy));
            player.resetCooldown();
        }
        player.special(mouse.x, mouse.y);
    }

    public void enemyActions() {
        //Enemy actions
        for (Enemy e: enemies) {
            enemyBullets.addAll(e.attack(frame));
            if (e.getCanSpawn()) {
                enemies.addAll(e.spawn(frame));
            }
            e.move();
            e.collide(playerBullets);
        }
    }

    public void moveBullets() {
        for (Projectile p: playerBullets) {
            p.move(player.getCenterX(), player.getCenterY(), frame);
        }

        for (Projectile p: enemyBullets) {
            p.move(player.getCenterX(), player.getCenterY(), frame);
        }
    }

    public void movePlayer() {
        //Player movements
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.moveY(player.getSpeed() * frame);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.moveX(-player.getSpeed() * frame);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.moveY(-player.getSpeed() * frame);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.moveX(player.getSpeed() * frame);
        }

        //Keep player in bounds
        if (player.getX() - 5 < 0) {
            player.setX(5);
        } else if (player.getX() + player.getHeight() + 5 > LEVEL_WIDTH) {
            player.setX(LEVEL_WIDTH - player.getWidth() - 5);
        }
        if (player.getY() - 5 < 0) {
            player.setY(5);
        } else if (player.getY() + player.getHeight() + 5 > LEVEL_HEIGHT) {
            player.setY(LEVEL_HEIGHT - player.getHeight() - 5);
        }

        //Control camera
        camera.position.x = player.getX();
        camera.position.y = player.getY();
        if (camera.position.x - camera.viewportWidth / 2 < 0) {
            camera.position.x = camera.viewportWidth / 2;
        } else if (camera.position.x + camera.viewportWidth / 2 + 5 > LEVEL_WIDTH) {
            camera.position.x = LEVEL_WIDTH - camera.viewportWidth / 2;
        }
        if (camera.position.y - camera.viewportHeight / 2 < 0) {
            camera.position.y = camera.viewportHeight / 2;
        } else if (camera.position.y + camera.viewportHeight / 2 > LEVEL_HEIGHT) {
            camera.position.y = LEVEL_HEIGHT - camera.viewportHeight / 2;
        }
        //Keep hitbox center consistent
        player.updatePosition();
    }

    public void playerCollisions() {
        if (player.getInvincible() > 0) {
            player.tickInvincible(frame);
        }

        for (Projectile p: enemyBullets) {
            if (player.getInvincible() <= 0 && player.getHitbox().overlaps(p.hitbox)) {
                player.setInvincible();
            }
        }

        for (Enemy e: enemies) {
            if (player.getInvincible() <= 0 && e.getCollisionBox().contains(player.getHitboxCenter())) {
                player.setInvincible();
            }
        }
    }

    public void removeBullets() {
        //Remove offscreen player bullets
        Iterator<Projectile> iterE = enemyBullets.iterator();
        while (iterE.hasNext()) {
            Projectile p = iterE.next();
            if (p.isDestroyed || p.hitbox.x < -150 || p.hitbox.x > LEVEL_WIDTH + 150 || p.hitbox.y < -150 || p.hitbox.y > LEVEL_HEIGHT + 150) {
                iterE.remove();
            }
        }

        Iterator<Projectile> iterF = playerBullets.iterator();
        while (iterF.hasNext()) {
            Projectile p = iterF.next();
            if (p.isDestroyed || p.hitbox.x < -150 || p.hitbox.x > LEVEL_WIDTH + 150 || p.hitbox.y < -150 || p.hitbox.y > LEVEL_HEIGHT + 150) {
                iterF.remove();
            }
        }
    }

    public void removeEnemies() {
        //Remove dead enemies
        Iterator<Enemy> iterEnemy = enemies.iterator();
        while (iterEnemy.hasNext()) {
            Enemy e = iterEnemy.next();
            if (e.getHealth() <= 0) {
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
            if (player.getSpeed() == 200) {
                player.setSpeed(150);
            } else {
                player.setSpeed(200);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new LevelSelectScreen(game));
            dispose();
        }
        //Deal damage (go to Next phase)
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            for (Enemy e: enemies) {
                e.modHealth(-50);
            }
        }

        //Toggle autoshoot
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            autoShoot = !autoShoot;
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
