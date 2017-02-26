package kpi.ua.dinojump.entities;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import kpi.ua.dinojump.Runner;
import kpi.ua.dinojump.view.GameView;

import static kpi.ua.dinojump.Runner.BaseBitmap;

public class Obstacle extends BaseEntity {

    public final static double MAX_GAP_COEFFICIENT = 1.5;
    public final static int MAX_OBSTACLE_LENGTH = 3;
    private Point spritePos;
    private Point dimensions;
    private double gapCoefficient;
    private types.ObstacleTypes typeConfig;
    private int size;
    public boolean remove;
    public int xPos, yPos, width;
    private CollisionBox[] collisionBoxes;
    public int gap;
    private double speedOffset;
    private int currentFrame;
    private long timer;
    public boolean followingObstacleCreated;


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
            public CollisionBox[] collisionBoxes;
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
                    ret.spritePos = Runner.spritePos.CACTUS_LARGE;
                    break;
                case 2:
                    ret = new PTERODACTYL();
                    ret.type = 2;
                    ret.spritePos = Runner.spritePos.PTERODACTYL;
                    break;
                case 0:
                default:
                    ret = new CACTUS_SMALL();
                    ret.type = 0;
                    ret.spritePos = Runner.spritePos.CACTUS_SMALL;
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
                collisionBoxes = new CollisionBox[]{
                        new CollisionBox(0, 7, 5, 27),
                        new CollisionBox(4, 0, 6, 34),
                        new CollisionBox(10, 4, 7, 14)
                };
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
                collisionBoxes = new CollisionBox[]{
                        new CollisionBox(0, 12, 7, 38),
                        new CollisionBox(8, 0, 7, 49),
                        new CollisionBox(13, 10, 10, 38)
                };
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
                collisionBoxes = new CollisionBox[]{
                        new CollisionBox(15, 15, 16, 5),
                        new CollisionBox(18, 21, 24, 6),
                        new CollisionBox(2, 14, 4, 3),
                        new CollisionBox(6, 10, 4, 7),
                        new CollisionBox(10, 8, 6, 9)
                };
                numFrames = 2;
                frameRate = 1000 / 6;
                speedOffset = .8;
            }
        }
    }

    public Obstacle(types.ObstacleTypes type, Point dimensions, double gapCoefficient, double speed) {
        this.spritePos = type.spritePos;
        this.typeConfig = type;
        this.gapCoefficient = gapCoefficient;
        this.size = (int) getRandomNum(1, Obstacle.MAX_OBSTACLE_LENGTH);
        this.dimensions = dimensions;
        this.remove = false;
        this.xPos = 0;
        this.yPos = 0;
        this.width = 0;
        this.gap = 0;
        this.speedOffset = 0;
        // For animated obstacles.
        this.currentFrame = 0;
        this.timer = 0;
        this.init(speed);
    }

    public void init(double speed) {
        this.cloneCollisionBoxes();
        // Only allow sizing if we're at the right speed.
        if (this.size > 1 && this.typeConfig.multipleSpeed > speed) {
            this.size = 1;
        }
        this.width = this.typeConfig.width * this.size;
        this.xPos = this.dimensions.x - this.width;
        // Check if obstacle can be positioned at various heights.
        if (this.typeConfig.yPos.length > 1)  {
            this.yPos = this.typeConfig.yPos[((int) getRandomNum(0, this.typeConfig.yPos.length - 1))];
        } else {
            this.yPos = this.typeConfig.yPos[0];
        }
        // Make collision box adjustments,
        // Central box is adjusted to the size as one box.
        //      ____        ______        ________
        //    _|   |-|    _|     |-|    _|       |-|
        //   | |<->| |   | |<--->| |   | |<----->| |
        //   | | 1 | |   | |  2  | |   | |   3   | |
        //   |_|___|_|   |_|_____|_|   |_|_______|_|
        //
        if (this.size > 1) {
            this.collisionBoxes[1].width = this.width - this.collisionBoxes[0].width -
                    this.collisionBoxes[2].width;
            this.collisionBoxes[2].x = this.width - this.collisionBoxes[2].width;
        }
        // For obstacles that go at a different speed from the horizon.
        if (this.typeConfig.speedOffset > 0) {
            this.speedOffset = Math.random() > 0.5 ? this.typeConfig.speedOffset :
                    -this.typeConfig.speedOffset;
        }
        this.gap = (int) this.getGap(this.gapCoefficient, speed);
    }

    public void cloneCollisionBoxes() {
        CollisionBox[] collisionBoxes = this.typeConfig.collisionBoxes;
        this.collisionBoxes = new CollisionBox[collisionBoxes.length];
        for (int i = collisionBoxes.length - 1; i >= 0; i--) {
            this.collisionBoxes[i] = new CollisionBox(collisionBoxes[i].x,
                    collisionBoxes[i].y, collisionBoxes[i].width,
                    collisionBoxes[i].height);
        }
    }

    public double getGap(double gapCoefficient, double speed) {
        int minGap = (int) Math.round(this.width * speed + this.typeConfig.minGap * gapCoefficient);
        int maxGap = (int) Math.round(minGap * Obstacle.MAX_GAP_COEFFICIENT);
        return getRandomNum(minGap, maxGap);
    }

    public boolean isVisible() {
        return this.xPos + this.width > 0;
    }

    @Override
    public void update(Object... args) {
        super.update(args);
        long deltaTime = 0;
        double speed = 0;
        if (args.length > 0) {
            deltaTime = (long) args[0];
            if (args.length > 1) {
                speed = (double) args[1];
            }
        }
        if (!this.remove) {
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
                this.remove = true;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int sourceWidth = this.typeConfig.width;
        int sourceHeight = this.typeConfig.height;
        int sourceX = (int) ((sourceWidth * this.size) * (0.5 * (this.size - 1)) +
                        this.spritePos.x);
        // Animation frames.
        if (this.currentFrame > 0) {
            sourceX += sourceWidth * this.currentFrame;
        }
        Rect sRect = getScaledSource(sourceX, this.spritePos.y, sourceWidth * this.size, sourceHeight);
        Rect tRect = getScaledTarget(this.xPos, this.yPos, this.typeConfig.width * this.size, this.typeConfig.height);
        canvas.drawBitmap(BaseBitmap, sRect, tRect, null);
    }
}
