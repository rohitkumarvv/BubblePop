package com.game.bubblepop;

import android.content.Context;
import android.graphics.Bitmap;


public class Game {
    public static final int FIXED_TIME_MODE = 1;
    public static final int INFINITE_PLAY_MODE = 2;
    private static Game instance;
    private static final int DEFAULT_BONUS = 5000;
    protected static int MAX_BALLS;
    protected static int num_balls_start;
    protected static int max_speed_increase;
    protected static int min_speed_increase;
    public static final int BUSTER_BALL_TARGETS = 5;

    private int BONUS = 5000;

    protected int level = 1;

    public volatile int numBallsPopped = 0;
    public int numBallsAdded = 0;
    protected int thresholdVelocity = 15;
    protected int thresholdNumBalls = 15;

    public long score;

    public volatile int totalRegularBalls = 0;
    public volatile int buddiBalls = 0;

    private Velocity minBubbleVelocity = VelocityUtils.getDefaultMinVelocity();
    private Velocity maxBubbleVelocity = VelocityUtils.getDefaultMaxVelocity();

    public int chaosLevel = 1;
    public int ballSizeLevel = 1;
    public int speedLevel = 1;
    private static long defaultAreaPixels = 1080 * 1920;
    protected final int game_mode;
    protected static long areaPixels;

    // Bubble image's bitmap
    private Bubbles bubbles;

    private Game(Context context) {
        bubbles = new Bubbles(context);
        minBubbleVelocity = VelocityUtils.getNewVelocity(minBubbleVelocity, areaPixels , defaultAreaPixels);
        maxBubbleVelocity = VelocityUtils.getNewVelocity(maxBubbleVelocity, areaPixels , defaultAreaPixels);

        game_mode = 1;
        MAX_BALLS = context.getResources().getInteger(R.integer.max_balls_count);
        max_speed_increase = context.getResources().getInteger(R.integer.max_increase);
        min_speed_increase = context.getResources().getInteger(R.integer.min_increase);
        num_balls_start = 8;

    }

    public static Game getGame(Context context) {
        //return instance;
        if (instance == null) {
            instance = new Game(context);
        }
        return instance;
    }

    public void resetLevelVariables() {
        level++;
        if (game_mode == INFINITE_PLAY_MODE) {
            score = score + BONUS;
            switch (level % 3) {
                case 1:
                    speedLevel++;
                    break;
                case 0:
                    num_balls_start++;
                    if (num_balls_start > 15) {
                        num_balls_start = 15;
                    }
                    break;
                case 2:
                    thresholdNumBalls++;
                    thresholdVelocity++;
                    break;
            }
            BONUS = BONUS + 1000;
        }

        if (game_mode == FIXED_TIME_MODE) {
            if (level == 6) {
                speedLevel = 3;
                num_balls_start = 8;
                ballSizeLevel = 5;
            } else if (level < 6) {
                speedLevel++;
                num_balls_start++;
            } else {
                ballSizeLevel++;
                if (ballSizeLevel > 15) {
                    ballSizeLevel = 15;
                }
                speedLevel++;
            }
        }
    }

    public void resetGameInstance() {
        numBallsPopped = 0;
        numBallsAdded = 0;
        score = 0;
        BONUS = DEFAULT_BONUS;
        level = 1;
        minBubbleVelocity = VelocityUtils.getDefaultMinVelocity();
        maxBubbleVelocity = VelocityUtils.getDefaultMaxVelocity();
        chaosLevel = 1;
        ballSizeLevel = 1;
        speedLevel = 1;
        num_balls_start = 8;
    }

    public Bubbles getBubbles() {
        return bubbles;
    }

    public Bitmap getAppropriateBitmap(int bubble_type, int mScaledBitmapWidth) {
        return BitMapCache.getCache().getAppropriateBitmap(bubble_type, mScaledBitmapWidth, this);
    }

    public Velocity getMinimumVelocity(){
        return minBubbleVelocity;
    }

    public Velocity getMaximumVelocity(){
        return maxBubbleVelocity;
    }
}
