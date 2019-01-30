package com.battle.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class WaveProjectile extends Projectile {

    float maxOffset;
    float offset;
    float direction;
    float offsetVel;
    float offsetAccel;
    float period = 0.2f;
    Vector2 normalPosition;

    Array<Vector2> path = new Array<Vector2>();

    public WaveProjectile(final BattleLevel lev, float x, float y, float vx, float vy, float mo) {
        this(lev, new Rectangle(x, y, 5, 5), new Vector2(vx, vy), mo);
    }

    public WaveProjectile(final BattleLevel lev, Rectangle r, Vector2 v, float mo) {
        super(lev, r, v);
        direction = MathUtils.atan2(v.y, v.x);
        //Kinematics
        maxOffset = mo;
        offset = 0;
        offsetVel = (2 * maxOffset) / period;
        offsetAccel = -offsetVel / period;
        //System.out.printf("%f, %f, %f\n", offset, offsetVel, offsetAccel);
        normalPosition = new Vector2(hitbox.x, hitbox.y);
    }

    //For special wave effects (since direction usually won't matter
    public WaveProjectile(final BattleLevel lev, Rectangle r, Vector2 v, float mo, boolean startPositive) {
        super(lev, r, v);
        direction = MathUtils.atan2(v.y, v.x);
        //Kinematics
        maxOffset = mo;
        offset = 0;
        if (startPositive) {
            offsetVel = (2 * maxOffset) / period;
            offsetAccel = -offsetVel / period;
        } else {
            offsetVel = -(2 * maxOffset) / period;
            offsetAccel = offsetVel / period;
        }
        //System.out.printf("%f, %f, %f\n", offset, offsetVel, offsetAccel);
        normalPosition = new Vector2(hitbox.x, hitbox.y);
    }

    //And more
    public WaveProjectile(final BattleLevel lev, Rectangle r, Vector2 v, float t, float mo, boolean startPositive) {
        super(lev, r, v);
        period = t;
        direction = MathUtils.atan2(v.y, v.x);
        //Kinematics
        maxOffset = mo;
        offset = 0;
        if (startPositive) {
            offsetVel = (2 * maxOffset) / period;
            offsetAccel = -offsetVel / period;
        } else {
            offsetVel = -(2 * maxOffset) / period;
            offsetAccel = offsetVel / period;
        }
        //System.out.printf("%f, %f, %f\n", offset, offsetVel, offsetAccel);
        normalPosition = new Vector2(hitbox.x, hitbox.y);
    }
    public void move(float frame) {
        //Undo previous wave shift
        hitbox.x = normalPosition.x;
        hitbox.y = normalPosition.y;

        //Increment offset
        offset += offsetVel * frame;
        //Acceleration
        offsetVel += offsetAccel * frame;

        //Keep in maxOffset bounds
        if (offset > maxOffset) {
            offset = maxOffset;
            offsetVel = 0;
            //System.out.println("TURN DOWN");
        } else if (offset < -maxOffset) {
            offset = -maxOffset;
            offsetVel = 0;
            //System.out.println("TURN UP");
        }

        if (offset > 0 && offset - offsetVel * frame < 0) {
            offset = 0;
            offsetVel = (2 * maxOffset) / period;
            offsetAccel *= -1;
            //System.out.println("SWITCH A - -> +");
        }
        if (offset < 0 && offset - offsetVel * frame > 0) {
            offset = 0;
            offsetVel = -(2 * maxOffset) / period;
            offsetAccel *= -1;
            //System.out.println("SWITCH A + -> -");
        }


        //System.out.printf("%f, %f, %f\n", offset, offsetVel, offsetAccel);

        //Set hitbox
        hitbox.x += velocity.x * frame;
        hitbox.y += velocity.y * frame;
        normalPosition.x = hitbox.x;
        normalPosition.y = hitbox.y;

        hitbox.x -= offset * MathUtils.sin(direction);
        hitbox.y += offset * MathUtils.cos(direction);

        path.add(new Vector2(hitbox.x, hitbox.y));
    }

    public void drawSpecial(ShapeRenderer r) {
        r.setColor(Color.ORANGE);
        for (Vector2 p: path) {
            r.ellipse(p.x, p.y, 1, 1);
        }
    }
}
