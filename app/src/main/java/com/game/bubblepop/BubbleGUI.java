package com.game.bubblepop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.RelativeLayout;

import com.game.bubblepop.com.game.bubblepop.bubble.Bubble;
import com.game.bubblepop.com.game.bubblepop.bubble.BubbleFactory;
import com.game.bubblepop.com.game.bubblepop.bubble.DefaultBubble;
import com.game.bubblepop.com.game.bubblepop.bubble.SpecialBubble;
import com.game.bubblepop.com.game.bubblepop.bubble.IBubble;
import com.game.bubblepop.com.game.bubblepop.bubble.IRotatable;
import com.game.bubblepop.com.game.bubblepop.bubble.RotationUtils;

// View which displays a bubble.
// This class handles anim, draw, and pop amongst other actions.
// A new BubbleView is created for each bubble on the display

public class BubbleGUI extends View implements Parcelable {

    //private static final int REFRESH_RATE = 17;
    private final Paint mPainter = new Paint();
    protected int bubbleType;
    private Bitmap mScaledBitmap;
    private RelativeLayout mFrame;
    private IBubble bubble;

    private Game game;
    GameActivity activity;
    protected static final String TAG = "game-chaos";


    BubbleGUI(Context context, BubbleGUI prevView) {
        super(context);
        this.bubbleType = prevView.bubbleType;
        this.bubble = prevView.bubble;
        activity = (GameActivity) context;
        game = Game.getGame(activity);
        mFrame = (RelativeLayout) activity.findViewById(com.game.bubblepop.R.id.frame);
        mScaledBitmap = bubble.createScaledBitmap();
    }


    BubbleGUI(Context context, float x, float y, boolean autoCreated, int bubbleType) {
        super(context);
        this.bubbleType = bubbleType;
        long startMillies = System.currentTimeMillis();
        game = Game.getGame(activity);
        bubble = BubbleFactory.getBubbleFactory().createBubble(bubbleType, game);

        activity = (GameActivity) context;
        mFrame = (RelativeLayout) activity.findViewById(com.game.bubblepop.R.id.frame);

        bubble.setmScaledBitmapWidth();
        mScaledBitmap = bubble.createScaledBitmap();

        bubble.setmXPos(x, autoCreated);
        bubble.setmYPos(y, autoCreated);


        // Set the BubbleView's speed and direction
        bubble.setSpeedAndDirection();

        // Set the BubbleView's rotation
        setRotation();

        mPainter.setAntiAlias(true);

    }

    private void setRotation() {
        bubble.setStartRotation(IRotatable.ROTATION_INCREMENTS);
    }

    public IBubble getBubble() {
        return bubble;
    }


    //public void pause(){
    //if(null!=mMoverFuture){
    //    mMoverFuture.cancel(true);
    //}
    //}


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    // Cancel the Bubble's movement
    // Remove Bubble from mFrame
    // Play pop sound if the BubbleView was popped

    protected synchronized void stopMovement(final boolean wasPopped) {
        //Log.i(Game.TAG, "entered stopMovement");
        if (isOnUIThread()) {
            bubble.stopMovement(wasPopped);
            mFrame.removeView(this);

            if (game.totalRegularBalls == 0) {
                activity.restartgame();
            }
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopMovement(wasPopped);
                }
            });
        }
    }

    private boolean isOnUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    // Draw the Bubble at its current location
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        RotationUtils.setNewRotation(bubble);
        canvas.rotate(bubble.getCurrentRotation(), (float) bubble.getCenterX(), (float) bubble.getCenterY());
        canvas.drawBitmap(mScaledBitmap, (float) bubble.getmXPos(), (float) bubble.getmYPos(), mPainter);
        canvas.restore();
    }


/*    @Override
    public Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("bubbleType", this.bubbleType);
        bundle.putInt("mScaledBitmapWidth", this.mScaledBitmapWidth);
        bundle.putDouble("mXPos", this.mXPos);
        bundle.putDouble("mYPos", this.mYPos);
        bundle.putDouble("mDx", this.mDx);
        bundle.putDouble("mDy", this.mDy);
        bundle.putDouble("mRadius", this.mRadius);
        bundle.putDouble("mRadiusSquared", this.mRadiusSquared);
        bundle.putDouble("mRotate", this.mRotate);
        bundle.putLong("mDRotate", this.mDRotate);

        bundle.putDouble("startMillies", this.startMillies);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.bubbleType = bundle.getInt("bubbleType");
            this.mScaledBitmapWidth = bundle.getInt("mScaledBitmapWidth");
            this.mXPos = bundle.getDouble("mXPos");
            this.mYPos = bundle.getDouble("mYPos");
            this.mDRotate = bundle.getLong("mDRotate");
            this.mDx = bundle.getDouble("mDx");
            this.mDy = bundle.getDouble("mDy");
            this.mRadius = bundle.getDouble("mRadius");
            this.mRadiusSquared = bundle.getDouble("mRadiusSquared");

            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }*/


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.bubbleType);
        parcel.writeParcelable(bubble, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
    }

    BubbleGUI(Context context, Parcel in) {
        super(context);
        this.bubbleType = in.readInt();
        switch (this.bubbleType) {
            case Bubble.BUBBLE_FRICTION_TYPE:
                this.bubble = in.readParcelable(SpecialBubble.class.getClassLoader());
                break;
            case Bubble.BUBBLE_REGULAR_TYPE:
                this.bubble = in.readParcelable(DefaultBubble.class.getClassLoader());
                break;
        }
    }

    public static final Parcelable.Creator<BubbleGUI> CREATOR = new Parcelable.Creator<BubbleGUI>() {
        public BubbleGUI createFromParcel(Parcel in) {
            return new BubbleGUI(null, in);
        }

        public BubbleGUI[] newArray(int size) {
            return new BubbleGUI[size];
        }
    };

}
