package kpi.ua.dinojump.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import kpi.ua.dinojump.core.GameLogic;
import kpi.ua.dinojump.core.GameViewContract;
import kpi.ua.dinojump.model.BaseEntity;


public class GameView extends SurfaceView implements GameViewContract {
    public static final String LOG_TAG = GameView.class.getName();
    public static int FPS = 60;

    private final Vibrator mVibrator;
    public int mWidth;
    public int mHeight;

    private final GameLogic mGameLogic;

    public GameView(Context context) {
        super(context);
        init();
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        Point dimensions = new Point(600, 150);
        mGameLogic = new GameLogic(getContext(), this, dimensions, FPS);
    }

    public GameLogic getGameLogic() {
        return mGameLogic;
    }

    private void init() {
        this.setOnTouchListener(mGameLogic);
    }

    // TODO: Implement
    public void pause() {
    }

    public void resume() {
    }

    public void gameOver() {
        mVibrator.vibrate(200);
        log("game over");
        mGameLogic.setPlaying(false);

        // TODO: Make restart after button press.
    }

    @Override
    public void draw() {
        SurfaceHolder surfaceHolder = getHolder();
        if (surfaceHolder == null) {
            log("SurfaceHolder has not been created");
            return;
        }
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) {
            log("Canvas is null");
            return;
        }
        try {
            int w = canvas.getWidth();
            int h = canvas.getHeight();
            if (w != mWidth || h != mHeight) {
                mWidth = w;
                mHeight = h;
                if (mWidth / mHeight >= 4) {
                    BaseEntity.ScaleTarget = (double) mHeight / 150;
                    BaseEntity.startY = 0;
                    BaseEntity.startX = (mWidth - 4 * mHeight) / 2;
                } else {
                    BaseEntity.ScaleTarget = (double) mWidth / 600;
                    BaseEntity.startY = (mHeight - mWidth / 4) / 2;
                    BaseEntity.startX = 0;
                }
            }
            canvas.drawColor(Color.WHITE);

            for (BaseEntity e : mGameLogic.getDrawableEntities()) {
                e.draw(canvas);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        } finally {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void log(String str) {
        Log.d(GameView.class.getName(), str);
    }
}
