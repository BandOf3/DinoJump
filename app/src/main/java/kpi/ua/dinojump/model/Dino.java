package kpi.ua.dinojump.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.HashMap;
import java.util.Map;

import kpi.ua.dinojump.Runner;

import static kpi.ua.dinojump.Runner.BaseBitmap;


public class Dino extends BaseEntity {

    private static int DROP_VELOCITY = -5;
    private static double GRAVITY = 0.6;
    private static int HEIGHT = 47;
    private static int INITIAL_JUMP_VELOCITY = -10;
    private static int MAX_JUMP_HEIGHT = 30;
    private static int MIN_JUMP_HEIGHT = 30;
    private static int SPEED_DROP_COEFFICIENT = 3;
    private static int WIDTH = 44;
    private static int WIDTH_DUCK = 59;

    //Possible dino states
    public enum DinoState {CRASHED, DUCKING, JUMPING, RUNNING, WAITING}
    private double jumpVelocity;
    public boolean jumping, ducking, playingIntro , speedDrop, reachedMinHeight;
    private int groundYPos;
    private Rect collisionBox;
    private int xPos, yPos, animFrameX, animFrameY, currentFrame;
    private DinoState currStatus;
    private int minJumpHeight;
    private Map<DinoState, AnimFrames> animFrames;
    private int[] currentAnimFrames;

    public Dino(Point s) {
        this.spritePos = s;
        this.xPos = 0;
        this.yPos = 0;
        // Position when on the ground.
        this.groundYPos = 0;
        this.currentFrame = 0;
        this.currStatus = DinoState.WAITING;
        this.jumping = false;
        this.ducking = false;
        this.jumpVelocity = 0;
        this.reachedMinHeight = false;
        this.speedDrop = false;
        init();
    }

    private void init() {
        initAnimationFrames();
        collisionBox = new Rect(xPos, yPos, WIDTH, HEIGHT);
        this.groundYPos = Runner.HEIGHT - HEIGHT - Runner.BOTTOM_PAD;
        this.yPos = this.groundYPos;
        this.minJumpHeight = this.groundYPos - MIN_JUMP_HEIGHT;
        xPos = 100;
        yPos = 100;
        this.update(DinoState.WAITING);
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
        if (jumping) updateJump();
        this.draw(this.currentAnimFrames[this.currentFrame], 0);
        this.currentFrame = this.currentFrame == this.currentAnimFrames.length - 1 ? 0 : this.currentFrame + 1;
        collisionBox.left = xPos;
        collisionBox.top = yPos;
        collisionBox.right = xPos + ((currStatus == DinoState.DUCKING) ? WIDTH_DUCK : WIDTH);
        collisionBox.bottom = yPos + HEIGHT;
    }
    public void update() {
        defaultUpdate();
    }

    public void update(DinoState status) {
        DinoState opt_status = status;
        if (opt_status != null) {
            this.currStatus = opt_status;
            this.currentFrame = 0;
            this.currentAnimFrames = animFrames.get(opt_status).frames;
        }
        defaultUpdate();
    }

    private void updateJump() {
        int framesElapsed = 1;
        double speedDropCoefficient = framesElapsed * ((this.speedDrop) ? SPEED_DROP_COEFFICIENT : 1);

        this.yPos += Math.round(this.jumpVelocity * speedDropCoefficient);
        this.jumpVelocity += GRAVITY * framesElapsed;

        // Minimum height has been reached.
        if (this.yPos < this.minJumpHeight || this.speedDrop) {
            this.reachedMinHeight = true;
        }
        // Reached max height
        if (this.yPos < MAX_JUMP_HEIGHT || this.speedDrop) {
            this.endJump();
        }
        // Back down at ground level. Jump completed.
        if (this.yPos > this.groundYPos) {
            this.reset();
        }
    }

    public void reset() {
        this.yPos = this.groundYPos;
        this.jumpVelocity = 0;
        this.jumping = false;
        this.ducking = false;
        this.update(DinoState.RUNNING);
        this.speedDrop = false;
    }

    private void draw(int vx, int vy) {
        animFrameX = vx;
        animFrameY = vy;
    }

    public void startJump(int speed) {
        if (!this.jumping) {
            this.update(DinoState.JUMPING);
            // Tweak the jump velocity based on the speed.
            this.jumpVelocity = INITIAL_JUMP_VELOCITY - (speed / 10);
            this.jumping = true;
            this.reachedMinHeight = false;
            this.speedDrop = false;
        }
    }

    public void endJump() {
        if (this.reachedMinHeight &&
                this.jumpVelocity < DROP_VELOCITY) {
            this.jumpVelocity = DROP_VELOCITY;
        }
    }

    private boolean isValidDuckingState() {
        return this.ducking && this.currStatus != DinoState.CRASHED;
    }

    public void draw(Canvas canvas) {
        Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        int animFrameX = this.animFrameX;
        int animFrameY = this.animFrameY;
        int sourceWidth = isValidDuckingState() ? WIDTH_DUCK : WIDTH;
        int sourceHeight = HEIGHT;
        // Adjustments for sprite sheet position.
        animFrameX += this.spritePos.x;
        animFrameY += this.spritePos.y;
        if (isValidDuckingState()) {
            canvas.drawBitmap(BaseBitmap,
                    new Rect(animFrameX, animFrameY, sourceWidth, sourceHeight),
                    new Rect(xPos, yPos, WIDTH_DUCK, HEIGHT),
                    null);
        } else {
            Paint p = new Paint();
            p.setColor(Color.RED);
            p.setStrokeWidth(2f);
            if (this.ducking && this.currStatus == DinoState.CRASHED) {
                this.xPos++;
            }
            Rect sRect = getScaledSource(animFrameX, animFrameY, sourceWidth, sourceHeight);
            Rect tRect = getScaledTarget(xPos, yPos, WIDTH, HEIGHT);
            canvas.drawBitmap(BaseBitmap,sRect,tRect,bitmapPaint);

        }
    }

    public Rect getCollisionBox() {
        return collisionBox;
    }

    public class AnimFrames {
        public int[] frames;
        public int FPS;
        public AnimFrames(int[] f, int fps) {
            FPS = fps;
            frames = f;
        }
    }
}