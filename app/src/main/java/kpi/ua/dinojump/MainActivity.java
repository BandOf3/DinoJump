package kpi.ua.dinojump;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_button :
                startActivity(new Intent(MainActivity.this, GameActivity.class));
                break;
            case R.id.score_button:
                Toast.makeText(this, getHighScoreMessage(), Toast.LENGTH_LONG).show();

        }
    }

    private String getHighScoreMessage() {
        long score = getBaseContext().getSharedPreferences("HighScore", MODE_PRIVATE).getLong("score", 0);
        return (score > 0) ?  "Current high score is " + score : "Nobody has been played yet";
    }
}
