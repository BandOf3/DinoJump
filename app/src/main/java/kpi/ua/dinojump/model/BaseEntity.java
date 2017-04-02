package kpi.ua.dinojump.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import kpi.ua.dinojump.Configuration;

public abstract class BaseEntity {
    public static final int BASE_WIDTH = 1204;

    protected Point spritePos;

    public abstract void draw(Canvas canvas);

    protected Rect getScaledSource(int l, int t, int w, int h) {
        Configuration conf = Configuration.get();
        double scale = conf.getBitmapScale();
        int rl = (int) (l * scale);
        int rt = (int) (t * scale);
        int rr = (int) (rl + w * scale);
        int rb = (int) (rt + h * scale);
        return new Rect(rl, rt, rr, rb);
    }

    protected Rect getScaledTarget(int l, int t, int w, int h) {
        Configuration conf = Configuration.get();
        int sx = conf.getStartX();
        int sy = conf.getStartY();
        double scale = conf.getTargetScale();
        int rl = (int) (l * scale + sx);
        int rt = (int) (t * scale + sy);
        int rr = (int) (rl + w * scale);
        int rb = (int) (rt + h * scale);
        return new Rect(rl, rt, rr, rb);
    }

    protected Bitmap getBaseBitmap() {
        return Configuration.get().getBaseBitmap();
    }

    protected double getRandomNum(int min, int max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }
}
