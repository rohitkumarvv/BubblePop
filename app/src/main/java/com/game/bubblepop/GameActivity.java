package com.game.bubblepop;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.game.bubblepop.com.game.bubblepop.bubble.Bubble;
import com.game.bubblepop.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameActivity extends Activity implements Parcelable {

	// These variables are for testing purposes, do not modify
    public static int in_game_score_view_height;
    public static int mDisplayWidth, mDisplayHeight;

    protected static final String TAG = "game-chaos";

    // Sound variables
    // AudioManager
    private static AudioManager mAudioManager;
    // SoundPool
    public static SoundPool mSoundPool;
    // ID for the bubble popping sound
    public static int mSoundID;
    // Audio volume
    public static float mStreamVolume;



    private ArrayList<BubbleGUI> views;

	// The views
	protected RelativeLayout mFrame;
    private RelativeLayout mHeader;
    private TextView scoreView;
    private TextView ballsView;
    private TextView ballsPoppedView;
    private TextView levelTextView;
    private TextView pausePlayView;
    private TextView countDownView;

    //private InterstitialAd mInterstitialAd;
    private boolean areThreadsRunning;

    private Timer timer;



    private long mStartMillis;
    private long mEndMillis;
    //protected long score;
    //protected int level = 1;
    private volatile Velocity maxVelocity = new Velocity(1,1);

    private ScheduledExecutorService chaosExecutor;
    private ScheduledExecutorService collisionExecutor;
    private ScheduledExecutorService bubbleMoverService;

    private Runnable chaosCreator;
    private Runnable collisionDetector;
    private Runnable bubbleMover;

    Random r1 = new Random();
    int sinceLastFrictionBall = 0;
    private long gamePlaytimeinmillies;
    private int levelBallsPopped;
    private int levelBallsAdded;

    private Game game;
    private boolean isPaused;

    public GameActivity(){

    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
            setContentView(R.layout.main);
            game = Game.getGame(this);

            // Set up user interface
            mFrame = (RelativeLayout) findViewById(R.id.frame);
            mHeader = (RelativeLayout) findViewById(R.id.in_game_score_header);

            // Load bubble Bitmaps
            chaosCreator = new ChaosManager(this);
            collisionDetector = new CollisionManager(this);
            bubbleMover = new BubbleMoveManager(this);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mDisplayHeight = metrics.heightPixels;
            mDisplayWidth = metrics.widthPixels;
            loadAndSetViews();
            //setUpInterstitial();
            if(savedInstanceState==null) {
                startGame();
                switch (game.game_mode) {
                    case Game.INFINITE_PLAY_MODE:
                        timer = new Timer(15000, 1000, true, this);
                        break;
                    case Game.FIXED_TIME_MODE:
                        timer = new Timer(60000, 1000, true, this);
                        break;
                }
                timer.create();
            }
        Log.i(TAG, "OnCreate");

    }

    private void loadAndSetViews(){
        scoreView = (TextView) findViewById(R.id.in_game_score_view);
        ballsView = (TextView) findViewById(R.id.balls_remaining_header);
        ballsPoppedView = (TextView) findViewById(R.id.balls_popped);
        levelTextView = (TextView) findViewById(R.id.level_text_view);
        levelTextView.setText("Level " + game.level);
        countDownView = (TextView) findViewById(R.id.countdown_view);
        pausePlayView = (TextView) findViewById(R.id.pause_view);
        pausePlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPaused) {
                    resumeGame();
                } else {
                    pauseGame();
                }
            }
        });
    }

    private void setViews(){
        updateScoreView();
        levelTextView.setText("Level " + game.level);
        if(isPaused){
            pausePlayView.setText("Play");
        } else{
            pausePlayView.setText("Pause");
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        mFrame.removeAllViews();
        GameActivity c = savedInstanceState.getParcelable("STATE");
        views = savedInstanceState.getParcelableArrayList("VIEWS_STATE");
        views = convertViews(views);
        mStartMillis = c.mStartMillis;
        mEndMillis = c.mEndMillis;
        maxVelocity = c.maxVelocity;
        sinceLastFrictionBall = c.sinceLastFrictionBall;
        gamePlaytimeinmillies = c.gamePlaytimeinmillies;
        isPaused = c.isPaused;
        timer = new Timer(savedInstanceState.getLong("timeleft"),60000, 1000, true, this).create();
        if(isPaused){
            timer.pause();
        }
        Log.i(TAG, "onrestoreInstancestate");

    }

    private ArrayList<BubbleGUI> convertViews(List<BubbleGUI> oldViews){
        ArrayList<BubbleGUI> newList = new ArrayList<>();
        for(BubbleGUI oldView:oldViews){
            BubbleGUI newView = new BubbleGUI(this,oldView);
            newList.add(newView);
        }
        oldViews.clear();
        return newList;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onsaveInstanceState");
        saveGameState();
        savedInstanceState.putParcelableArrayList("VIEWS_STATE", views);
        savedInstanceState.putParcelable("STATE", this);
        savedInstanceState.putLong("timeleft",timer.timeLeft());
    }

    private void pauseGame(){
        shutdownThreadServices();
        isPaused = true;
        timer.pause();
        saveGameState();
        pausePlayView.setText("Play");
    }

    private void resumeGame(){
        Log.i(TAG, "resumeGame");
        resumeGameState();
        //views.clear();
        isPaused = false;
        pausePlayView.setText("Pause");
    }

    //Runs on main thread onPause event
    private void saveGameState(){
        views.clear();
        int i = 0;
        while(i<mFrame.getChildCount()){
            BubbleGUI b = (BubbleGUI) mFrame.getChildAt(i);
            //b.pause();
            views.add(b);
            i++;
        }
        //mFrame.removeAllViews();
    }


    //Runs on main thread onResume event
    private void resumeGameState(){
        Log.i(TAG, "resumeGameState");
        //int i = 0;
        timer.resume();
        if(views==null || views.isEmpty()){
            return;
        }
        game.totalRegularBalls = 0;
        mFrame.removeAllViews();
        for(BubbleGUI b:views){
            insertNewBall(b);
        }
        views.clear();
        startThreadServices();
    }

    //run on UI thread
    private void startGame(){
        views = new ArrayList<>(20);
        Random r1 = new Random();
        for(int i = 0;i<game.num_balls_start;i++){
            BubbleGUI newBubble = new BubbleGUI(mFrame.getContext(),
                    r1.nextInt(mDisplayWidth),
                    r1.nextInt(mDisplayHeight),
                    true,
                    Bubble.BUBBLE_REGULAR_TYPE);
            views.add(newBubble);
        }
    }

    //run on UI thread
    protected void gameOver(){
        mEndMillis = System.currentTimeMillis();
        shutdownThreadServices();
        mFrame.removeCallbacks(chaosCreator);
        mFrame.removeAllViews();
        Intent intent = new Intent(this, GameoverActivity.class);
        intent.putExtra("numMilliesPlayed", mEndMillis - mStartMillis + gamePlaytimeinmillies);
        startActivity(intent);
        timer.cancel();
    }

	@Override
	protected void onResume() {
        super.onResume();
        Log.i(TAG, "OnResume");
        resumeGameState();
        setCountDownView(String.valueOf(timer.timeLeft()/1000));
        if(isPaused){
            pauseGame();
        }

        setViews();

        mStartMillis = System.currentTimeMillis();
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		mStreamVolume = (float) mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC)
				/ mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		// TO DO - make a new SoundPool, allowing up to 10 streams
		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                if (0 == status) {
                    //setupGestureDetector();
                } else {
                    Log.i(TAG, "Unable to load sound");
                    finish();
                }
            }
        });

        mSoundID = mSoundPool.load(this, R.raw.bubble_pop, 1);


	}

    @Override
    public void onStart(){
        super.onStart();
        Log.i(TAG, "start method");
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
            in_game_score_view_height = mHeader.getHeight();
			mDisplayWidth = mFrame.getWidth();
			mDisplayHeight = mFrame.getHeight();
            Log.i(TAG,"mDisplayWidth: "+mDisplayWidth+" , mDisplayHeight:"+mDisplayHeight);
		}
	}

    public void restartgame(){
        if(game.game_mode==Game.FIXED_TIME_MODE) {
            game.score+=timer.timeLeft()/10;
        }
        if(levelBallsAdded==0){
            game.score+=5000;
        }
        updateScoreView();
        timer.restartCounter();
        game.resetLevelVariables();
        Random r1 = new Random();
        for(int i = 0;i<game.num_balls_start;i++){
            insertNewBall(
                    r1.nextInt(mDisplayWidth),
                    r1.nextInt(mDisplayHeight),
                    true,
                    Bubble.BUBBLE_REGULAR_TYPE);
        }
        levelTextView.setText("Level " + game.level);
        levelBallsAdded = 0;
        levelBallsPopped = 0;
    }

    private void insertNewBall(float x, float y, boolean autoCreated, int bubbleType){
        if(game.totalRegularBalls>=Game.MAX_BALLS){
            gameOver();
            return;
        }
        BubbleGUI newBubble = new BubbleGUI(mFrame.getContext(), x, y, autoCreated, bubbleType);
        insertNewBall(newBubble);
    }

    protected void insertNewBall(final BubbleGUI newBubble){
        if(isOnUIThread()) {
            mFrame.addView(newBubble);
            if (newBubble.bubbleType == Bubble.BUBBLE_REGULAR_TYPE) {
                game.totalRegularBalls++;
                updateScoreView();
                //ballsView.setText(max_balls-totalRegularBalls);
                if (newBubble.getBubble().getmRadius() == Bubble.BITMAP_SIZE) {
                    game.buddiBalls++;
                }
            }
            //newBubble.startMovement();
        } else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    insertNewBall(newBubble);
                }
            });
        }
    }

    private boolean isOnUIThread(){
        return Looper.myLooper() == Looper.getMainLooper();
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()== MotionEvent.ACTION_DOWN) {

            if(isPaused || event.getRawY()<in_game_score_view_height){
                return false;
            }
            boolean intersects = false;
            int totalChildren = getNumBallsOnScreen();
            for (int i = 0; i < totalChildren; i++) {
                BubbleGUI bubbleView = (BubbleGUI) mFrame.getChildAt(i);

                if (bubbleView.getBubble().intersects(event.getRawX(), event.getRawY())) {
                    levelBallsPopped++;
                    intersects = true;
                    bubbleView.stopMovement(true);
                    if(game.game_mode==Game.INFINITE_PLAY_MODE) {
                        timer.restartCounter();
                    }
                    if(bubbleView.bubbleType == Bubble.BUBBLE_BUSTER_TYPE){
                        int bustedBalls = 0;
                        while(bustedBalls<Game.BUSTER_BALL_TARGETS && game.buddiBalls>0){
                            if(i>getNumBallsOnScreen()){
                                i=0;
                            }
                            if(game.buddiBalls==0){
                                break;
                            }
                            BubbleGUI b = (BubbleGUI) mFrame.getChildAt(i);
                            if(b==null){
                                i++;
                                continue;
                            }
                            if(b.getBubble().getmRadius() == Bubble.BITMAP_SIZE){
                                bustedBalls++;
                                boolean x = game.totalRegularBalls==1;
                                b.stopMovement(true);
                                if(x){
                                    break;
                                }
                            }
                            i++;
                        }
                    }

                    break;
                }
            }

            if (!intersects) {
                game.score = game.score - 100;
                game.numBallsAdded++;
                levelBallsAdded++;
                insertNewBall(event.getRawX(), event.getRawY(), false, Bubble.BUBBLE_REGULAR_TYPE);
            }
            updateScoreView();
            return true;
        }
        return false;
	}

	@Override
	protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        pauseGame();
        gamePlaytimeinmillies += System.currentTimeMillis() - mStartMillis;
        if (null != mSoundPool) {
            mSoundPool.unload(mSoundID);
            mSoundPool.release();
            mSoundPool = null;
        }

        mAudioManager.unloadSoundEffects();
	}

	@Override
	public void onBackPressed() {
        openOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.quit:
			gameOver();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

    public int getNumBallsOnScreen(){
        if(mFrame!=null){
            return mFrame.getChildCount();
        } else {
            return 0;
        }
    }

    protected void updateScoreView(){
        scoreView.setText(String.valueOf(game.score));
        if(isPaused){
            ballsView.setText(String.valueOf(game.MAX_BALLS - views.size()));
        }else {
            ballsView.setText(String.valueOf(game.MAX_BALLS - game.totalRegularBalls));
        }
        ballsPoppedView.setText(String.valueOf(game.numBallsPopped));
    }

	private void exitRequested() {
		super.onBackPressed();
	}

    public void setCountDownView(String text){
        countDownView.setText(text);
    }

    private void startThreadServices(){
        if(areThreadsRunning){
            return;
        }

        bubbleMoverService = Executors
                .newScheduledThreadPool(1);
        bubbleMoverService.scheduleWithFixedDelay(bubbleMover,0,17,TimeUnit.MILLISECONDS);
        collisionExecutor = Executors
                .newScheduledThreadPool(1);
        collisionExecutor.scheduleWithFixedDelay(collisionDetector, 0, 17, TimeUnit.MILLISECONDS);

        if(game.game_mode==Game.INFINITE_PLAY_MODE) {
            chaosExecutor = Executors
                    .newScheduledThreadPool(1);
            chaosExecutor.scheduleWithFixedDelay(chaosCreator, 3000, 3000, TimeUnit.MILLISECONDS);
        }
        areThreadsRunning = true;
    }

    private void shutdownThreadServices(){
        if(areThreadsRunning) {
            if(game.game_mode==Game.INFINITE_PLAY_MODE) {
                chaosExecutor.shutdown();
            }
            collisionExecutor.shutdown();
            bubbleMoverService.shutdown();
        }
        areThreadsRunning = false;
    }


    protected GameActivity(Parcel in) {
        mStartMillis = in.readLong();
        mEndMillis = in.readLong();
        maxVelocity = (Velocity) in.readValue(Velocity.class.getClassLoader());
        sinceLastFrictionBall = in.readInt();
        gamePlaytimeinmillies = in.readLong();
        isPaused = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mStartMillis);
        dest.writeLong(mEndMillis);
        dest.writeValue(maxVelocity);
        dest.writeInt(sinceLastFrictionBall);
        dest.writeLong(gamePlaytimeinmillies);
        dest.writeByte((byte) (isPaused ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GameActivity> CREATOR = new Parcelable.Creator<GameActivity>() {
        @Override
        public GameActivity createFromParcel(Parcel in) {
            return new GameActivity(in);
        }

        @Override
        public GameActivity[] newArray(int size) {
            return new GameActivity[size];
        }
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        views = null;
        //finish();
    }


}