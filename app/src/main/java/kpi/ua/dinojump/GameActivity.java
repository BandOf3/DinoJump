package kpi.ua.dinojump;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import kpi.ua.dinojump.core.GameLogic;
import kpi.ua.dinojump.core.GameRunnable;
import kpi.ua.dinojump.model.BaseEntity;
import kpi.ua.dinojump.view.GameView;

public class GameActivity extends AppCompatActivity {

    private GameView mGameView;
    private GameLogic mGameLogic;
    private GameRunnable mGameRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        Bitmap baseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dino_sprite);
        Configuration conf = Configuration.get();
        conf.setBaseBitmap(baseBitmap);
        conf.setBitmapScale((double) baseBitmap.getWidth() / BaseEntity.BASE_WIDTH);

        mGameView = new GameView(this, baseBitmap);
        setContentView(mGameView);

        mGameLogic = mGameView.getGameLogic();
        Handler handler = new Handler(Looper.getMainLooper());
        mGameRunnable = new GameRunnable(mGameLogic, mGameView, handler, 60);

        mGameRunnable.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameLogic.pause();
    }

    //running the game when activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        mGameLogic.resume();
    }
}