package com.game.bubblepop.com.game.bubblepop.bubble;

import android.graphics.Bitmap;
import android.os.Parcel;

import com.game.bubblepop.Game;
import com.game.bubblepop.GameActivity;

import java.util.Random;

public abstract class Bubble  implements IBubble{
    private static Random r = new Random();
    public static final int BITMAP_SIZE = 50;
    private volatile double mXPos;
    private volatile double mYPos;
    volatile double mDx;
    volatile double mDy;
    double mRadius;
    double mRadiusSquared;
    int mScaledBitmapWidth;
    private long mRotate;
    private long mDRotate;
    private volatile boolean left,right,top,bottom;
    Game gameInstance;

    public Bubble(Game game){
        this.gameInstance = game;
    }



    public long getStartRotation() {
        return mDRotate;
    }

    public void setStartRotation(long startRotation) {
        this.mDRotate = startRotation;
    }

    public long getCurrentRotation() {
        return mRotate;
    }

    public void setCurrentRotation(long rotation) {
        this.mRotate = rotation;
    }

    public double getmRadiusSquared() {
        return mRadiusSquared;
    }

    public void setmRadiusSquared(double mRadiusSquared) {
        this.mRadiusSquared = mRadiusSquared;
    }

    public double getmXPos() {
        return mXPos;
    }

    public void setmXPos(double mXPos, boolean autoCreated) {
        // Adjust position to center the bubble under user's finger
        if(autoCreated){
            this.mXPos = mXPos;
        } else{
            this.mXPos = mXPos - mRadius;
        }
    }

    public double getmYPos() {
        return mYPos;
    }

    public void setmYPos(double mYPos, boolean autoCreated) {
        // Adjust position to center the bubble under user's finger
        if(autoCreated){
            this.mYPos = mYPos;
        } else{
            this.mYPos = mYPos - mRadius;
        }
    }

    public double getmDx() {
        return mDx;
    }

    public void setmDx(double mDx) {
        this.mDx = mDx;
    }

    public double getmDy() {
        return mDy;
    }

    public void setmDy(double mDy) {
        this.mDy = mDy;
    }

    public double getmRadius() {
        return mRadius;
    }

    public void setmRadius(double mRadius) {
        this.mRadius = mRadius;
    }

    public int getmScaledBitmapWidth() {
        return mScaledBitmapWidth;
    }

    public abstract void setmScaledBitmapWidth();

    public boolean checkWallCollisions() {
        setSidesFalse();
        if (mXPos < 0){
            mXPos = 0;
            left = true;
            return false;
        }

        if( mXPos > GameActivity.mDisplayWidth - mScaledBitmapWidth) {
            mXPos =  GameActivity.mDisplayWidth - mScaledBitmapWidth;
            right = true;
            return false;
        }

        if(mYPos < GameActivity.in_game_score_view_height) {
            mYPos = GameActivity.in_game_score_view_height;
            top = true;
            return false;
        }

        if(mYPos > GameActivity.mDisplayHeight - mScaledBitmapWidth) {
            mYPos =  GameActivity.mDisplayHeight - mScaledBitmapWidth;
            bottom = true;
            return false;
        }
        return true;
    }

    public void setSidesFalse(){
        left = false;
        right = false;
        top = false;
        bottom = false;
    }

    public void setSpeedAndDirection() {
        if(left || right) {
            mDx = -mDx;
        }

        else if(top || bottom){
            mDy = -mDy;
        }

        else{
            double adjustedSpeed = gameInstance.getMinimumVelocity().dy + gameInstance.speedLevel*0.35;
            mDy = (r.nextInt((int) adjustedSpeed) + adjustedSpeed)*(r.nextBoolean()?1:-1);
            mDx = (r.nextInt((int) adjustedSpeed) + adjustedSpeed)*(r.nextBoolean()?1:-1);
        }

    }

    // Returns true if the BubbleView intersects position (x,y)
    public boolean intersects(float x, float y) {
        if ((mXPos <= x && x <= mScaledBitmapWidth + mXPos ) &&
                (mYPos <= y && y <= mScaledBitmapWidth + mYPos))
            return true;
        return  false;
    }

    public void move(){
        mXPos += mDx;
        mYPos += mDy;
        if(!checkWallCollisions()) {
            setSpeedAndDirection();
        }
    }

    public abstract Bitmap createScaledBitmap();

    public Bubble(Parcel in){
        this.mScaledBitmapWidth = in.readInt();
        this.mXPos = in.readDouble();
        this.mYPos = in.readDouble();
        this.mDx = in.readDouble();
        this.mDy = in.readDouble();
        this.mRadius = in.readDouble();
        this.mRadiusSquared = in.readDouble();
        this.mRotate = in.readLong();
        this.mDRotate = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        //parcel.writeInt(this.bubbleType);
        parcel.writeInt(this.mScaledBitmapWidth);
        parcel.writeDouble(this.mXPos);
        parcel.writeDouble(this.mYPos);
        parcel.writeDouble(this.mDx);
        parcel.writeDouble(this.mDy);
        parcel.writeDouble(this.mRadius);
        parcel.writeDouble(this.mRadiusSquared);
        parcel.writeLong(this.mRotate);
        parcel.writeLong(this.mDRotate);
    }

    public double getCenterX() {
        return (getmXPos() + getmRadius());
    }

    public double getCenterY() {
        return (getmYPos() + getmRadius());
    }

}
