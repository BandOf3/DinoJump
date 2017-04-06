package kpi.ua.dinojump.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.HashMap;
import java.util.Map;

import kpi.ua.dinojump.Constants;


public class Dino extends BaseEntity {

    private static final int HEIGHT = 47;
    private static final int HEIGHT_DUCK = 22;
    private static final int WIDTH = 44;
    private static final int WIDTH_DUCK = 59;
    private int GROUND_POS;

    //Possible dino states
    public enum DinoState {
        CRASHED, DUCKING, JUMPING, RUNNING, WAITING
    }

    private DinoState preCrashState = null;

    private double velocityY;

    private Rect collisionBox;
    private int xPos, yPos, animFrameX, animFrameY, currentFrame;
    private DinoState currStatus;
    private Map<DinoState, AnimFrames> animFrames;
    private int[] currentAnimFrames;

    public Dino(Point s) {
        spritePos = s;
        init();
    }

    private void init() {
        initAnimationFrames();
        updateGroundPosition();
        yPos = GROUND_POS;
        xPos = 100;
        collisionBox = new Rect(xPos, yPos, WIDTH, HEIGHT);
        update(DinoState.WAITING);
    }

    private void updateGroundPosition() {
        GROUND_POS = Constants.HEIGHT - (isDucking() ? HEIGHT_DUCK : HEIGHT) - Constants.BOTTOM_PAD;
    }

    private void initAnimationFrames() {
        animFrames = new HashMap<>();
        animFrames.put(DinoState.WAITING, new AnimFrames(new int[]{44, 0}));
        animFrames.put(DinoState.RUNNING, new AnimFrames(new int[]{88, 132}));
        animFrames.put(DinoState.CRASHED, new AnimFrames(new int[]{220}));
        animFrames.put(DinoState.JUMPING, new AnimFrames(new int[]{0}));
        animFrames.put(DinoState.DUCKING, new AnimFrames(new int[]{264, 323}));
    }

    public void update() {
        if (isJumping()) {
            updateJump();
        } else {
            // update y coordinate
            updateGroundPosition();
            yPos = GROUND_POS;
        }
        animFrameX = currentAnimFrames[currentFrame];
        animFrameY = 0;
        currentFrame = currentFrame == currentAnimFrames.length - 1 ? 0 : currentFrame + 1;
        collisionBox.left = xPos;
        collisionBox.top = yPos;
        collisionBox.right = xPos + (isDucking() ? WIDTH_DUCK : WIDTH);
        collisionBox.bottom = yPos + HEIGHT;
    }

    public void update(DinoState status) {
        if (status != null) {
            if (status == DinoState.CRASHED) {
                if (isDucking()) endDuck();  // for the correct yPos after reset state
                preCrashState = currStatus;
            }
            currStatus = status;
            currentFrame = 0;
            currentAnimFrames = animFrames.get(status).frames;
        }
        update();
    }

    private void updateJump() {
        yPos += Math.round(velocityY);
        velocityY += Constants.GRAVITY;

        // Back down at ground level. Jump completed.
        if (yPos > GROUND_POS) {
            reset();
        }
    }

    public void reset() {
        if (isDucking()) endDuck();
        velocityY = 0;
        update(DinoState.RUNNING);
    }

    public void startDuck() {
        if (currStatus == DinoState.RUNNING) {
            update(DinoState.DUCKING);
            spritePos = Constants.TREX_DUCKING;
        }
    }

    public void endDuck() {
        if (isDucking()) {
            update(DinoState.RUNNING);
            spritePos = Constants.TREX;
        }
    }


    public void startJump() {
        if (!isJumping()) {
            update(DinoState.JUMPING);
            velocityY = Constants.INITIAL_JUMP_VELOCITY;
        }
    }

    public void endJump() {
        if (velocityY < -6.0) {
            velocityY = -6.0;
        }
    }

    public void draw(Canvas canvas) {
        Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        int animFrameX = this.animFrameX;
        int animFrameY = this.animFrameY;
        // Adjustments for sprite sheet position.
        animFrameX += spritePos.x;
        animFrameY += spritePos.y;
        if (currStatus == DinoState.CRASHED && preCrashState == DinoState.DUCKING) {
            xPos++;
        }
        int rWidth = isDucking() ? WIDTH_DUCK : WIDTH;
        int rHeight = isDucking() ? HEIGHT_DUCK : HEIGHT;
        Rect sRect = getScaledSource(animFrameX, animFrameY, rWidth, rHeight);
        Rect tRect = getScaledTarget(xPos, yPos, rWidth, rHeight);
        canvas.drawBitmap(getBaseBitmap(), sRect, tRect, bitmapPaint);
    }

    public Rect getCollisionBox() {
        return collisionBox;
    }

    private class AnimFrames {
        final int[] frames;

        AnimFrames(int[] f) {
            frames = f;
        }
    }

    private boolean isDucking() {
        return currStatus == DinoState.DUCKING;
    }

    private boolean isJumping() {
        return currStatus == DinoState.JUMPING;
    }
}