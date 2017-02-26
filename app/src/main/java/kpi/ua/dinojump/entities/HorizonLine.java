package kpi.ua.dinojump.entities;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Pair;

import kpi.ua.dinojump.view.GameView;

import static kpi.ua.dinojump.Runner.BaseBitmap;

public class HorizonLine extends BaseEntity {

    Point spritePos;
    int[] sourceXPos = new int[2];
    double[] xPos = new double[2];
    int yPos;

    public static class dimensions {
        public static int WIDTH = 600;
        public static int HEIGHT = 12;
        public static int YPOS = 127;
    }

    public HorizonLine(Point pos) {
        spritePos = pos;
        sourceXPos[0] = spritePos.x;
        sourceXPos[1] = spritePos.x + dimensions.WIDTH;
        xPos[0] = 0;
        xPos[1] = dimensions.WIDTH;
        yPos = dimensions.YPOS;
    }

    private void UpdateXPos(int pos, double increment) {
        int line1 = pos;
        int line2 = pos == 0 ? 1 : 0;
        xPos[line1] -= increment;
        xPos[line2] = xPos[line1] + dimensions.WIDTH;
        if (xPos[line1] <= -dimensions.WIDTH) {
            xPos[line1] += dimensions.WIDTH * 2;
            xPos[line2] = xPos[line1] - dimensions.WIDTH;
            sourceXPos[line1] = spritePos.x;
        }
    }

    private int counter = 0;
    private long cTime;
    private double cSpeed;

    public Pair<Long, Double> getAvgDeltaTime() {
        if (counter <= 0)
            counter = 1;
        Pair<Long, Double> ret = new Pair<>(cTime / counter, cSpeed / counter);
        counter = 0;
        cTime = 0;
        cSpeed = 0;
        return ret;
    }

    @Override
    public void update(Object... args) {
        long deltaTime = 0;
        double speed = 0;
        if (args.length > 0) {
            deltaTime = (long) args[0];
            if (args.length > 1)
                speed = (double) args[1];
        }
        cSpeed += speed;
        cTime += deltaTime;
        counter++;
        int fps = GameView.FPS;
        double increment = speed * fps * deltaTime / 1000;
        if (xPos[0] <= 0) {
            UpdateXPos(0, increment);
        } else {
            UpdateXPos(1, increment);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Rect sRect1 = getScaledSource(sourceXPos[0], spritePos.y, dimensions.WIDTH, dimensions.HEIGHT);
        Rect tRect1 = getScaledTarget((int) xPos[0], yPos, dimensions.WIDTH, dimensions.HEIGHT);
        canvas.drawBitmap(
                BaseBitmap,
                sRect1,
                tRect1,
                null);
        Rect sRect2 = getScaledSource(sourceXPos[1], spritePos.y, dimensions.WIDTH, dimensions.HEIGHT);
        Rect tRect2 = getScaledTarget((int) xPos[1], yPos, dimensions.WIDTH, dimensions.HEIGHT);
        canvas.drawBitmap(
                BaseBitmap,
                sRect2,
                tRect2,
                null);
    }

    public void reset() {
        this.xPos[0] = 0;
        this.xPos[1] = dimensions.WIDTH;
    }
}
