package kpi.ua.dinojump.entities;

import android.content.Context;

public class Dino extends BaseEntity{

    private final int GRAVITY = -10;
    private int maxY;
    private int minY;

    private boolean isJump;
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;

    public Dino(Context context, int maxX, int screenY, final int imageSrc, int initSpeed, int x, int y) {
        super(context, imageSrc, initSpeed, x, y);
        maxY = screenY - bitmap.getHeight() - 120;
        minY = 0;
        isJump = false;
    }

    //setting isJump true
    public void jumP() {
        isJump = true;
    }

    public void stopJump() {
        isJump = false;
    }


    public void update() {
        //if the ship is boosting
        if (isJump) {
            speed += 2;
            y = 120;
        } else {
            //slowing down if not boosting
            speed -= 2;
        }
        //controlling the top speed
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }
        //if the speed is less than min speed
        //controlling it so that it won't stop completely
        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }
        //moving the ship down
        y -= speed + GRAVITY;
        //but controlling it also so that it won't go off the screen
        if (y < minY) {
            y = minY;
        }
        if (y > maxY) {
            y = maxY;
        }
    }
}
