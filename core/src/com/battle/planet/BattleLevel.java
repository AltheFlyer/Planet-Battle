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
    Player player;

    //Other level things
    Array<Projectile> playerBullets;
    Array<Projectile> enemyBullets;
    Array<Enemy> enemies;

    //Arena values
    float LEVEL_WIDTH = 600;
    float LEVEL_HEIGHT = 600;

    //Camera values
    float SCREEN_WIDTH = 600;
    float SCREEN_HEIGHT = 600;

    //Render function things
    float frame;
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
        player.hitbox.getCenter(player.hitboxCenter);
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
        if (player.invincible <= 0) {
            render.setColor(Color.BLUE);
        } else {
            render.setColor(Color.RED);
        }
        drawRectangle(new Rectangle(player.hitbox.x - 5, player.hitbox.y - 5, 20, 20));
        //Draw secondary recharge
        //Back bar
        render.setColor(Color.GRAY);
        render.rect(
                player.hitboxCenter.x - 20,
                player.hitboxCenter.y - 20,
                40,
                10
        );
        //Charge amount
        render.setColor(Color.YELLOW);
        render.rect(
                player.hitboxCenter.x - 20,
                player.hitboxCenter.y - 20,
                40 * (1 - (player.secondCooldown / player.SECONDARY_COOLDOWN)),
                10
        );

        //Draw teleport range
        if (player.specialValue == 2) {
            render.set(ShapeRenderer.ShapeType.Line);
            render.setColor(Color.YELLOW);
            render.circle(player.hitboxCenter.x, player.hitboxCenter.y, 100);
        }
        //render.setColor(Color.GREEN);
        //drawRectangle(playerHitbox);

        //Draw player bullets, then enemy bullets
        render.set(ShapeRenderer.ShapeType.Filled);
        render.setColor(Color.YELLOW);
        for (Projectile p: playerBullets) {
            drawRectangle(p.hitbox);
            p.drawSpecial(render);
        }

        for (Projectile p: enemyBullets) {
            render.setColor(Color.RED);
            drawRectangle(p.hitbox);
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
        if ((Gdx.input.isButtonPressed(Input.Buttons.LEFT) ^ autoShoot) && player.cooldown <= 0) {
            float theta = MathUtils.atan2(mouse.y - player.hitbox.y, mouse.x - player.hitbox.x);
            float vx = MathUtils.cos(theta) * 400;
            float vy = MathUtils.sin(theta) * 400;

            playerBullets.add(new BasicProjectile(this, player.hitboxCenter.x, player.hitboxCenter.y, vx, vy));
            player.cooldown = player.PRIMARY_COOLDOWN;
        }
        player.special(mouse.x, mouse.y);
    }

    public void enemyActions() {
        //Enemy actions
        for (Enemy e: enemies) {
            enemyBullets.addAll(e.attack(frame));
            if (e.canSpawn) {
                enemies.addAll(e.spawn(frame));
            }
            e.move();
            e.collide(playerBullets);
        }
    }

    public void moveBullets() {
        for (Projectile p: playerBullets) {
            p.move(player.hitboxCenter.x, player.hitboxCenter.y, frame);
        }

        for (Projectile p: enemyBullets) {
            p.move(player.hitboxCenter.x, player.hitboxCenter.y, frame);
        }
    }

    public void movePlayer() {
        //Player movements
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.hitbox.y += player.speed * frame;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.hitbox.x -= player.speed * frame;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.hitbox.y -= player.speed * frame;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.hitbox.x += player.speed * frame;
        }

        //Keep player in bounds
        if (player.hitbox.x - 5 < 0) {
            player.hitbox.x = 5;
        } else if (player.hitbox.x + player.hitbox.height + 5 > LEVEL_WIDTH) {
            player.hitbox.x = LEVEL_WIDTH - player.hitbox.width - 5;
        }
        if (player.hitbox.y - 5 < 0) {
            player.hitbox.y = 5;
        } else if (player.hitbox.y + player.hitbox.height + 5 > LEVEL_HEIGHT) {
            player.hitbox.y = LEVEL_HEIGHT - player.hitbox.height - 5;
        }

        //Control camera
        camera.position.x = player.hitbox.x;
        camera.position.y = player.hitbox.y;
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
        player.hitbox.getCenter(player.hitboxCenter);
    }

    public void playerCollisions() {
        if (player.invincible > 0) {
            player.invincible -= frame;
        }

        for (Projectile p: enemyBullets) {
            if (player.invincible <= 0 && player.hitbox.overlaps(p.hitbox)) {
                player.invincible = player.MAX_INVINCIBLE;
            }
        }

        for (Enemy e: enemies) {
            if (player.invincible <= 0 && e.collisionBox.contains(player.hitboxCenter)) {
                player.invincible = player.MAX_INVINCIBLE;
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
            if (player.speed == 200) {
                player.speed = 150;
            } else {
                player.speed = 200;
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
