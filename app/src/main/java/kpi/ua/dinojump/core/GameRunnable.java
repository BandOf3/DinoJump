package kpi.ua.dinojump.core;

import android.os.Handler;
import android.util.Log;

/**
 * Manages scheduling logic of the GameActivity.
 */
public class GameRunnable implements Runnable {

    private static final String LOG_TAG = GameRunnable.class.getName();

    private final GameLogicContract mGameLogicContract;
    private final GameViewContract mGameViewContract;
    private final Handler mHandler;
    private final int mFps;

    private long mNextFrameMillis;

    public GameRunnable(GameLogicContract gameLogicContract, GameViewContract gameViewContract,
                        Handler mHandler, int mFps) {
        this.mGameLogicContract = gameLogicContract;
        this.mGameViewContract = gameViewContract;
        this.mHandler = mHandler;
        this.mFps = mFps;
    }

    @Override
    public void run() {
        if (mGameLogicContract.isPlaying()) {
            if (mNextFrameMillis == -1) {
                mNextFrameMillis = System.currentTimeMillis();
            }

            mGameLogicContract.update(System.currentTimeMillis() - mNextFrameMillis);
            mGameViewContract.draw();
            long delta = getFrameDelta(mFps);
            mNextFrameMillis += delta;
            Log.d(LOG_TAG, String.format("nextFrame: %d, delta: %d",  mNextFrameMillis, delta));
            mHandler.postDelayed(this, mNextFrameMillis - System.currentTimeMillis());
        }
    }

    private long getFrameDelta(int fps) {
        return 1000L / fps;
    }
}
