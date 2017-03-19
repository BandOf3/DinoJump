package kpi.ua.dinojump.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.HashMap;
import java.util.Map;

import kpi.ua.dinojump.Runner;
import kpi.ua.dinojump.view.GameView;

import static kpi.ua.dinojump.Runner.BaseBitmap;


public class Dino extends BaseEntity {

    public boolean playingIntro;
    public int jumpCount;
    private boolean speedDrop;
    private boolean reachedMinHeight;
    private double jumpVelocity;
    public boolean jumping;
    private int currentFrame;
    private int groundYPos;

    private Point spritePos;
    private int x;
    private int y;
    private int xPos, yPos;
    private boolean ducking = false;
    public Status status = Status.RUNNING;
    private int timer = 0;
    private int minJumpHeight;
    private Map<Status, AnimFrames> animFrames;
    public int[] currentAnimFrames;

    public class AnimFrames {
        public int[] frames;
        public int FPS;

        public AnimFrames(int[] f, int fps) {
            FPS = fps;
            frames = f;
        }
    }

    public static class config {
        public static int DROP_VELOCITY = -5;
        public static double GRAVITY = 0.6;
        public static int HEIGHT = 47;
        public static int INITIAL_JUMP_VELOCITY = -10;
        public static int MAX_JUMP_HEIGHT = 30;
        public static int MIN_JUMP_HEIGHT = 30;
        public static int SPEED_DROP_COEFFICIENT = 3;
        public static int WIDTH = 44;
        public static int WIDTH_DUCK = 59;
    }


    public enum Status {
        CRASHED, DUCKING, JUMPING, RUNNING, WAITING
    }

    public Dino(Point s) {
        animFrames = new HashMap<>();
        animFrames.put(Status.WAITING, new AnimFrames(new int[]{44, 0}, 3));
        animFrames.put(Status.RUNNING, new AnimFrames(new int[]{88, 132}, 12));
        animFrames.put(Status.CRASHED, new AnimFrames(new int[]{220}, 60));
        animFrames.put(Status.JUMPING, new AnimFrames(new int[]{0}, 60));
        animFrames.put(Status.DUCKING, new AnimFrames(new int[]{262, 321}, 8));
        this.spritePos = s;
        this.xPos = 0;
        this.yPos = 0;
        // Position when on the ground.
        this.groundYPos = 0;
        this.currentFrame = 0;
        this.timer = 0;
        this.status = Status.WAITING;
        this.jumping = false;
        this.ducking = false;
        this.jumpVelocity = 0;
        this.reachedMinHeight = false;
        this.speedDrop = false;
        this.jumpCount = 0;
        init();
    }

    private void init() {
        this.groundYPos = Runner.defaultDimensions.HEIGHT - config.HEIGHT - Runner.config.BOTTOM_PAD;
        this.yPos = this.groundYPos;
        this.minJumpHeight = this.groundYPos - config.MIN_JUMP_HEIGHT;
        xPos = 100;
        yPos = 100;
        this.update(Long.valueOf(1), Status.WAITING);
    }

    public void update(long deltaTime) {
        if (jumping) updateJump();
        this.timer += deltaTime;
        this.draw(this.currentAnimFrames[this.currentFrame], 0);
        this.currentFrame = this.currentFrame == this.currentAnimFrames.length - 1 ? 0 : this.currentFrame + 1;
        this.timer = 0;
    }

    public void update(long deltaTime, Status status) {
        Status opt_status = status;
        if (opt_status != null) {
            this.status = opt_status;
            this.currentFrame = 0;
            int fps = animFrames.get(opt_status).FPS;
            GameView.SetFPS(fps);
            this.currentAnimFrames = animFrames.get(opt_status).frames;
        }
        if (jumping) updateJump();
        this.timer += deltaTime;
        this.draw(this.currentAnimFrames[this.currentFrame], 0);
        this.currentFrame = this.currentFrame ==
                this.currentAnimFrames.length - 1 ? 0 : this.currentFrame + 1;
        this.timer = 0;
    }

    private void updateJump() {
        int framesElapsed = 1;
        if (this.speedDrop) {
            this.yPos += Math.round(this.jumpVelocity *
                    config.SPEED_DROP_COEFFICIENT * framesElapsed);
        } else {
            this.yPos += Math.round(this.jumpVelocity * framesElapsed);
        }
        this.jumpVelocity += config.GRAVITY * framesElapsed;
        // Minimum height has been reached.
        if (this.yPos < this.minJumpHeight || this.speedDrop) {
            this.reachedMinHeight = true;
        }
        // Reached max height
        if (this.yPos < config.MAX_JUMP_HEIGHT || this.speedDrop) {
            this.endJump();
        }
        // Back down at ground level. Jump completed.
        if (this.yPos > this.groundYPos) {
            this.reset();
            this.jumpCount++;
        }
    }

    public void reset() {
        this.yPos = this.groundYPos;
        this.jumpVelocity = 0;
        this.jumping = false;
        this.ducking = false;
        this.update((long) 0, Status.RUNNING);
        this.speedDrop = false;
        this.jumpCount = 0;
    }

    private void draw(int vx, int vy) {
        x = vx;
        y = vy;
    }

    public void startJump(int speed) {
        if (!this.jumping) {
            this.update((long) 0, Status.JUMPING);
            // Tweak the jump velocity based on the speed.
            this.jumpVelocity = config.INITIAL_JUMP_VELOCITY - (speed / 10);
            this.jumping = true;
            this.reachedMinHeight = false;
            this.speedDrop = false;
        }
    }

    public void endJump() {
        if (this.reachedMinHeight &&
                this.jumpVelocity < config.DROP_VELOCITY) {
            this.jumpVelocity = config.DROP_VELOCITY;
        }
    }

    public void draw(Canvas canvas) {
        int sourceX = x;
        int sourceY = y;
        int sourceWidth = this.ducking && this.status != Status.CRASHED ?
                config.WIDTH_DUCK : config.WIDTH;
        int sourceHeight = config.HEIGHT;
        // Adjustments for sprite sheet position.
        sourceX += this.spritePos.x;
        sourceY += this.spritePos.y;
        if (this.ducking && this.status != Status.CRASHED) {
            canvas.drawBitmap(
                    BaseBitmap,
                    new Rect(sourceX, sourceY, sourceWidth, sourceHeight),
                    new Rect(xPos, yPos, config.WIDTH_DUCK, config.HEIGHT),
                    null);
        } else {
            Paint p = new Paint();
            p.setColor(Color.RED);
            p.setStrokeWidth(2f);
            if (this.ducking && this.status == Status.CRASHED) {
                this.xPos++;
            }
            Rect sRect = getScaledSource(sourceX, sourceY, sourceWidth, sourceHeight);
            Rect tRect = getScaledTarget(xPos, yPos, config.WIDTH, config.HEIGHT);
            canvas.drawBitmap(
                    BaseBitmap,
                    sRect,
                    tRect,
                    null);

        }
    }
}
