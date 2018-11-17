package com.battle.planet;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.Array;

abstract public class Enemy {

    Circle collisionBox; //For player-enemy collision
    Circle hitbox; //For bullet-enemy collision
    float health; //Also controls when phases change
    boolean canSpawn; // Checks if spawning abilities are allowed

    //An array that is used to store bullets for the next attack.
    Array<Projectile> bullets;

    /**
     *
     * @param x The x coordinate to start at
     * @param y The y coordinate to start at
     * @param r0 The radius of the player-enemy hitbox
     * @param r1 The radius of the bullet-enemy hitbox
     */
    public Enemy(float x, float y, float r0, float r1){
        collisionBox = new Circle(x, y, r0);
        hitbox = new Circle(x, y, r1);
        bullets = new Array<Projectile>();
        canSpawn = false;
    }

    /**
     * Draws the planet sprite/shape render.
     * @param r The shape renderer to draw with.
     */
    abstract public void drawBody(ShapeRenderer r);

    /**
     * Draws other planet-based objects (warnings, alerts, animations)
     * @param r The shape renderer to draw with.
     */
    public void drawObjects(float x, float y, ShapeRenderer r) {

    }

    /**
     * Controls the attacks of the enemy, and returns an array of projectiles.
     * @param x The x coordinate of the player
     * @param y The y coordinate of the player
     * @param frame The amount of time that has passed in the last frame
     * @return An array of projectiles to be used in the level
     */
    public Array<Projectile> attack(float x, float y, float frame) {
        return bullets;
    }

    /**
     * Spawns additional enemies, should only be used when canSpawn is true.
     * @param x The x coordinate of the player
     * @param y The y coordinate of the player
     * @param frame The amount of time that has passed in the last frame
     * @return An array of enemies to be spawned
     */
    public Array<Enemy> spawn(float x, float y, float frame) {
        return new Array<Enemy>();
    }

    /**
     * Checks collisions with player bullets
     * @param bullets The array of player bullets in the level.
     * @return The array of player bullets after all collisions have been checked.
     */
    public Array<Projectile> collide(Array<Projectile> bullets) {
        for (Projectile p: bullets) {
            if (!p.isDestroyed && this.hitbox.contains(p.hitbox.x, p.hitbox.y)) {
                p.isDestroyed = true;
                this.health -= 1;
            }
        }
        return bullets;
    }
}
