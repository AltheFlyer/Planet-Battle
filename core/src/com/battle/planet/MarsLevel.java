package com.battle.planet;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

public class MarsLevel extends BattleLevel {

	public MarsLevel(final PlanetBattle g) {
		super(g);
		enemies.add(new Mars(300, 300));
	}

	@Override
	public void render (float delta) {
		super.render(delta);

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

		//Draw all enemies
		for (Enemy e: enemies) {
			e.drawBody(render);
			e.drawObjects(hitboxCenter.x, hitboxCenter.y, render);
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
		render.setColor(Color.RED);
		for (Projectile p: enemyBullets) {
			p.move(hitboxCenter.x, hitboxCenter.y, frame);
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

		//Enemy actions
		for (Enemy e: enemies) {
			enemyBullets.addAll(e.attack(hitboxCenter.x, hitboxCenter.y, frame));
			if (e.canSpawn) {
				enemies.addAll(e.spawn(hitboxCenter.x, hitboxCenter.y, frame));
			}
			if (e instanceof Mars && ((Mars) e).phase == 2 && enemies.size == 1) {
				((Mars) e).phase = -2;
			} else {
				if (e instanceof Mars && ((Mars) e).phase == 2) {

				} else {
					e.collide(playerBullets);
				}
			}
		}

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

		//Remove dead enemies
		Iterator<Enemy> iterEnemy = enemies.iterator();
		while (iterEnemy.hasNext()) {
			Enemy e = iterEnemy.next();
			if (e.health <= 0) {
				iterEnemy.remove();
			}
		}

		if (enemies.size == 0) {
			game.setScreen(new LevelSelectScreen(game));
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
	public void dispose () {
		batch.dispose();
	}

}

