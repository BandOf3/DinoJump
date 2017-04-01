package kpi.ua.dinojump.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public abstract class BaseEntity {
    public static final int BASE_WIDTH = 1204;
    public static double Scale = 1.;
    public static double ScaleTarget = 1;
    public static int startX = 0;
    public static int startY = 0;

    protected Point spritePos;

    protected final Bitmap baseBitmap;

    public Rect getScaledSource(int l, int t, int w, int h) {
        int rl = (int) (l * Scale);
        int rt = (int) (t * Scale);
        int rr = (int) (rl + w * Scale);
        int rb = (int) (rt + h * Scale);
        return new Rect(rl, rt, rr, rb);
    }

    public Rect getScaledTarget(int l, int t, int w, int h) {
        int sx = startX;
        int sy = startY;
        int rl = (int) (l * ScaleTarget + sx);
        int rt = (int) (t * ScaleTarget + sy);
        int rr = (int) (rl + w * ScaleTarget);
        int rb = (int) (rt + h * ScaleTarget);
        return new Rect(rl, rt, rr, rb);
    }

    public double getRandomNum(int min, int max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    public abstract void draw(Canvas canvas);

    public BaseEntity(Bitmap baseBitmap) {
        this.baseBitmap = baseBitmap;
    }
}
