package com.game.bubblepop.com.game.bubblepop.bubble;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.game.bubblepop.Game;
import com.game.bubblepop.GameActivity;

import java.util.Random;

public class DefaultBubble extends Bubble {

    private static Random r = new Random();

    public DefaultBubble(Game game) {
        super(game);
    }

    @Override
    public void setmScaledBitmapWidth() {
        int randomNumber = r.nextInt(30);
        int level = gameInstance.ballSizeLevel;
        //Log.i("bisha","level: "+level +", randomNumber: "+randomNumber);
        if (randomNumber < level) {
            mScaledBitmapWidth = Bubble.BITMAP_SIZE * 2;
        } else if (randomNumber >= level && randomNumber < 10 + level) {
            mScaledBitmapWidth = Bubble.BITMAP_SIZE * 3;
        } else {
            mScaledBitmapWidth = Bubble.BITMAP_SIZE * 4;
        }
        mRadius = mScaledBitmapWidth / 2;
        mRadiusSquared = mRadius * mRadius;
    }

    @Override
    public Bitmap createScaledBitmap() {
        return gameInstance.getAppropriateBitmap(Bubble.BUBBLE_REGULAR_TYPE, mScaledBitmapWidth);
    }

    @Override
    public void stopMovement(boolean wasPopped) {
        if (mRadius == BITMAP_SIZE) {
            super.gameInstance.buddiBalls--;
        }
        super.gameInstance.totalRegularBalls--;

        if (wasPopped) {
            gameInstance.numBallsPopped++;
            GameActivity.mSoundPool.play(GameActivity.mSoundID,
                    GameActivity.mStreamVolume,
                    GameActivity.mStreamVolume, 1, 0, 1.0f);
            gameInstance.score += 100;
        }
    }

    public DefaultBubble(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Bubble createFromParcel(Parcel in) {
            return new DefaultBubble(in);
        }

        public Bubble[] newArray(int size) {
            return new Bubble[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
