package com.game.bubblepop.com.game.bubblepop.bubble;

import android.graphics.Bitmap;
import android.os.Parcelable;

public interface IBubble extends IMovable, IRotatable , Parcelable {

    int BUBBLE_REGULAR_TYPE = 0;
    int BUBBLE_FRICTION_TYPE = 1;
    int BUBBLE_BUSTER_TYPE = 2;

    double getmRadiusSquared();

    double getmXPos();

    void setmXPos(double mXPos, boolean autoCreated);

    double getmYPos();

    void setmYPos(double mYPos, boolean autoCreated);

    double getmDx();

    void setmDx(double mDx);

    double getmDy();

    void setmDy(double mDy);

    double getmRadius();

    void setmScaledBitmapWidth();

    boolean checkWallCollisions() ;

    void setSidesFalse();

    void setSpeedAndDirection() ;

    // Returns true if the BubbleView intersects position (x,y)
    boolean intersects(float x, float y) ;

    void move();

    Bitmap createScaledBitmap();

    double getCenterX();

    double getCenterY();

}
