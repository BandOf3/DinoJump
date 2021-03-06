package kpi.ua.dinojump.core;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import kpi.ua.dinojump.Constants;
import kpi.ua.dinojump.model.BaseEntity;
import kpi.ua.dinojump.model.Dino;
import kpi.ua.dinojump.model.Horizon;
import kpi.ua.dinojump.model.ItemDistanceMeter;
import kpi.ua.dinojump.model.Obstacle;
import kpi.ua.dinojump.view.GameViewContract;

/**
 * Encapsulates logic of the game, mainly calculations physical positions of the objects.
 */
public class GameLogic implements GameLogicContract, View.OnTouchListener  {

    private double mDistanceRan;
    private double mHighestScore;
    private long mRunningTime;
    private double mCurrentSpeed;
    private int mIntroFramesPassed;
    private final int mFps;

    private boolean mPlaying;
    private boolean mStarted;
    private boolean mBeginning = true;
    private boolean mPlayingIntro;
    private boolean mGameOver;

    private final Dino mDino;
    private final Horizon mHorizon;
    private final ItemDistanceMeter mDistanceMeter;

    private final GameViewContract mGameContract;
    private final List<BaseEntity> mDrawableEntities;

    public GameLogic(GameViewContract gameContract, Point dimensions, int fps, Context context) {
        this.mPlaying = true;
        this.mGameContract = gameContract;
        mFps = fps;
        mCurrentSpeed = Constants.SPEED;
        mHorizon = new Horizon(dimensions, Constants.GAP_COEFFICIENT);
        mDistanceMeter = new ItemDistanceMeter(Constants.TEXT_SPRITE, dimensions.x, context);
        mDino = new Dino(Constants.TREX);
        mDrawableEntities = Arrays.asList(mHorizon, mDistanceMeter, mDino);
    }

    @Override
    public void update(long deltaTime) {
        if (!mStarted) {
            return;
        }
        mRunningTime += deltaTime;
        boolean showObstacles = mRunningTime > Constants.CLEAR_TIME;
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
            if (mCurrentSpeed < Constants.MAX_SPEED) {
                mCurrentSpeed += Constants.ACCELERATION;
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
        mDino.update(Dino.DinoState.CRASHED);
        // Update the high score.
        if (mDistanceRan > mHighestScore) {
            mHighestScore = Math.ceil(mDistanceRan);
            mDistanceMeter.setHighScore(mHighestScore);
        }
        mGameContract.gameOver();
    }

    private boolean checkForCollision(Obstacle obstacle, Dino tRex) {
        return Rect.intersects(obstacle.getCollisionDetector(), tRex.getCollisionBox());
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
    }

    private void startWithIntro() {
        if (!mStarted && !mGameOver) {
            log("startWithIntro");
            mPlayingIntro = true;
        } else if (mGameOver) {
            restart();
        }
    }

    private void restart() {
        log("restart");
        if (!mStarted) {
            mBeginning = false;
            mStarted = true;
            mRunningTime = 0;
            mGameOver = false;
            mDistanceRan = 0;
            setSpeed(Constants.SPEED);
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
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!mStarted) {
            restart();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (motionEvent.getX() < view.getWidth() / 2) {
                mDino.startDuck();
            } else {
                mDino.startJump();
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (motionEvent.getX() < view.getWidth() / 2) {
                mDino.endDuck();
            } else {
                mDino.endJump();
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean isPlaying() {
        return mPlaying;
    }

    @Override
    public boolean isRunning() {
        return mStarted;
    }

    @Override
    public boolean isBeginning() {
        return mBeginning;
    }

    // TODO: Implement
    public void pause() {
        mPlaying = false;
    }

    public void resume() {
        mPlaying = true;
    }
}
