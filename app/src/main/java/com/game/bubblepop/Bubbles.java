package com.game.bubblepop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Bubbles {

    private final Bitmap mOrangeBitmap;
    private final Bitmap mGreenBitmap;
    private final Bitmap mPurpleBitmap;

    Bubbles(Context context){
        mOrangeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.orange_ball);
        mGreenBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_ball);
        mPurpleBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.violet_ball);
    }

    public Bitmap getOrangeBubble() {
        return mOrangeBitmap;
    }

    public Bitmap getGreenBubble() {
        return mGreenBitmap;
    }

    public Bitmap getPurpleBubble() {
        return mPurpleBitmap;
    }
}
