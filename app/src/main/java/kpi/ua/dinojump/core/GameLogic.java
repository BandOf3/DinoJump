package kpi.ua.dinojump.core;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import kpi.ua.dinojump.Runner;
import kpi.ua.dinojump.model.BaseEntity;
import kpi.ua.dinojump.model.Dino;
import kpi.ua.dinojump.model.Horizon;
import kpi.ua.dinojump.model.ItemDistanceMeter;
import kpi.ua.dinojump.model.Obstacle;
import kpi.ua.dinojump.view.GameViewContract;
import kpi.ua.dinojump.view.OnSwipeTouchListener;

/**
 * Encapsulates logic of the game, mainly calculations physical positions of the objects.
 */
public class GameLogic extends OnSwipeTouchListener implements GameLogicContract {

    private double mDistanceRan;
    private double mHighestScore;
    private long mRunningTime;
    private double mCurrentSpeed;
    private int mIntroFramesPassed;
    private final int mFps;

    private boolean mPlaying;
    private boolean mStarted;
    private boolean mPlayingIntro;
    private boolean mGameOver;

    private final Dino mDino;
    private final Horizon mHorizon;
    private final ItemDistanceMeter mDistanceMeter;

    private final GameViewContract mGameContract;
    private final List<BaseEntity> mDrawableEntities;

    public GameLogic(Context context, GameViewContract gameContract, Point dimensions, int fps) {
        super(context);
        this.mPlaying = true;
        this.mGameContract = gameContract;
        mFps = fps;
        mCurrentSpeed = Runner.SPEED;
        mHorizon = new Horizon(dimensions, Runner.GAP_COEFFICIENT);
        mDistanceMeter = new ItemDistanceMeter(Runner.TEXT_SPRITE, dimensions.x);
        mDino = new Dino(Runner.TREX);
        mDrawableEntities = Arrays.asList(mHorizon, mDistanceMeter, mDino);
    }

    @Override
    public void update(long deltaTime) {
        mRunningTime += deltaTime;
        boolean showObstacles = mRunningTime > Runner.CLEAR_TIME;
        // First jump triggers the intro.
        if (!mPlayingIntro) {
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
        } else if (!mPlayingIntro) {
            mDistanceRan += mCurrentSpeed * deltaTime * mFps / 1000L;
            if (mCurrentSpeed < Runner.MAX_SPEED) {
                mCurrentSpeed += Runner.ACCELERATION;
            }
        }
        mDistanceMeter.update(deltaTime, Math.ceil(mDistanceRan));
        if (mGameOver) {
            mStarted = false;
        } else {
            mDino.update();
        }
    }

    public List<BaseEntity> getDrawableEntities() {
        return mDrawableEntities;
    }

    private void gameOver() {
        mGameOver = true;
        mStarted = false;
        mDino.update(Dino.Status.CRASHED);
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
        mHorizon.update(0L, mCurrentSpeed, false);
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
        } else if (mGameOver) {
            restart();
        }
    }

    private void restart() {
        log("restart");
        if (!mStarted) {
            mStarted = true;
            mRunningTime = 0;
            mGameOver = false;
            mDistanceRan = 0;
            setSpeed(Runner.SPEED);
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
        mDino.update(Dino.Status.DUCKING);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!mStarted) {
            restart();
        }
        return super.onTouch(view, motionEvent);
    }

    @Override
    public boolean isPlaying() {
        return mPlaying;
    }

    @Override
    public boolean isRunning() {
        return mStarted;
    }

    // TODO: Implement
    public void pause() {
        mPlaying = false;
    }

    public void resume() {
        mPlaying = true;
    }
}
