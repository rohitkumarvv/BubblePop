package com.game.bubblepop;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.game.bubblepop.com.game.bubblepop.bubble.IBubble;

public class CollisionManager implements Runnable {

    private GameActivity gameActivity;
    private RelativeLayout mFrame;
    protected static final String TAG = "game-chaos";


    public CollisionManager(Activity activity) {
        gameActivity = (GameActivity) activity;
        mFrame = gameActivity.mFrame;
    }

    @Override
    public void run() {
        checkAndApplyCollision();
    }

    public void checkAndApplyCollision() {
        int totalBalls = gameActivity.getNumBallsOnScreen();
        for (int i = 0; i < totalBalls; i++) {

            IBubble bubble1 = ((BubbleGUI) mFrame.getChildAt(i)).getBubble();
            if (bubble1 == null) {
                continue;
            }
            for (int j = i + 1; j < totalBalls; j++) {

                IBubble bubble2 = ((BubbleGUI) mFrame.getChildAt(j)).getBubble();
                if (bubble2 == null) {
                    continue;
                }
                double dx = bubble2.getmXPos() + bubble2.getmRadius() - bubble1.getmXPos() - bubble1.getmRadius();
                double dy = bubble2.getmYPos() + bubble2.getmRadius() - bubble1.getmYPos() - bubble1.getmRadius();
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (doBubblesOverlap(dist, bubble1, bubble2)) {
                    double overlap = (bubble1.getmRadius() + bubble2.getmRadius() - dist) / 2;
                    if (bubble1.getmXPos() > bubble2.getmXPos()) {
                        bubble2.setmXPos(bubble2.getmXPos() - overlap, true);
                        bubble1.setmXPos(bubble1.getmXPos() + overlap, true);
                    } else {
                        bubble1.setmXPos(bubble1.getmXPos() - overlap, true);
                        bubble2.setmXPos(bubble2.getmXPos() + overlap, true);
                    }

                    if (bubble1.getmYPos() > bubble2.getmYPos()) {
                        bubble2.setmYPos(bubble2.getmYPos() - overlap, true);
                        bubble1.setmYPos(bubble1.getmYPos() + overlap, true);
                    } else {
                        bubble1.setmYPos(bubble1.getmYPos() - overlap, true);
                        bubble2.setmYPos(bubble2.getmYPos() + overlap, true);
                    }
                    changeDirectionOfCollidingBalls(bubble1, bubble2);
                }
            }
        }
    }

    private void changeDirectionOfCollidingBalls(IBubble bubble1, IBubble bubble2) {
        double sumSquaredRadii = bubble1.getmRadiusSquared() + bubble2.getmRadiusSquared();
        double v1x = (bubble1.getmDx() * (bubble1.getmRadiusSquared() - bubble2.getmRadiusSquared()) + 2 * bubble2.getmRadiusSquared() * bubble2.getmDx()) / sumSquaredRadii;
        double v1y = (bubble1.getmDy() * (bubble1.getmRadiusSquared() - bubble2.getmRadiusSquared()) + 2 * bubble2.getmRadiusSquared() * bubble2.getmDy()) / sumSquaredRadii;
        double v2x = bubble1.getmDx() + v1x - bubble2.getmDx();
        double v2y = bubble1.getmDy() + v1y - bubble2.getmDy();

        bubble1.setmDx(v1x);
        bubble1.setmDy(v1y);
        bubble2.setmDx(v2x);
        bubble2.setmDx(v2y);
    }

    private boolean doBubblesOverlap(double dist, IBubble b1, IBubble b2) {
        return dist < (b1.getmRadius() + b2.getmRadius());
    }
}
