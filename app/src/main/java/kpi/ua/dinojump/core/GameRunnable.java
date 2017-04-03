package kpi.ua.dinojump.core;

import android.os.Handler;
import android.util.Log;

import kpi.ua.dinojump.view.GameViewContract;

/**
 * Manages scheduling logic of the GameActivity.
 */
public class GameRunnable implements Runnable {

    private static final String LOG_TAG = GameRunnable.class.getName();

    private final GameLogicContract mGameLogicContract;
    private final GameViewContract mGameViewContract;
    private final Handler mHandler;
    private final int mFps;

    private long mLastFrameMillis = -1;

    public GameRunnable(GameLogicContract gameLogicContract, GameViewContract gameViewContract,
                        Handler mHandler, int mFps) {
        this.mGameLogicContract = gameLogicContract;
        this.mGameViewContract = gameViewContract;
        this.mHandler = mHandler;
        this.mFps = mFps / 3;
    }

    @Override
    public void run() {
        if (mGameLogicContract.isPlaying()) {
            if (mLastFrameMillis == -1) {
                mLastFrameMillis = System.currentTimeMillis();
            }

            if (mGameLogicContract.isBeginning() || mGameLogicContract.isRunning()) {
                mGameLogicContract.update(System.currentTimeMillis() - mLastFrameMillis);
                mGameViewContract.draw();
            }
            long delta = getFrameDelta(mFps);
            mLastFrameMillis = System.currentTimeMillis();
            Log.d(LOG_TAG, String.format("lastFrame: %d, delta: %d",  mLastFrameMillis, delta));
            mHandler.postDelayed(this, delta);
        }
    }

    private long getFrameDelta(int fps) {
        return 1000L / fps;
    }
}
