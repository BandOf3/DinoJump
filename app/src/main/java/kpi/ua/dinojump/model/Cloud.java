package kpi.ua.dinojump.model;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import static kpi.ua.dinojump.Runner.BaseBitmap;


public class Cloud extends BaseEntity {

    private static class config {
        public static int HEIGHT = 14;
        public static int MAX_CLOUD_GAP = 400;
        public static int MAX_SKY_LEVEL = 30;
        public static int MIN_CLOUD_GAP = 100;
        public static int MIN_SKY_LEVEL = 71;
        public static int WIDTH = 46;
    }

    private Point spritePos;
    private int containerWidth;
    public double xPos;
    public int yPos;
    public boolean remove;
    public double cloudGap;

    public Cloud(Point sprite, int w) {
        this.spritePos = sprite;
        this.containerWidth = w;
        this.xPos = containerWidth;
        this.yPos = 0;
        this.remove = false;
        this.cloudGap = getRandomNum(config.MIN_CLOUD_GAP,
                config.MAX_CLOUD_GAP);
        this.init();
    }

    private void init() {
        this.yPos = (int) getRandomNum(config.MAX_SKY_LEVEL,
                config.MIN_SKY_LEVEL);
    }

    public void update(Object... args) {
        double speed = 0;
        if (args.length > 1) {
            speed = (double) args[1];
        } else {
        }
        if (!this.remove) {
            this.xPos -= Math.ceil(speed);
            if (!this.isVisible()) {
                this.remove = true;
            }
        }
    }

    public void draw(Canvas canvas) {
        int sourceWidth = config.WIDTH;
        int sourceHeight = config.HEIGHT;
        Rect sRect = getScaledSource(spritePos.x, spritePos.y, sourceWidth, sourceHeight);
        Rect tRect = getScaledTarget((int) xPos, yPos, config.WIDTH, config.HEIGHT);
        canvas.drawBitmap(BaseBitmap, sRect, tRect, null);
    }

    private boolean isVisible() {
        return this.xPos + config.WIDTH > 0;
    }
}
