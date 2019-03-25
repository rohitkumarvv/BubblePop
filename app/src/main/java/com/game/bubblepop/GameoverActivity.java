package com.game.bubblepop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameoverActivity extends Activity {
    
    private String numballspoppedkey;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(com.game.bubblepop.R.layout.gameover);
        Game game = Game.getGame(this);

        numballspoppedkey = getResources().getString(com.game.bubblepop.R.string.num_balls_popped);
        Button try_again_button = (Button) findViewById(com.game.bubblepop.R.id.try_again);
        int numBallsPopped = game.numBallsPopped;
        int numBallsAdded = game.numBallsAdded;
        int numSecondsPlayed = Math.round((int) getIntent().getLongExtra("numMilliesPlayed", 0) / 1000);
        long score = game.score;

        TextView ballsPoppedView = (TextView) findViewById(com.game.bubblepop.R.id.balls_popped);
        ballsPoppedView.setText(String.valueOf(numBallsPopped));

        TextView ballsAddedView = (TextView) findViewById(com.game.bubblepop.R.id.balls_added);
        ballsAddedView.setText(String.valueOf(numBallsAdded));

        TextView timePlayedView = (TextView) findViewById(com.game.bubblepop.R.id.total_time_played);
        timePlayedView.setText(String.valueOf(numSecondsPlayed));


        TextView totalScoreView = (TextView) findViewById(com.game.bubblepop.R.id.total_score);
        totalScoreView.setText(String.valueOf(score));

        try_again_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (Game.getGame(this) != null) {
            Game.getGame(this).resetGameInstance();
        }
        Intent intent = new Intent(this, StartGameActivity.class);
        this.startActivity(intent);
    }

    private void restartGame() {
        if (Game.getGame(this) != null) {
            Game.getGame(this).resetGameInstance();
        }
        Intent intent = new Intent(this, GameActivity.class);
        this.startActivity(intent);
    }

}
