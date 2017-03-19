package kpi.ua.dinojump.entities;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import static kpi.ua.dinojump.Runner.BaseBitmap;

public class ItemDistanceMeter extends BaseEntity {

    private boolean paint;
    private Point spritePos;
    private int x;
    private int y;
    private int maxScore;
    private long highScore;
    private int maxScoreUnits;
    public boolean achievement;
    private String defaultString;
    private int currentDistance;
    private int flashTimer;
    private int flashIterations;
    private long distance;

    public static class config {
        // Number of digits.
        public static final int MAX_DISTANCE_UNITS = 5;
        // Distance that causes achievement animation.
        public static final int ACHIEVEMENT_DISTANCE = 100;
        // Used for conversion from pixel distance to a scaled unit.
        public static final double COEFFICIENT = 0.025;
        // Flash duration in milliseconds.
        public static final int FLASH_DURATION = 1000 / 4;
        // Flash iterations for achievement animation.
        public static final int FLASH_ITERATIONS = 3;
    }

    public static final class dimensions {
        public static final int WIDTH = 10;
        public static final int HEIGHT = 13;
        public static final int DEST_WIDTH = 11;
    }

    public ItemDistanceMeter(Point pos, int w) {
        this.spritePos = pos;
        this.x = 0;
        this.y = 5;
        this.currentDistance = 0;
        this.maxScore = 0;
        this.highScore = 0;
        this.achievement = false;
        this.defaultString = "";
        this.flashTimer = 0;
        this.flashIterations = 0;
        this.maxScoreUnits = config.MAX_DISTANCE_UNITS;
        this.init(w);
    }

    private void init(int width) {
        String maxDistanceStr = "";
        this.calcXPos(width);
        this.maxScore = this.maxScoreUnits;
        for (int i = 0; i < this.maxScoreUnits; i++) {
            this.defaultString += '0';
            maxDistanceStr += '9';
        }
        this.maxScore = Integer.parseInt(maxDistanceStr);
    }

    private void calcXPos(int canvasWidth) {
        this.x = canvasWidth - (dimensions.DEST_WIDTH *
                (this.maxScoreUnits + 1));
    }

    public void update(Object... args) {
        long deltaTime = 0;
        double distance = 0;
        if (args.length > 0) {
            deltaTime = (long) args[0];
            if (args.length > 1) {
                distance = (double) args[1];
            }
        }
        if (!this.achievement) {
            paint = true;
            distance = this.getActualDistance(distance);
            // Score has gone beyond the initial digit count.
            if (distance > this.maxScore && this.maxScoreUnits ==
                    config.MAX_DISTANCE_UNITS) {
                this.maxScoreUnits++;
            } else {
                this.distance = 0;
            }
            if (distance > 0) {
                this.distance = (long) distance;
                if (distance % config.ACHIEVEMENT_DISTANCE == 0) {
                    this.achievement = true;
                    this.flashTimer = 0;
                }
            }
        } else {
            // Control flashing of the score on reaching acheivement.
            if (this.flashIterations <= config.FLASH_ITERATIONS) {
                this.flashTimer += deltaTime;
                if (this.flashTimer < config.FLASH_DURATION) {
                    paint = false;
                } else if (this.flashTimer >
                        config.FLASH_DURATION * 2) {
                    this.flashTimer = 0;
                    this.flashIterations++;
                    paint = true;
                } else {
                    paint = true;
                }
            } else {
                paint = true;
                this.achievement = false;
                this.flashIterations = 0;
                this.flashTimer = 0;
            }
        }
    }

    public void draw(Canvas canvas) {
        int dis = (int) distance;
        if (paint) {
            for (int i = 0; i < this.maxScoreUnits; i++) {
                int v = (int) ((dis / Math.pow(10, i)) % 10);
                this.DrawNum(canvas, this.maxScoreUnits - 1 - i, v, false);
            }
        }
        for (int i = 0; i < this.maxScoreUnits; i++) {
            int v = (int) ((highScore / Math.pow(10, i)) % 10);
            this.DrawNum(canvas, this.maxScoreUnits - 1 - i, v, true);
        }
    }

    private void DrawNum(Canvas canvas, int digitPos, int value, boolean opt_highScore) {
        int deltaX = x;
        if (opt_highScore) {
            deltaX = this.x - (this.maxScoreUnits * 2) * dimensions.WIDTH;
        }
        Rect sRect = getScaledSource(dimensions.WIDTH * value + spritePos.x,
                                     0 + spritePos.y,
                                     dimensions.WIDTH,
                                     dimensions.HEIGHT);
        Rect tRect = getScaledTarget(dimensions.DEST_WIDTH * digitPos + deltaX,
                                     y + y,
                                     dimensions.WIDTH,
                                     dimensions.HEIGHT);
        canvas.drawBitmap(BaseBitmap, sRect, tRect, null);
    }

    private long getActualDistance(double distance) {
        long ret = distance > 0 ? Math.round(distance * config.COEFFICIENT) : 0;
        return ret;
    }

    public void setHighScore(double highest) {
        highScore = getActualDistance(highest);
    }

    public void reset() {
        update(Long.valueOf(0));
        achievement = false;
    }
}
