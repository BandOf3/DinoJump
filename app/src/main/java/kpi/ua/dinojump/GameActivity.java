package kpi.ua.dinojump;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import kpi.ua.dinojump.entities.BaseEntity;
import kpi.ua.dinojump.view.GameView;
import static kpi.ua.dinojump.Runner.BaseBitmap;

public class GameActivity extends AppCompatActivity {

    private GameView mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(this);
    }

    public void init(Context context) {
        mGameView = new GameView(context);
        BaseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sprite100);
        BaseEntity.Scale = (double) BaseBitmap.getWidth() / BaseEntity.BaseWidth;
        setContentView(mGameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameView.Stop();
    }

    //running the game when activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        mGameView.resume();
    }
}