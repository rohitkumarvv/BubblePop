package com.game.bubblepop;

import android.app.Activity;
import android.widget.RelativeLayout;

public class BubbleMoveManager implements Runnable {

    private GameActivity gameActivity;
    private RelativeLayout mFrame;

    public BubbleMoveManager(Activity activity){
        gameActivity = (GameActivity) activity;
        mFrame = gameActivity.mFrame;
    }

    @Override
    public void run() {
        moveBalls();
    }

    public void moveBalls(){
        int totalBalls = gameActivity.getNumBallsOnScreen();
        for (int i = 0; i < totalBalls; i++) {
            BubbleGUI bubbleView = (BubbleGUI) mFrame.getChildAt(i);
            if(bubbleView==null){
                continue;
            }
            bubbleView.getBubble().move();
            bubbleView.postInvalidate();
        }
    }
}
