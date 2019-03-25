package com.game.bubblepop.com.game.bubblepop.bubble;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.game.bubblepop.Game;

public class SpecialBubble extends Bubble {

    private long startMillies;
    public static final int maxLifeTimeFrictionBall = 10000;
    private static final double friction = (float) 0.75;


    public SpecialBubble(Game game) {
        super(game);
        startMillies = System.currentTimeMillis();
        setmScaledBitmapWidth();

    }

    @Override
    public void setmScaledBitmapWidth() {
        mScaledBitmapWidth = Bubble.BITMAP_SIZE * 3;
        mRadius = mScaledBitmapWidth / 2;
        mRadiusSquared = mRadius * mRadius;
    }

    @Override
    public Bitmap createScaledBitmap() {
        return gameInstance.getAppropriateBitmap(Bubble.BUBBLE_FRICTION_TYPE, mScaledBitmapWidth);

    }

    @Override
    public void stopMovement(boolean wasPopped) {

    }

    @Override
    public void move() {
        if (System.currentTimeMillis() - startMillies < maxLifeTimeFrictionBall) {
            mDx -= friction * mDx / (Math.abs(mDx) + Math.abs(mDy));
            mDy -= friction * mDy / (Math.abs(mDx) + Math.abs(mDy));
        } else {
            stopMovement(false);
        }

        super.move();
    }


    public SpecialBubble(Parcel in) {
        super(in);
        this.startMillies = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(startMillies);
        super.writeToParcel(parcel, flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Bubble createFromParcel(Parcel in) {
            return new SpecialBubble(in);
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
