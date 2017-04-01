package kpi.ua.dinojump.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import kpi.ua.dinojump.Configuration;
import kpi.ua.dinojump.core.GameLogic;
import kpi.ua.dinojump.model.BaseEntity;

import static kpi.ua.dinojump.Constants.FPS;
import static kpi.ua.dinojump.Constants.GAME_OVER_VIBRATE_LEN;


public class GameView extends SurfaceView implements GameViewContract {
    public static final String LOG_TAG = GameView.class.getName();

    private final Vibrator mVibrator;
    public int mWidth;
    public int mHeight;

    private final GameLogic mGameLogic;

    public GameView(Context context, Bitmap baseBitmap) {
        super(context);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        Point dimensions = new Point(600, 150);
        mGameLogic = new GameLogic(getContext(), this, dimensions, FPS);
        setOnTouchListener(mGameLogic);
    }

    public GameLogic getGameLogic() {
        return mGameLogic;
    }

    public void gameOver() {
        mVibrator.vibrate(GAME_OVER_VIBRATE_LEN);
        log("game over");
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
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();
            if (canvasWidth != mWidth || canvasHeight != mHeight) {
                mWidth = canvasWidth;
                mHeight = canvasHeight;
                Configuration conf = Configuration.get();
                if (mWidth / mHeight >= 4) {
                    conf.setTargetScale((double) mHeight / 150);
                    conf.setStartY(0);
                    conf.setStartX((mWidth - 4 * mHeight) / 2);
                } else {
                    conf.setTargetScale((double) mWidth / 600);
                    conf.setStartY((mHeight - mWidth / 4) / 2);
                    conf.setStartX(0);
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
