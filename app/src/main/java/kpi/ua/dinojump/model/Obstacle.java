package kpi.ua.dinojump.model;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import kpi.ua.dinojump.Runner;
import kpi.ua.dinojump.view.GameView;

import static kpi.ua.dinojump.Runner.BaseBitmap;

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
        this.spritePos = type.spritePos;
        this.typeConfig = type;
        this.gapCoefficient = gapCoefficient;
        this.size = (int) getRandomNum(1, Obstacle.MAX_OBSTACLE_LENGTH);
        this.dimensions = dimensions;
        this.toRemove = false;
        this.xPos = 0;
        this.yPos = 0;
        this.width = 0;
        this.gap = 0;
        this.speedOffset = 0;
        // For animated obstacles.
        this.currentFrame = 0;
        this.timer = 0;
        this.init(speed);
        collisionDetector = new Rect(xPos, yPos, typeConfig.width, typeConfig.height);
    }

    private void init(double speed) {
        // Only allow sizing if we're at the right speed.
        if (this.size > 1 && this.typeConfig.multipleSpeed > speed) {
            this.size = 1;
        }
        this.width = this.typeConfig.width * this.size;
        this.xPos = this.dimensions.x - this.width;
        // Check if obstacle can be positioned at various heights.
        if (this.typeConfig.yPos.length > 1) {
            this.yPos = this.typeConfig.yPos[((int) getRandomNum(0, this.typeConfig.yPos.length - 1))];
        } else {
            this.yPos = this.typeConfig.yPos[0];
        }
        // For obstacles that go at a different speed from the horizon.
        if (this.typeConfig.speedOffset > 0) {
            this.speedOffset = Math.random() > 0.5 ? this.typeConfig.speedOffset :
                    -this.typeConfig.speedOffset;
        }
        this.gap = (int) this.getGap(this.gapCoefficient, speed);
    }

    private double getGap(double gapCoefficient, double speed) {
        int minGap = (int) Math.round(this.width * speed + this.typeConfig.minGap * gapCoefficient);
        int maxGap = (int) Math.round(minGap * Obstacle.MAX_GAP_COEFFICIENT);
        return getRandomNum(minGap, maxGap);
    }

    public boolean isVisible() {
        return this.xPos + this.width > 0;
    }

    public void update(long deltaTime, double speed) {
        if (!this.toRemove) {
            if (this.typeConfig.speedOffset > 0) {
                speed += this.speedOffset;
            }
            this.xPos -= Math.floor((speed * GameView.FPS / 1000) * deltaTime);
            // Update frame
            if (this.typeConfig.numFrames > 0) {
                this.timer += deltaTime;
                if (this.timer >= this.typeConfig.frameRate) {
                    this.currentFrame =
                            this.currentFrame == this.typeConfig.numFrames - 1 ?
                                    0 : this.currentFrame + 1;
                    this.timer = 0;
                }
            }
            if (!this.isVisible()) {
                this.toRemove = true;
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
        int sourceWidth = this.typeConfig.width;
        int sourceHeight = this.typeConfig.height;
        int sourceX = (int) ((sourceWidth * this.size) * (0.5 * (this.size - 1)) +
                this.spritePos.x);
        if (this.currentFrame > 0) {
            sourceX += sourceWidth * this.currentFrame;
        }
        Rect sRect = getScaledSource(sourceX, this.spritePos.y, sourceWidth * this.size, sourceHeight);
        Rect tRect = getScaledTarget(this.xPos, this.yPos, this.typeConfig.width * this.size, this.typeConfig.height);
        canvas.drawBitmap(BaseBitmap, sRect, tRect, null);
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
                    ret.spritePos = Runner.CACTUS_LARGE;
                    break;
                case 2:
                    ret = new PTERODACTYL();
                    ret.type = 2;
                    ret.spritePos = Runner.PTERODACTYL;
                    break;
                case 0:
                default:
                    ret = new CACTUS_SMALL();
                    ret.type = 0;
                    ret.spritePos = Runner.CACTUS_SMALL;
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
