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

    public int BLINK_TIMING = 7000;
    public boolean playingIntro;
    private boolean midair = false;
    private int jumpspotX;
    public int jumpCount;
    private boolean speedDrop;
    private boolean reachedMinHeight;
    private double jumpVelocity;
    public boolean jumping;
    private int blinkDelay;
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
        public static int HEIGHT_DUCK = 25;
        public static int INIITAL_JUMP_VELOCITY = -10;
        public static int INTRO_DURATION = 1500;
        public static int MAX_JUMP_HEIGHT = 30;
        public static int MIN_JUMP_HEIGHT = 30;
        public static int SPEED_DROP_COEFFICIENT = 3;
        public static int SPRITE_WIDTH = 262;
        public static int START_X_POS = 50;
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
        //this.currentAnimFrames = [];
        this.blinkDelay = 0;
        this.timer = 0;
        this.status = Status.WAITING;
        this.jumping = false;
        this.ducking = false;
        this.jumpVelocity = 0;
        this.reachedMinHeight = false;
        this.speedDrop = false;
        this.jumpCount = 0;
        this.jumpspotX = 0;
        Init();
    }

    private void Init() {
        this.blinkDelay = (int) (Math.ceil(Math.random() * BLINK_TIMING));
        this.groundYPos = Runner.defaultDimensions.HEIGHT - config.HEIGHT -
                Runner.config.BOTTOM_PAD;
        this.yPos = this.groundYPos;
        this.minJumpHeight = this.groundYPos - config.MIN_JUMP_HEIGHT;
        //this.draw(0, 0);
        //this.update(0, Trex.status.WAITING);
        xPos = 100;
        yPos = 100;
        this.update((long) 0, Status.WAITING);
    }

    @Override
    public void update(Object... args) {
        long deltaTime = 0;
        if (args.length > 0) {
            deltaTime = (long) args[0];
            if (args.length > 1) {
                Status opt_status = (Status) args[1];
                if (opt_status != null) {
                    this.status = opt_status;
                    this.currentFrame = 0;
                    int fps = animFrames.get(opt_status).FPS;
                    GameView.SetFPS(fps);
                    this.currentAnimFrames = animFrames.get(opt_status).frames;
                    if (opt_status == Status.WAITING) {
                        //this.animStartTime = getTimeStamp();
                        //this.setBlinkDelay();
                    }
                }
            }
        }

        if (jumping)
            updateJump(args);
        this.timer += deltaTime;
        // Update the status.
        // Game intro animation, T-rex moves in from the left.

        if (this.status == Status.WAITING) {
            //this.blink(getTimeStamp());
        } else {
            this.draw(this.currentAnimFrames[this.currentFrame], 0);
        }
        this.currentFrame = this.currentFrame ==
                this.currentAnimFrames.length - 1 ? 0 : this.currentFrame + 1;
        this.timer = 0;
        if (this.speedDrop && this.yPos == this.groundYPos) {
            this.speedDrop = false;
            this.setDuck(true);
        }
    }

    private void updateJump(Object... args) {
        long deltaTime = (long) args[0];
        //int msPerFrame = Trex.animFrames[this.status].msPerFrame;
        //var framesElapsed = deltaTime / msPerFrame;
        // Speed drop makes Trex fall faster.
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
        //this.DoUpdate(deltaTime);
    }

    public void reset() {
        this.yPos = this.groundYPos;
        this.jumpVelocity = 0;
        this.jumping = false;
        this.ducking = false;
        this.update((long) 0, Status.RUNNING);
        this.midair = false;
        this.speedDrop = false;
        this.jumpCount = 0;
    }

    private void setDuck(boolean isDucking) {
        if (isDucking && this.status != Status.DUCKING) {
            this.update(0, Status.DUCKING);
            this.ducking = true;
        } else if (this.status == Status.DUCKING) {
            this.update(0, Status.RUNNING);
            this.ducking = false;
        }
    }

    private void draw(int vx, int vy) {
        x = vx;
        y = vy;
    }

    public void startJump(int speed) {
        if (!this.jumping) {
            this.update((long) 0, Status.JUMPING);
            // Tweak the jump velocity based on the speed.
            this.jumpVelocity = config.INIITAL_JUMP_VELOCITY - (speed / 10);
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

    @Override
    public void draw(Canvas canvas) {
        int sourceX = x;
        int sourceY = y;
        int sourceWidth = this.ducking && this.status != Status.CRASHED ?
                config.WIDTH_DUCK : config.WIDTH;
        int sourceHeight = config.HEIGHT;
        // Adjustments for sprite sheet position.
        sourceX += this.spritePos.x;
        sourceY += this.spritePos.y;
        // Ducking.
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
            double scale = Scale;
            double scale2 = ScaleTarget;
            int sl = (int) (sourceX * scale);
            int st = (int) (sourceY * scale);
            int sr = (int) (sl + sourceWidth * scale);
            int sb = (int) (st + sourceHeight * scale);
            int tl = (int) (this.xPos * scale2);
            int tt = (int) (this.yPos * scale2);
            int tr = (int) (tl + config.WIDTH * scale2);
            int tb = (int) (tt + config.HEIGHT * scale2);
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
