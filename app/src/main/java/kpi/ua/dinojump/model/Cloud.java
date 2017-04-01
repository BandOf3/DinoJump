package kpi.ua.dinojump.model;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class Cloud extends BaseEntity {

    private static final int HEIGHT = 14;
    private static final int MAX_CLOUD_GAP = 400;
    private static final int MAX_SKY_LEVEL = 30;
    private static final int MIN_CLOUD_GAP = 100;
    private static final int MIN_SKY_LEVEL = 71;
    private static final int WIDTH = 46;

    private int xPos;
    private int yPos;
    private boolean toRemove;
    private double cloudGap;

    public Cloud(Point sprite, int xPos) {
        this.spritePos = sprite;
        this.xPos = xPos;
        this.yPos = (int) getRandomNum(MAX_SKY_LEVEL, MIN_SKY_LEVEL);
        this.toRemove = false;
        this.cloudGap = getRandomNum(MIN_CLOUD_GAP, MAX_CLOUD_GAP);
    }

    public void update(double speed) {
        if (!toRemove) {
            xPos -= Math.ceil(speed);
            if (!itemIsVisible())
                toRemove = true;
        }
    }

    public int getXPosition() {
        return xPos;
    }

    public void draw(Canvas canvas) {
        Rect sRect = getScaledSource(spritePos.x, spritePos.y, WIDTH, HEIGHT);
        Rect tRect = getScaledTarget(xPos, yPos, WIDTH, HEIGHT);
        canvas.drawBitmap(getBaseBitmap(), sRect, tRect, null);
    }

    public double getCloudGap() {
        return cloudGap;
    }

    public boolean removeItemFromScreen() {
        return toRemove;
    }

    private boolean itemIsVisible() {
        return this.xPos + WIDTH > 0;
    }
}
