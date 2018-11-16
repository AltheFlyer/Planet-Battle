package com.battle.planet;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class PlanetBattle extends ApplicationAdapter {

	SpriteBatch batch;
	ShapeRenderer render;
	OrthographicCamera camera;

	Rectangle playerHitbox;
	Array<Projectile> playerBullets;
	Array<Projectile> enemyBullets;
	Vector2 hitboxCenter;
	final float MAX_COOLDOWN = 0.2f;
	float cooldown;
	Mars mars;
	float LEVEL_WIDTH = 600;
	float LEVEL_HEIGHT = 600;
	float SCREEN_WIDTH = 600;
	float SCREEN_HEIGHT = 600;
	
	@Override
	public void create () {
		//initialize renders
		batch = new SpriteBatch();
		render = new ShapeRenderer();
		render.setAutoShapeType(true);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

		//Initialize player
		playerHitbox = new Rectangle(0, 0, 20, 20);
		playerBullets = new Array<Projectile>();
		hitboxCenter = new Vector2(0, 0);

		//Initialize enemies
		enemyBullets = new Array<Projectile>();
		//enemyBullets.add(new WaveProjectile(0, 200, 60, 0, 10));
		mars = new Mars(200, 200);
	}

	@Override
	public void render () {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		render.setProjectionMatrix(camera.combined);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float frame = Gdx.graphics.getDeltaTime();
		if (frame > 0.2f) {
			frame = 0.2f;
		}
		Vector3 mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		playerHitbox.getCenter(hitboxCenter);

		batch.begin();
		batch.end();

		render.begin();
		render.set(ShapeRenderer.ShapeType.Filled);
		render.setColor(Color.RED);

		mars.drawBody(render);
		mars.drawObjects(render);
		mars.collide(playerBullets);
		//Check collisions (FOR NOW)
		render.setColor(Color.BLUE);
		for (Projectile p: enemyBullets) {
			if (playerHitbox.overlaps(p.hitbox)) {
				render.setColor(Color.RED);
			}
		}
		drawRectangle(playerHitbox);

		//Draw player bullets, then enemy bullets
		render.setColor(Color.YELLOW);
		for (Projectile p: playerBullets) {
			p.move(frame);
			drawRectangle(p.hitbox);
			p.drawSpecial(render);
		}
		render.setColor(Color.RED);
		for (Projectile p: enemyBullets) {
			p.move(frame);
			drawRectangle(p.hitbox);
			p.drawSpecial(render);
		}


		render.end();

		//Player movements
		if (Gdx.input.isKeyPressed(Keys.W)) {
			playerHitbox.y += 200 * frame;
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			playerHitbox.x -= 200 * frame;
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			playerHitbox.y -= 200 * frame;
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			playerHitbox.x += 200 * frame;
		}

		//Keep player in bounds
		if (playerHitbox.x < 0) {
			playerHitbox.x = 0;
		} else if (playerHitbox.x + playerHitbox.height > LEVEL_WIDTH) {
			playerHitbox.x = LEVEL_WIDTH - playerHitbox.height;
		}
		if (playerHitbox.y < 0) {
			playerHitbox.y = 0;
		} else if (playerHitbox.y + playerHitbox.height > LEVEL_HEIGHT) {
			playerHitbox.y = LEVEL_HEIGHT - playerHitbox.height;
		}

		//Player shooting
		if (Gdx.input.isTouched()) {
			float theta = MathUtils.atan2(mouse.y - playerHitbox.y, mouse.x - playerHitbox.x);
			float vx = MathUtils.cos(theta) * 400;
			float vy = MathUtils.sin(theta) * 400;

			playerBullets.add(new BasicProjectile(hitboxCenter.x, hitboxCenter.y, vx, vy));
		}

		//Enemy actions
		enemyBullets.addAll(mars.attack(hitboxCenter.x, hitboxCenter.y, frame));

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
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public void drawRectangle(Rectangle r) {
		render.box(r.x, r.y, 0, r.width, r.height, 0);
	}
}

