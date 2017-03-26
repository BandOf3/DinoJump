package kpi.ua.dinojump;

import android.graphics.Bitmap;
import android.graphics.Point;

public class Runner {

    private static int DEFAULT_WIDTH = 600;
    public static Bitmap BaseBitmap;

    public static double ACCELERATION = 0.001;
    public static double BG_CLOUD_SPEED = 0.2;
    public static int BOTTOM_PAD = 10;
    public static int CLEAR_TIME = 3000;
    public static double CLOUD_FREQUENCY = 0.5;
    public static int GAMEOVER_CLEAR_TIME = 750;
    public static double GAP_COEFFICIENT = 0.6;
    public static double GRAVITY = 0.6;
    public static int INITIAL_JUMP_VELOCITY = 12;
    public static int MAX_CLOUDS = 6;
    public static int MAX_OBSTACLE_LENGTH = 3;
    public static int MAX_OBSTACLE_DUPLICATION = 2;
    public static int MAX_SPEED = 13;
    public static int MIN_JUMP_HEIGHT = 35;
    public static double MOBILE_SPEED_COEFFICIENT = 1.2;
    public static String RESOURCE_TEMPLATE_ID = "audio-resources";
    public static double SPEED = 6;
    public static int SPEED_DROP_COEFFICIENT = 3;

    public final static Point HORIZON = new Point(2, 54);
    public final static Point CLOUD = new Point(86, 2);
    public final static Point TREX = new Point(677, 2);
    public final static Point TEXT_SPRITE = new Point(484, 2);
    public final static Point CACTUS_LARGE = new Point(332, 2);
    public final static Point CACTUS_SMALL = new Point(228, 2);
    public final static Point PTERODACTYL = new Point(134, 2);

    public static int HEIGHT = 150;
    public static int WIDTH = DEFAULT_WIDTH;
}
