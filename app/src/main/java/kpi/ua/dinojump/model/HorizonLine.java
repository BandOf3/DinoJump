package kpi.ua.dinojump.model;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import static kpi.ua.dinojump.Constants.FPS;

public class HorizonLine extends BaseEntity {

    private int[] sourceXPos = new int[2];
    private double[] xPos = new double[2];
    private final int yPos = 127;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 12;

    public HorizonLine(Point pos) {
        spritePos = pos;
        sourceXPos[0] = spritePos.x;
        sourceXPos[1] = spritePos.x + WIDTH;
        xPos[0] = 0;
        xPos[1] = WIDTH;
    }

    private void updateXPos(int pos, double increment) {
        int line1 = pos;
        int line2 = pos == 0 ? 1 : 0;
        xPos[line1] -= increment;
        xPos[line2] = xPos[line1] + WIDTH;
        if (xPos[line1] <= -WIDTH) {
            xPos[line1] += WIDTH * 2;
            xPos[line2] = xPos[line1] - WIDTH;
            sourceXPos[line1] = spritePos.x;
        }
    }

    public void update(long deltaTime, double speed) {
        double increment = speed * FPS * deltaTime / 1000;
        if (xPos[0] <= 0) {
            updateXPos(0, increment);
        } else {
            updateXPos(1, increment);
        }
    }

    public void draw(Canvas canvas) {
        Rect sRect1 = getScaledSource(sourceXPos[0], spritePos.y, WIDTH, HEIGHT);
        Rect tRect1 = getScaledTarget((int) xPos[0], yPos, WIDTH, HEIGHT);
        canvas.drawBitmap(getBaseBitmap(), sRect1, tRect1, null);
        Rect sRect2 = getScaledSource(sourceXPos[1], spritePos.y, WIDTH, HEIGHT);
        Rect tRect2 = getScaledTarget((int) xPos[1], yPos, WIDTH, HEIGHT);
        canvas.drawBitmap(getBaseBitmap(), sRect2, tRect2, null);
    }

    public void reset() {
        this.xPos[0] = 0;
        this.xPos[1] = WIDTH;
    }
}
