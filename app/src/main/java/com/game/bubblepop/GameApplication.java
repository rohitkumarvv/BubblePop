package com.game.bubblepop;

import android.app.Application;

import org.acra.ACRA;

public class GameApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // report application crash
        ACRA.init(this);
    }
}
