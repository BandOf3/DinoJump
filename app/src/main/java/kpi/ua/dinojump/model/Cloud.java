package kpi.ua.dinojump.model;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import static kpi.ua.dinojump.Runner.BaseBitmap;
import static kpi.ua.dinojump.model.BaseEntity.*;

public class Cloud extends BaseEntity {

    private final static int HEIGHT = 14;
    private final static int MAX_CLOUD_GAP = 400;
    private final static int MAX_SKY_LEVEL = 30;
    private final static int MIN_CLOUD_GAP = 100;
    private final static int MIN_SKY_LEVEL = 71;
    private final static int WIDTH = 46;

    private Point spritePos;
    private int containerWidth;
    private double xPos;
    private int yPos;
    private boolean remove;
    private double cloudGap;

    public Cloud(Point sprite, int w) {
        this.spritePos = sprite;
        this.containerWidth = w;
        this.xPos = containerWidth;
        this.yPos = 0;
        this.remove = false;
        this.cloudGap = getRandomNum(MIN_CLOUD_GAP,
                MAX_CLOUD_GAP);
        this.init();
    }

    private void init() {
        this.yPos = (int) getRandomNum(MAX_SKY_LEVEL,
                MIN_SKY_LEVEL);
    }

    public void update(long deltaTime, double speed) {
        if (!this.remove) {
            this.xPos -= Math.ceil(speed);
            if (!this.isVisible()) {
                this.remove = true;
            }
        }
    }

    public double getxPos() {
        return xPos;
    }

    public void draw(Canvas canvas) {
        Rect sRect = getScaledSource(spritePos.x, spritePos.y, WIDTH, HEIGHT);
        Rect tRect = getScaledTarget((int) xPos, yPos, WIDTH, HEIGHT);
        canvas.drawBitmap(BaseBitmap, sRect, tRect, null);
    }


    public double getCloudGap() {
        return cloudGap;
    }

    public boolean isRemove() {
        return remove;
    }

    private boolean isVisible() {
        return this.xPos + WIDTH > 0;
    }
}
