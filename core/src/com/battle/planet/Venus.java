package com.battle.planet;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class Venus extends Enemy {
    /**
     * @param x  The x coordinate to start at
     * @param y  The y coordinate to start at
     */
    public Venus(float x, float y) {
        super(x, y, 70, 80, 500);
    }

    @Override
    public void drawBody(ShapeRenderer r) {

    }

    @Override
    public Array<Projectile> attack(float x, float y, float frame) {
        bullets.clear();

        return bullets;
    }

}
