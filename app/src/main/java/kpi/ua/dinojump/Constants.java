package kpi.ua.dinojump;


import android.graphics.Point;

public class Constants {

    public static final double ACCELERATION = 0.001;
    public static final int BOTTOM_PAD = 10;
    public static final int CLEAR_TIME = 3000;
    public static final double GAP_COEFFICIENT = 0.6;
    public static final int MAX_OBSTACLE_DUPLICATION = 2;
    public static final int MAX_SPEED = 13;
    public static final double SPEED = 4;
    public static final double GRAVITY = 2.4;
    public static final int INITIAL_JUMP_VELOCITY = -26;

    public static final Point HORIZON = new Point(2, 54);
    public static final Point CLOUD = new Point(86, 2);
    public static final Point TREX = new Point(677, 2);
    public static final Point TREX_DUCKING = new Point(677, 22);
    public static final Point TEXT_SPRITE = new Point(484, 2);
    public static final Point CACTUS_LARGE = new Point(332, 2);
    public static final Point CACTUS_SMALL = new Point(228, 2);
    public static final Point PTERODACTYL = new Point(134, 2);

    public static final int HEIGHT = 150;

    public static final int FPS = 60;
    public static final int GAME_OVER_VIBRATE_LEN = 200;
}
