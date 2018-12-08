package com.battle.planet;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {

    Rectangle hitbox;
    Vector2 hitboxCenter;
    final float PRIMARY_COOLDOWN = 0.08f;
    float cooldown;
    float speed = 200;
    float MAX_INVINCIBLE = 1f;
    float invincible = 0;

    public Player(float x, float y){
        hitbox = new Rectangle(x, y, 10, 10);
        hitboxCenter = new Vector2(0, 0);
    }
}
