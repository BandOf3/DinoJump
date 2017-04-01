package kpi.ua.dinojump.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.HashMap;
import java.util.Map;

import kpi.ua.dinojump.Runner;


public class Dino extends BaseEntity {

    private static int DROP_VELOCITY = 4;
    private static double UP_GRAVITY = 0.6;
    private static double DOWN_GRAVITY = 1.8;
    private static int HEIGHT = 47;
    private static int INITIAL_JUMP_VELOCITY = -18;

    // TODO: Make it dependent on the length of press.
    private static int JUMP_HEIGHT = 40;
    private static int WIDTH = 44;
    private static int WIDTH_DUCK = 59;

    //Possible dino states
    public enum DinoState {
        CRASHED, DUCKING, JUMPING, RUNNING, WAITING
    }

    private DinoState preCrashState = null;

    private double jumpVelocity;
    private boolean playingIntro;
    private boolean goingDown;

    private int groundYPos;
    private Rect collisionBox;
    private int xPos, yPos, animFrameX, animFrameY, currentFrame;
    private DinoState currStatus;
    private Map<DinoState, AnimFrames> animFrames;
    private int[] currentAnimFrames;

    public Dino(Bitmap baseBitmap, Point s) {
        super(baseBitmap);
        spritePos = s;
        xPos = 0;
        yPos = 0;
        // Position when on the ground.
        groundYPos = 0;
        currentFrame = 0;
        currStatus = DinoState.WAITING;
        jumpVelocity = 0;
        goingDown = false;
        init();
    }

    private void init() {
        initAnimationFrames();
        collisionBox = new Rect(xPos, yPos, WIDTH, HEIGHT);
        groundYPos = Runner.HEIGHT - HEIGHT - Runner.BOTTOM_PAD;
        yPos = groundYPos;
        xPos = 100;
        yPos = 100;
        update(DinoState.WAITING);
    }

    private void initAnimationFrames() {
        animFrames = new HashMap<>();
        animFrames.put(DinoState.WAITING, new AnimFrames(new int[]{44, 0}, 3));
        animFrames.put(DinoState.RUNNING, new AnimFrames(new int[]{88, 132}, 12));
        animFrames.put(DinoState.CRASHED, new AnimFrames(new int[]{220}, 60));
        animFrames.put(DinoState.JUMPING, new AnimFrames(new int[]{0}, 60));
        animFrames.put(DinoState.DUCKING, new AnimFrames(new int[]{262, 321}, 8));
    }

    private void defaultUpdate() {
        if (isJumping()) {
            updateJump();
        }
        draw(currentAnimFrames[currentFrame], 0);
        currentFrame = currentFrame == currentAnimFrames.length - 1 ? 0 : currentFrame + 1;
        collisionBox.left = xPos;
        collisionBox.top = yPos;
        collisionBox.right = xPos + (isDucking() ? WIDTH_DUCK : WIDTH);
        collisionBox.bottom = yPos + HEIGHT;
    }

    public void update() {
        defaultUpdate();
    }

    public void update(DinoState status) {
        if (status != null) {
            if (status == DinoState.CRASHED) {
                preCrashState = currStatus;
            }
            currStatus = status;
            currentFrame = 0;
            currentAnimFrames = animFrames.get(status).frames;
        }
        defaultUpdate();
    }

    private void updateJump() {
        int framesElapsed = 1;

        yPos += Math.round(jumpVelocity/* * speedDropCoefficient*/);
        jumpVelocity += (goingDown ? DOWN_GRAVITY : UP_GRAVITY) * framesElapsed;

        // Reached max height
        if (yPos < JUMP_HEIGHT) {
            startDescending();
        }
        // Back down at ground level. Jump completed.
        if (yPos > groundYPos) {
            reset();
        }
    }

    public void reset() {
        yPos = groundYPos;
        jumpVelocity = 0;
        update(DinoState.RUNNING);
        goingDown = false;
    }

    private void draw(int vx, int vy) {
        animFrameX = vx;
        animFrameY = vy;
    }

    public void tryJump(int speed) {
        if (!isJumping()) {
            startJump(speed);
        }
    }

    public void tryDuck() {
        if (currStatus == DinoState.RUNNING) {
            update(DinoState.DUCKING);
        }
    }

    private void startJump(int speed) {
        if (!isJumping()) {
            update(DinoState.JUMPING);
            // Tweak the jump velocity based on the speed.
            jumpVelocity = INITIAL_JUMP_VELOCITY - (speed / 10);
            goingDown = false;
        }
    }

    private void startDescending() {
        goingDown = true;
        jumpVelocity = DROP_VELOCITY;
    }

    public void draw(Canvas canvas) {
        Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        int animFrameX = this.animFrameX;
        int animFrameY = this.animFrameY;
        int sourceWidth = isDucking() ? WIDTH_DUCK : WIDTH;
        int sourceHeight = HEIGHT;
        // Adjustments for sprite sheet position.
        animFrameX += spritePos.x;
        animFrameY += spritePos.y;
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setStrokeWidth(2f);
        if (currStatus == DinoState.CRASHED && preCrashState == DinoState.DUCKING) {
            xPos++;
        }
        Rect sRect = getScaledSource(animFrameX, animFrameY, sourceWidth, sourceHeight);
        Rect tRect = getScaledTarget(xPos, yPos, WIDTH, HEIGHT);
        canvas.drawBitmap(baseBitmap, sRect, tRect, bitmapPaint);
    }

    public Rect getCollisionBox() {
        return collisionBox;
    }

    private class AnimFrames {
        final int[] frames;
        final int FPS;

        AnimFrames(int[] f, int fps) {
            FPS = fps;
            frames = f;
        }
    }

    public void setPlayingIntro(boolean playingIntro) {
        this.playingIntro = playingIntro;
    }

    private boolean isDucking() {
        return currStatus == DinoState.DUCKING;
    }

    private boolean isJumping() {
        return currStatus == DinoState.JUMPING;
    }
}