package kpi.ua.dinojump.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import static android.content.Context.MODE_PRIVATE;


public class ItemDistanceMeter extends BaseEntity {

    private static final int WIDTH = 10;
    private static final int HEIGHT = 13;
    private static final int DEST_WIDTH = 11;
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

    private boolean paint;
    private int xPos;
    private int yPos;
    private int maxScore;
    private long highScore;
    private int maxScoreUnits;
    private boolean achievement; // animate score after reaching every 100 pts;
    private int flashTimer;
    private int flashIterations;
    private long distance;
    private SharedPreferences highScoreSharedPreferences;

    public ItemDistanceMeter(Point pos, int w, Context context) {
        spritePos = pos;
        highScoreSharedPreferences = context.getSharedPreferences("HighScore", MODE_PRIVATE);
        xPos = 0;
        yPos = 5;
        maxScore = 0;
        highScore = highScoreSharedPreferences.getLong("score", 0L);
        achievement = false;
        flashTimer = 0;
        flashIterations = 0;
        maxScoreUnits = MAX_DISTANCE_UNITS;
        init(w);
    }

    private void init(int width) {
        String maxDistanceStr = "";
        calculateXPosition(width);
        maxScore = maxScoreUnits;
        for (int i = 0; i < maxScoreUnits; i++) {
            maxDistanceStr += '9';
        }
        maxScore = Integer.parseInt(maxDistanceStr);
    }

    private void calculateXPosition(int canvasWidth) {
        xPos = canvasWidth - (DEST_WIDTH * (maxScoreUnits + 1));
    }

    public void update(long deltaTime, double distance) {
        if (achievement) {
            // Control flashing of the score on reaching achievement.
            animateScore(deltaTime);
        } else {
            paint = true;
            distance = getActualDistance(distance);
            // Score has gone beyond the initial digit count.
            if (distance > maxScore && maxScoreUnits == MAX_DISTANCE_UNITS) {
                maxScoreUnits++;
            } else {
                this.distance = 0;
            }
            if (distance > 0) {
                this.distance = (long) distance;
                if (distance % ACHIEVEMENT_DISTANCE == 0) {
                    achievement = true;
                    flashTimer = 0;
                }
            }
        }
    }

    private void animateScore(long deltaTime) {
        if (flashIterations <= FLASH_ITERATIONS) {
            flashTimer += deltaTime;
            if (flashTimer < FLASH_DURATION) {
                paint = false;
            } else if (flashTimer >
                    FLASH_DURATION * 2) {
                flashTimer = 0;
                flashIterations++;
                paint = true;
            } else {
                paint = true;
            }
        } else {
            paint = true;
            achievement = false;
            flashIterations = 0;
            flashTimer = 0;
        }
    }

    public void draw(Canvas canvas) {
        int dis = (int) distance;
        if (paint) {
            for (int i = 0; i < maxScoreUnits; i++) {
                int v = (int) ((dis / Math.pow(10, i)) % 10);
                drawNumber(canvas, maxScoreUnits - 1 - i, v, false);
            }
        }
        for (int i = 0; i < maxScoreUnits; i++) {
            int v = (int) ((highScore / Math.pow(10, i)) % 10);
            drawNumber(canvas, maxScoreUnits - 1 - i, v, true);
        }
    }

    private void drawNumber(Canvas canvas, int digitPos, int value, boolean opt_highScore) {
        int deltaX = xPos;
        if (opt_highScore) {
            deltaX = xPos - (maxScoreUnits * 2) * WIDTH;
        }
        Rect sRect = getScaledSource(WIDTH * value + spritePos.x, spritePos.y, WIDTH, HEIGHT);
        Rect tRect = getScaledTarget(DEST_WIDTH * digitPos + deltaX, yPos + yPos, WIDTH, HEIGHT);
        canvas.drawBitmap(getBaseBitmap(), sRect, tRect, null);
    }

    private long getActualDistance(double distance) {
        return distance > 0 ? Math.round(distance * COEFFICIENT) : 0;
    }

    public void setHighScore(double highest) {
        highScore = getActualDistance(highest);
        SharedPreferences.Editor editor = highScoreSharedPreferences.edit();
        editor.putLong("score", highScore);
        editor.commit();
    }

    public void reset() {
        update(0L, 0.);
        achievement = false;
    }
}
