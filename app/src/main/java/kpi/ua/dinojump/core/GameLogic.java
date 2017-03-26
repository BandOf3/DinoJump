package kpi.ua.dinojump.core;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import kpi.ua.dinojump.Runner;
import kpi.ua.dinojump.model.BaseEntity;
import kpi.ua.dinojump.model.Dino;
import kpi.ua.dinojump.model.Horizon;
import kpi.ua.dinojump.model.ItemDistanceMeter;
import kpi.ua.dinojump.model.Obstacle;
import kpi.ua.dinojump.view.OnSwipeTouchListener;

/**
 * Encapsulates logic of the game, mainly calculations physical positions of the objects.
 */
public class GameLogic extends OnSwipeTouchListener implements GameLogicContract {

    private double mDistanceRan;
    private boolean mPlayingIntro;
    private double mHighestScore;
    private long mRunningTime;
    private double mCurrentSpeed;
    private int mIntroFramesPassed;
    private final int mFps;

    private boolean mPlaying = true;
    private boolean mStarted;
    private boolean mGameOver;

    private Dino mDino;
    private Horizon mHorizon;
    private ItemDistanceMeter mDistanceMeter;

    private Point mDimensions;

    private final GameViewContract mGameContract;
    private List<BaseEntity> mDrawableEntities;

    public GameLogic(Context context, GameViewContract gameContract, Point dimensions, int fps) {
        super(context);
        this.mGameContract = gameContract;
        mDimensions = dimensions;
        mFps = fps;
        init();
    }

    @Override
    public void update(long deltaTime) {
        mRunningTime += deltaTime;
        boolean showObstacles = mRunningTime > Runner.config.CLEAR_TIME;
        // First jump triggers the intro.
        if (mDino.jumpCount == 1 && !mPlayingIntro) {
            startWithIntro();
        }
        // The mHorizon doesn't move until the intro is over.
        if (mPlayingIntro) {
            playIntro();
        } else {
            mHorizon.update(deltaTime, mCurrentSpeed, showObstacles);
        }
        // Check for collisions.
        if (showObstacles && checkForCollision(mHorizon.getObstacles().get(0), mDino)) {
            gameOver();
        } else {
            mDistanceRan += mCurrentSpeed * deltaTime * mFps / 1000L;
            if (mCurrentSpeed < Runner.config.MAX_SPEED) {
                mCurrentSpeed += Runner.config.ACCELERATION;
            }
        }
        mDistanceMeter.update(deltaTime, Math.ceil(mDistanceRan));
        if (mGameOver) {
            mPlaying = false;
        } else {
            mDino.update(deltaTime / 2);
        }
    }

    public List<BaseEntity> getDrawableEntities() {
        return mDrawableEntities;
    }

    private void init() {
        mCurrentSpeed = Runner.config.SPEED;
        mHorizon = new Horizon(mDimensions, Runner.config.GAP_COEFFICIENT);
        mDistanceMeter = new ItemDistanceMeter(Runner.spritePos.TEXT_SPRITE, mDimensions.x);
        mDino = new Dino(Runner.spritePos.TREX);
        mDrawableEntities = Arrays.asList(mHorizon, mDistanceMeter, mDino);
    }

    private void gameOver() {
        mGameOver = true;
        mDino.update(100, Dino.Status.CRASHED);
        // Update the high score.
        if (mDistanceRan > mHighestScore) {
            mHighestScore = Math.ceil(mDistanceRan);
            mDistanceMeter.setHighScore(mHighestScore);
        }
        mGameContract.gameOver();
    }

    private boolean checkForCollision(Obstacle obstacle, Dino tRex) {
        return Rect.intersects(obstacle.getDetectCollision(), tRex.getCollisionBox());
    }

    private void playIntro() {
        mIntroFramesPassed++;
        if (mIntroFramesPassed > 10) {
            startGame();
        }
        mHorizon.update((long) 0, this.mCurrentSpeed, false);
    }

    private void startGame() {
        mRunningTime = 0;
        mIntroFramesPassed = 0;
        mPlayingIntro = false;
        mDino.playingIntro = false;
    }

    private void startWithIntro() {
        if (!mStarted && !mGameOver) {
            log("startWithIntro");
            mPlayingIntro = true;
            mDino.playingIntro = true;
            mStarted = true;
        } else if (mGameOver) {
            restart();
        }
    }

    public void restart() {
        log("restart");
        if (!mStarted) {
            mStarted = true;
            mRunningTime = 0;
            mGameOver = false;
            mDistanceRan = 0;
            setSpeed(Runner.config.SPEED);
            mDistanceMeter.reset();
            mHorizon.reset();
            mDino.reset();
        }
    }

    private void setSpeed(double speed) {
        this.mCurrentSpeed = speed;
    }

    private void log(String str) {
        Log.d(GameLogic.class.getName(), str);
    }

    @Override
    public void onSwipeTop() {
        if (mGameOver) {
            restart();
        } else {
            if (!mStarted) {
                mStarted = true;
            }
            if (!mDino.jumping) {
                mDino.startJump((int) mCurrentSpeed);
            }
        }
    }

    @Override
    public void onSwipeBottom() {
        mDino.update(1L, Dino.Status.DUCKING);
    }

    @Override
    public boolean isPlaying() {
        return mPlaying;
    }

    public void setPlaying(boolean playing) {
        mPlaying = playing;
    }
}
