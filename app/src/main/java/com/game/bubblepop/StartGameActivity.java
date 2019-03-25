package com.game.bubblepop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.game.bubblepop.com.game.bubblepop.bubble.Bubble;

public class StartGameActivity  extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startgame);
        ImageView start_game_button = (ImageView) findViewById(R.id.start_game_button);
        Bitmap start_game_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.homescreen_burstball);
        Bitmap scaled_start_game_bitmap = Bitmap.createScaledBitmap(start_game_bitmap, Bubble.BITMAP_SIZE*4,Bubble.BITMAP_SIZE*4,false);
        start_game_button.setImageBitmap(scaled_start_game_bitmap);

        ImageView more_button = (ImageView) findViewById(R.id.more_button);
        Bitmap more_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.homescreen_moreball);
        Bitmap scaled_more_bitmap = Bitmap.createScaledBitmap(more_bitmap, Bubble.BITMAP_SIZE*4,Bubble.BITMAP_SIZE*4,false);
        more_button.setImageBitmap(scaled_more_bitmap);

        ImageView settings_button = (ImageView) findViewById(R.id.settings_button);
        Bitmap settings_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.homescreen_settingsball);
        Bitmap scaled_settings_bitmap = Bitmap.createScaledBitmap(settings_bitmap,Bubble.BITMAP_SIZE*4,Bubble.BITMAP_SIZE*4,false);
        settings_button.setImageBitmap(scaled_settings_bitmap);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDisplayHeight = metrics.heightPixels;
        int mDisplayWidth = metrics.widthPixels;
        Game.areaPixels = mDisplayHeight*mDisplayWidth;

        start_game_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartGameActivity.this, GameActivity.class);
                StartGameActivity.this.startActivity(intent);
            }
        });
    }
}
