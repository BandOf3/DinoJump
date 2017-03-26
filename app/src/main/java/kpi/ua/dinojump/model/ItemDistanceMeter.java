package kpi.ua.dinojump.model;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import static kpi.ua.dinojump.Runner.BaseBitmap;
import static kpi.ua.dinojump.model.BaseEntity.*;


public class ItemDistanceMeter extends BaseEntity{

    private boolean paint;
    private Point spritePos;
    private int x;
    private int y;
    private int maxScore;
    private long highScore;
    private int maxScoreUnits;
    private boolean achievement;
    private String defaultString;
    private int flashTimer;
    private int flashIterations;
    private long distance;

    // Number of digits.
    private static final int MAX_DISTANCE_UNITS = 5;
    // Distance that causes achievement animation.
    private static final int ACHIEVEMENT_DISTANCE = 100;
    // Used for conversion from pixel distance to a scaled unit.
    private static final double COEFFICIENT = 0.025;
    // Flash duration in milliseconds.
    private static final int FLASH_DURATION = 1000 / 4;
    // Flash iterations for achievement animation.
    private static final int FLASH_ITERATIONS = 3;

    private static final int WIDTH = 10;
    private static final int HEIGHT = 13;
    private static final int DEST_WIDTH = 11;

    public ItemDistanceMeter(Point pos, int w) {
        this.spritePos = pos;
        this.x = 0;
        this.y = 5;
        this.maxScore = 0;
        this.highScore = 0;
        this.achievement = false;
        this.defaultString = "";
        this.flashTimer = 0;
        this.flashIterations = 0;
        this.maxScoreUnits = MAX_DISTANCE_UNITS;
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
        this.x = canvasWidth - (DEST_WIDTH *
                (this.maxScoreUnits + 1));
    }

    public void update(long deltaTime) {
        update(deltaTime, 0.);
    }

    public void update(long deltaTime, double distance) {
        if (!this.achievement) {
            paint = true;
            distance = this.getActualDistance(distance);
            // Score has gone beyond the initial digit count.
            if (distance > this.maxScore && this.maxScoreUnits ==
                    MAX_DISTANCE_UNITS) {
                this.maxScoreUnits++;
            } else {
                this.distance = 0;
            }
            if (distance > 0) {
                this.distance = (long) distance;
                if (distance % ACHIEVEMENT_DISTANCE == 0) {
                    this.achievement = true;
                    this.flashTimer = 0;
                }
            }
        } else {
            // Control flashing of the score on reaching acheivement.
            if (this.flashIterations <= FLASH_ITERATIONS) {
                this.flashTimer += deltaTime;
                if (this.flashTimer < FLASH_DURATION) {
                    paint = false;
                } else if (this.flashTimer >
                        FLASH_DURATION * 2) {
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
            deltaX = this.x - (this.maxScoreUnits * 2) * WIDTH;
        }
        Rect sRect = getScaledSource(WIDTH * value + spritePos.x,
                spritePos.y,
                WIDTH,
                HEIGHT);
        Rect tRect = getScaledTarget(DEST_WIDTH * digitPos + deltaX,
                y + y,
                WIDTH,
                HEIGHT);
        canvas.drawBitmap(BaseBitmap, sRect, tRect, null);
    }

    private long getActualDistance(double distance) {
        return distance > 0 ? Math.round(distance * COEFFICIENT) : 0;
    }

    public void setHighScore(double highest) {
        highScore = getActualDistance(highest);
    }

    public void reset() {
        update(0L);
        achievement = false;
    }
}
