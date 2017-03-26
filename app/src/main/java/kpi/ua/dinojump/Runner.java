package kpi.ua.dinojump;

import android.graphics.Bitmap;
import android.graphics.Point;

public class Runner {

    public static Bitmap BaseBitmap;
    public static double ACCELERATION = 0.001;
    public static int BOTTOM_PAD = 10;
    public static int CLEAR_TIME = 3000;
    public static double GAP_COEFFICIENT = 0.6;
    public static int MAX_OBSTACLE_DUPLICATION = 2;
    public static int MAX_SPEED = 13;
    public static double SPEED = 6;

    public final static Point HORIZON = new Point(2, 54);
    public final static Point CLOUD = new Point(86, 2);
    public final static Point TREX = new Point(677, 2);
    public final static Point TEXT_SPRITE = new Point(484, 2);
    public final static Point CACTUS_LARGE = new Point(332, 2);
    public final static Point CACTUS_SMALL = new Point(228, 2);
    public final static Point PTERODACTYL = new Point(134, 2);

    public static int HEIGHT = 150;
}
