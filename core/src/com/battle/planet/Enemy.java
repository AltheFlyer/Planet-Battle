package com.battle.planet;

import com.badlogic.gdx.math.Circle;

public class Enemy {

    Circle collisionBox; //For player-enemy collision
    Circle hitbox; //For player-bullet collision
    float health; //Also controls when phases change

}
