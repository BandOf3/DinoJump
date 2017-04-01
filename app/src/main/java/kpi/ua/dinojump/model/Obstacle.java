package kpi.ua.dinojump.model;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import kpi.ua.dinojump.Constants;

import static kpi.ua.dinojump.Constants.FPS;

public class Obstacle extends BaseEntity {

    private final static double MAX_GAP_COEFFICIENT = 1.5;
    private final static int MAX_OBSTACLE_LENGTH = 3;

    private Point dimensions;
    private int xPos, yPos, width, size, gap, currentFrame;
    private types.ObstacleTypes typeConfig;
    private boolean toRemove, followingObstacleCreated;
    private double gapCoefficient, speedOffset;
    private long timer;
    private Rect collisionDetector;

    public Obstacle(types.ObstacleTypes type, Point dimensions, double gapCoefficient, double speed) {
        spritePos = type.spritePos;
        typeConfig = type;
        this.gapCoefficient = gapCoefficient;
        size = (int) getRandomNum(1, Obstacle.MAX_OBSTACLE_LENGTH);
        this.dimensions = dimensions;
        toRemove = false;
        xPos = 0;
        yPos = 0;
        width = 0;
        gap = 0;
        speedOffset = 0;
        // For animated obstacles.
        currentFrame = 0;
        timer = 0;
        init(speed);
        collisionDetector = new Rect(xPos, yPos, typeConfig.width, typeConfig.height);
    }

    private void init(double speed) {
        // Only allow sizing if we're at the right speed.
        if (size > 1 && typeConfig.multipleSpeed > speed) {
            size = 1;
        }
        width = typeConfig.width * size;
        xPos = dimensions.x - width;
        // Check if obstacle can be positioned at various heights.
        if (typeConfig.yPos.length > 1) {
            yPos = typeConfig.yPos[((int) getRandomNum(0, typeConfig.yPos.length - 1))];
        } else {
            yPos = typeConfig.yPos[0];
        }
        // For obstacles that go at a different speed from the horizon.
        if (typeConfig.speedOffset > 0) {
            speedOffset = Math.random() > 0.5 ? typeConfig.speedOffset :
                    -typeConfig.speedOffset;
        }
        gap = (int) getGap(gapCoefficient, speed);
    }

    private double getGap(double gapCoefficient, double speed) {
        int minGap = (int) Math.round(width * speed + typeConfig.minGap * gapCoefficient);
        int maxGap = (int) Math.round(minGap * Obstacle.MAX_GAP_COEFFICIENT);
        return getRandomNum(minGap, maxGap);
    }

    public boolean isVisible() {
        return xPos + width > 0;
    }

    public void update(long deltaTime, double speed) {
        if (!toRemove) {
            if (typeConfig.speedOffset > 0) {
                speed += speedOffset;
            }
            xPos -= Math.floor((speed * FPS / 1000) * deltaTime);
            // Update frame
            if (typeConfig.numFrames > 0) {
                timer += deltaTime;
                if (timer >= typeConfig.frameRate) {
                    currentFrame =
                            currentFrame == typeConfig.numFrames - 1 ?
                                    0 : currentFrame + 1;
                    timer = 0;
                }
            }
            if (!isVisible()) {
                toRemove = true;
            }
        }
        updateCollisionDetector();
    }

    private void updateCollisionDetector() {
        collisionDetector.left = xPos;
        collisionDetector.top = yPos;
        collisionDetector.right = xPos + typeConfig.width;
        collisionDetector.bottom = yPos + typeConfig.height;
    }

    public void draw(Canvas canvas) {
        int sourceWidth = typeConfig.width;
        int sourceHeight = typeConfig.height;
        int sourceX = (int) ((sourceWidth * size) * (0.5 * (size - 1)) +
                spritePos.x);
        if (currentFrame > 0) {
            sourceX += sourceWidth * currentFrame;
        }
        Rect sRect = getScaledSource(sourceX, spritePos.y, sourceWidth * size, sourceHeight);
        Rect tRect = getScaledTarget(xPos, yPos, typeConfig.width * size, typeConfig.height);
        canvas.drawBitmap(getBaseBitmap(), sRect, tRect, null);
    }

    public Rect getCollisionDetector() {
        return collisionDetector;
    }

    public int getWidth() {
        return width;
    }

    public int getGap() {
        return gap;
    }

    public boolean followingObstacleWasCreated() {
        return followingObstacleCreated;
    }

    public void setFollowingObstacleCreated(boolean followingObstacleCreated) {
        this.followingObstacleCreated = followingObstacleCreated;
    }

    public boolean isRemove() {
        return toRemove;
    }

    public int getXPosition() {
        return xPos;
    }

    // defines obstacles variances
    public static class types {

        public static class ObstacleTypes {
            public Point spritePos;
            public int type;
            public int width;
            public int height;
            public int[] yPos;
            public int multipleSpeed;
            public double minSpeed;
            public int minGap;
            public int numFrames;
            public double frameRate;
            public double speedOffset;
        }

        public final static int length = 3;

        public static ObstacleTypes getObstacleTypes(int i) {
            ObstacleTypes ret;
            switch (i) {
                case 1:
                    ret = new CACTUS_LARGE();
                    ret.type = 1;
                    ret.spritePos = Constants.CACTUS_LARGE;
                    break;
                case 2:
                    ret = new PTERODACTYL();
                    ret.type = 2;
                    ret.spritePos = Constants.PTERODACTYL;
                    break;
                case 0:
                default:
                    ret = new CACTUS_SMALL();
                    ret.type = 0;
                    ret.spritePos = Constants.CACTUS_SMALL;
                    break;
            }
            return ret;
        }

        public static class CACTUS_SMALL extends ObstacleTypes {
            public CACTUS_SMALL() {
                width = 17;
                height = 35;
                yPos = new int[]{105};
                multipleSpeed = 4;
                minGap = 120;
                minSpeed = 0;
            }
        }

        public static class CACTUS_LARGE extends ObstacleTypes {
            public CACTUS_LARGE() {
                width = 25;
                height = 50;
                yPos = new int[]{90};
                multipleSpeed = 7;
                minGap = 120;
                minSpeed = 0;
            }
        }

        public static class PTERODACTYL extends ObstacleTypes {
            public PTERODACTYL() {
                width = 46;
                height = 40;
                yPos = new int[]{100, 75, 50};
                multipleSpeed = 999;
                minSpeed = 8.5;
                minGap = 150;
                numFrames = 2;
                frameRate = 1000 / 6;
                speedOffset = .8;
            }
        }
    }
}
