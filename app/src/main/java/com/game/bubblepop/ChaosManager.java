package com.game.bubblepop;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.game.bubblepop.com.game.bubblepop.bubble.Bubble;
import com.game.bubblepop.com.game.bubblepop.bubble.IBubble;

import java.util.Random;


public class ChaosManager implements Runnable {

    private GameActivity gameActivity;
    int sinceLastSpecialBall;
    private RelativeLayout mFrame;
    private Game game;
    Random randomX = new Random();
    Random randomY = new Random();
    Random r = new Random();
    Velocity maxVelocity = new Velocity(1, 1);
    protected static final String TAG = "game-chaos";


    public ChaosManager(Activity activity) {
        gameActivity = (GameActivity) activity;
        mFrame = gameActivity.mFrame;
        game = Game.getGame(gameActivity);
    }

    @Override
    public void run() {
        insertChaos();
    }

    public void insertChaos() {
        if (game.totalRegularBalls >= game.MAX_BALLS) {
            gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameActivity.gameOver();
                }
            });
            return;
        }
        resetMaxVelocity();
        int draw = r.nextInt(100);

        final BubbleGUI chaoticBall;
        int chaosX = randomX.nextInt(gameActivity.mDisplayWidth);
        int chaosY = randomY.nextInt(gameActivity.mDisplayHeight - gameActivity.in_game_score_view_height)
                + gameActivity.in_game_score_view_height;

        if (draw <= 40
                && gameActivity.getNumBallsOnScreen() > game.thresholdNumBalls
                && maxVelocity.absoluteVelocity() > game.thresholdVelocity && sinceLastSpecialBall > 2) {
            sinceLastSpecialBall = 0;
            if (draw < 25 || game.buddiBalls < 15) {
                chaoticBall = new BubbleGUI(mFrame.getContext(),
                        chaosX,
                        chaosY, true, Bubble.BUBBLE_FRICTION_TYPE);
            } else {
                chaoticBall = new BubbleGUI(mFrame.getContext(),
                        chaosX,
                        chaosY, true, Bubble.BUBBLE_BUSTER_TYPE);
            }
        } else {
            sinceLastSpecialBall++;
            if (r.nextBoolean()) {
                chaoticBall = new BubbleGUI(mFrame.getContext(),
                        chaosX, gameActivity.in_game_score_view_height, true, Bubble.BUBBLE_REGULAR_TYPE);
            } else {
                chaoticBall = new BubbleGUI(mFrame.getContext(), 0,
                        chaosY, true, Bubble.BUBBLE_REGULAR_TYPE);
            }
        }

        IBubble chaoticBubble = chaoticBall.getBubble();

        //x == 1 smallest ball, 2 medium, 3 largest
        double x = (chaoticBubble.getmRadius() / Bubble.BITMAP_SIZE) * 2 - 1;

        chaoticBubble.setmDx(maxVelocity.dx * (randomX.nextInt(game.max_speed_increase - game.min_speed_increase)
                + game.min_speed_increase) / (100 * x));
        chaoticBubble.setmDy(maxVelocity.dy * (randomY.nextInt(game.max_speed_increase - game.min_speed_increase)
                + game.min_speed_increase) / (100 * x));
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameActivity.insertNewBall(chaoticBall);
            }
        });
    }

    private void resetMaxVelocity() {
        Velocity maxVel = new Velocity(0, 0);
        int totalChildren = gameActivity.getNumBallsOnScreen();
        for (int i = 0; i < totalChildren; i++) {
            BubbleGUI b = (BubbleGUI) mFrame.getChildAt(i);
            Velocity v = new Velocity(b.getBubble().getmDx(), b.getBubble().getmDy());
            if (maxVel.absoluteVelocity() < v.absoluteVelocity()) {
                maxVel = v;
            }
        }
        if (maxVel.absoluteVelocity() > game.getMaximumVelocity().absoluteVelocity()) {
            maxVelocity = game.getMaximumVelocity();
        } else if (maxVel.absoluteVelocity() < game.getMinimumVelocity().absoluteVelocity()) {
            maxVelocity = game.getMinimumVelocity();
        } else {
            maxVelocity = maxVel;
        }
    }

}
