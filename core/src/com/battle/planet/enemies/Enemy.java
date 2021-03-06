package com.battle.planet.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.BattleLevel;
import com.battle.planet.Player;
import com.battle.planet.projectiles.BasicProjectile;
import com.battle.planet.projectiles.Projectile;
import com.battle.planet.projectiles.WaveProjectile;

abstract public class Enemy {

    protected Circle collisionBox; //For player-enemy collision
    protected Circle hitbox; //For bullet-enemy collision
    private float health; //Also controls when phases change
    private final float MAX_HEALTH;
    private boolean canSpawn; // Checks if spawning abilities are allowed
    private Array<Integer> phaseMarkers;

    //An array that is used to store bullets for the next attack.
    private Array<Projectile> bullets;

    private final BattleLevel level;
    private final Player player;

    /**
     * @param x  The x coordinate to start at
     * @param y  The y coordinate to start at
     * @param r0 The radius of the player-enemy hitbox
     * @param r1 The radius of the bullet-enemy hitbox
     */
    public Enemy(final BattleLevel lev, float x, float y, float r0, float r1, float hp) {
        collisionBox = new Circle(x, y, r0);
        hitbox = new Circle(x, y, r1);
        bullets = new Array<Projectile>();
        canSpawn = false;
        MAX_HEALTH = hp;
        health = hp;
        phaseMarkers = new Array<Integer>();
        level = lev;
        player = lev.player;
    }

    /**
     * Draws the planet sprite/shape render.
     *
     * @param r The shape renderer to draw with.
     */
    abstract public void drawBody(ShapeRenderer r);

    /**
     * Draws other planet-based objects (warnings, alerts, animations)
     *
     * @param r The shape renderer to draw with.
     */
    public void drawObjects(ShapeRenderer r) {

    }

    /**
     * Controls the attacks of the enemy, and returns an array of projectiles.
     *
     * @param frame The amount of time that has passed in the last frame
     * @return An array of projectiles to be used in the level
     */
    public Array<Projectile> attack(float frame) {
        return bullets;
    }

    /**
     * Spawns additional enemies, should only be used when canSpawn is true.
     *
     * @param frame The amount of time that has passed in the last frame
     * @return An array of enemies to be spawned
     */
    public Array<Enemy> spawn(float frame) {
        return new Array<Enemy>();
    }

    /**
     * Checks collisions with player bullets
     *
     * @param projectiles The array of player bullets in the level.
     * @return The array of player bullets after all collisions have been checked.
     */
    public Array<Projectile> collide(Array<Projectile> projectiles) {
        for (Projectile p : projectiles) {
            if (!p.isDestroyed && p.overlaps(hitbox)) {
                p.isDestroyed = true;
                this.health -= 1;
            }
        }
        return projectiles;
    }

    public void createSpread(float theta, int amount, float spread) {
        for (int i = 0; i < amount; ++i) {
            float angle = theta + ((float) i / amount) * spread - (spread / 2);
            bullets.add(new BasicProjectile(level, hitbox.x, hitbox.y, MathUtils.cos(angle) * 180, MathUtils.sin(angle) * 180));
        }
    }

    public void createSpread(float theta, int amount, float spread, float speed) {
        for (int i = 0; i < amount; ++i) {
            float angle = theta + ((float) i / amount) * spread - (spread / 2);
            bullets.add(new BasicProjectile(level, hitbox.x, hitbox.y, MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed));
        }
    }

    public void createWaveSpread(float theta, int amount, float spread, float mo) {
        for (int i = 0; i < amount; ++i) {
            float angle = theta + ((float) i / amount) * spread - (spread / 2);
            bullets.add(new WaveProjectile(level, hitbox.x, hitbox.y, MathUtils.cos(angle) * 180, MathUtils.sin(angle) * 180, mo));
        }
    }

    public void drawHealthBars(ShapeRenderer r) {
        r.setColor(Color.DARK_GRAY);
        r.rect(hitbox.x - hitbox.radius, hitbox.y - hitbox.radius, hitbox.radius * 2, 10);
        r.setColor(Color.GREEN);
        r.rect(hitbox.x - hitbox.radius, hitbox.y - hitbox.radius, hitbox.radius * 2 * (health / MAX_HEALTH), 10);
        r.setColor(Color.BLACK);
        //Phase transition markers
        for (int i : phaseMarkers) {
            if (health > i) {
                r.rect(hitbox.x - hitbox.radius + hitbox.radius * 2 * (i / MAX_HEALTH), hitbox.y - hitbox.radius, 2, 10);
            }
        }
    }

    /**
     * Sets collision hitbox position to bullet hitbox position
     */
    public void move() {
        collisionBox.setPosition(hitbox.x, hitbox.y);
    }

    public BattleLevel getLevel() {
        return level;
    }

    public Player getPlayer() {
        return player;
    }

    public void addProjectile(Projectile p) {
        bullets.add(p);
    }

    public void clearProjectiles() {
        bullets.clear();
    }

    public Array<Projectile> getBullets() {
        return bullets;
    }

    public float getHealth() {
        return health;
    }

    /**
     * [modHealth]
     * modifies health of entity
     * @param mod the amount of health to add/remove
     */
    public void modHealth(int mod) {
        health += mod;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void setCanSpawn(boolean b) {
        canSpawn = b;
    }

    public float getMaxHealth() {
        return MAX_HEALTH;
    }

    public void addPhaseMarkers(int ...marks) {
        for (int marker: marks) {
            phaseMarkers.add(marker);
        }
    }

    public boolean getCanSpawn() {
        return canSpawn;
    }

    public Circle getCollisionBox() {
        return collisionBox;
    }

    public Circle getHitbox() {
        return hitbox;
    }
}
