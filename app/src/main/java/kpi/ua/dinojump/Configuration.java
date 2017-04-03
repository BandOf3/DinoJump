package kpi.ua.dinojump;

import android.graphics.Bitmap;

public class Configuration {
    private static Configuration instance = null;

    public static Configuration get() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    private Bitmap baseBitmap;
    private double bitmapScale = 1.;
    private double targetScale = 1.;
    private int startX = 0;
    private int startY = 0;

    private Configuration() {
    }

    public Bitmap getBaseBitmap() {
        return baseBitmap;
    }

    public void setBaseBitmap(Bitmap baseBitmap) {
        this.baseBitmap = baseBitmap;
    }

    public double getBitmapScale() {
        return bitmapScale;
    }

    public void setBitmapScale(double bitmapScale) {
        this.bitmapScale = bitmapScale;
    }

    public double getTargetScale() {
        return targetScale;
    }

    public void setTargetScale(double targetScale) {
        this.targetScale = targetScale;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }
}
