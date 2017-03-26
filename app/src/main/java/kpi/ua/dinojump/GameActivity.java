package kpi.ua.dinojump;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import kpi.ua.dinojump.core.GameLogic;
import kpi.ua.dinojump.core.GameRunnable;
import kpi.ua.dinojump.model.BaseEntity;
import kpi.ua.dinojump.view.GameView;

import static kpi.ua.dinojump.Runner.BaseBitmap;

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
        mGameView = new GameView(this);
        setContentView(mGameView);

        mGameLogic = mGameView.getGameLogic();
        Handler handler = new Handler(Looper.getMainLooper());
        mGameRunnable = new GameRunnable(mGameLogic, mGameView, handler, 60);

        BaseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dino_sprite);
        BaseEntity.Scale = (double) BaseBitmap.getWidth() / BaseEntity.BaseWidth;

        mGameRunnable.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameView.pause();
    }

    //running the game when activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        mGameView.resume();
    }
}