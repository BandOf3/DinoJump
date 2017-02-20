package kpi.ua.dinojump.entities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

abstract class BaseEntity {
    protected Bitmap bitmap;
    protected int x;
    protected int y;
    protected int speed;

    public BaseEntity(Context context, final int imageSrc, int initSpeed, int x, int y) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), imageSrc);
        this.speed = initSpeed;
        this.x = x;
        this.y = y;
    }

    public Bitmap getBitmap() {return bitmap;}

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }
}
