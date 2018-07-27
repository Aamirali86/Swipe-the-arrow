package com.techytec.swipethearrow;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivityFragment extends Fragment implements OnSwipeTouchListener.SimpleGestureListener {

    private RelativeLayout background;
    private TextView text_score, counterStars, requiredStars;
    private ImageView direction;
    private ProgressBar circularProgress, horizontalProgress;
    private RelativeLayout progressWheel;
    private MyPreferences myPreferences;
    private int randomColor, totalStars;
    private int randomDirection, lastRandomDirection = -1;
    private String action;
    private HorizontalProgress horizontalProgressAsyncTask;
    private CircularProgress circularProgressAsyncTask;
    public String gameType = "classic";

    public static final int RED = 0;
    public static final int GREEN = 1;

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;

    public MainActivityFragment() {
    }

    public static MainActivityFragment newInstance(int lastDirection) {
        MainActivityFragment fragment = new MainActivityFragment();
        fragment.lastRandomDirection = lastDirection;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myPreferences = new MyPreferences();
        gameType = getActivity().getIntent().getStringExtra("type");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        this.progressWheel = (RelativeLayout) view.findViewById(R.id.progressWheel);
        this.progressWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(((MainActivity)getActivity()).resumeGameCounter > totalStars) {
                    startActivity(new Intent(getActivity(), PurchaseActivity.class));
                } else {
                    progressWheel.setVisibility(View.GONE);
                    if (circularProgressAsyncTask != null)
                        circularProgressAsyncTask.cancel(true);

                    totalStars -= ((MainActivity)getActivity()).resumeGameCounter;
                    myPreferences.saveTotalStars(totalStars, getActivity());

                    requiredStars.setText(" You have: " + totalStars + " ");
                    counterStars.setText(String.valueOf(((MainActivity)getActivity()).resumeGameCounter)+" ");
                }
                ((MainActivity)getActivity()).resumeGameCounter *= 2;

            }
        });
        this.background = (RelativeLayout)view.findViewById(R.id.background);
        this.background.setOnTouchListener(new OnSwipeTouchListener(getActivity(), this));

        this.text_score = (TextView)view.findViewById(R.id.score);
        this.text_score.setText(String.valueOf(((MainActivity)getActivity()).score));
        this.direction = (ImageView)view.findViewById(R.id.direction);

        this.counterStars = (TextView) view.findViewById(R.id.starts_cost);
        this.counterStars.setText(String.valueOf(((MainActivity) getActivity()).resumeGameCounter));

        totalStars = myPreferences.retreiveTotalStars(getActivity());
        this.requiredStars = (TextView) view.findViewById(R.id.text2);
        this.requiredStars.setText(" You have: "+ totalStars + " ");

        this.horizontalProgress = (ProgressBar)view.findViewById(R.id.progressBar);
        this.horizontalProgress.setProgress(0);
        this.horizontalProgress.setProgressDrawable(getResources().getDrawable(R.drawable.progress));

        this.circularProgress = (ProgressBar) view.findViewById(R.id.circle_progress_bar);
        this.circularProgress.setProgress(100);

        if(((MainActivity)getActivity()).score > 0) {
            horizontalProgressAsyncTask = new HorizontalProgress();
            horizontalProgressAsyncTask.execute();
        }

        randomDirection = Util.generateRandomNumber(0, 3);
        if(lastRandomDirection == -1)
            lastRandomDirection = randomDirection;

        if(gameType.equals("classic")) {
            randomColor = Util.generateRandomNumber(0, 1);
            int colors[] = getResources().getIntArray(R.array.color_array);
            background.setBackgroundColor(colors[randomColor]);
        } else {
            background.setBackgroundColor(getResources().getColor(R.color.light_blue));
        }

        TypedArray imgs = getResources().obtainTypedArray(R.array.direction_array);
        direction.setImageResource(imgs.getResourceId(randomDirection, -1));
        imgs.recycle();

        setCurrentAction();

        return view;

    }

    public void showCircularProgress() {
        progressWheel.setVisibility(View.VISIBLE);
        circularProgressAsyncTask = new CircularProgress();
        circularProgressAsyncTask.execute();
    }

    public void saveScore() {
        if(gameType.equals("classic")) {
            int bestScore = myPreferences.retreiveClassicBestScore(getActivity());
            if(((MainActivity) getActivity()).score > bestScore) {
                myPreferences.saveClassicBestScore(((MainActivity) getActivity()).score, getActivity());
            }
        } else {
            int bestScore = myPreferences.retreiveExpertBestScore(getActivity());
            if(((MainActivity) getActivity()).score > bestScore) {
                myPreferences.saveExpertBestScore(((MainActivity) getActivity()).score, getActivity());
            }
        }
    }

    public void setCurrentAction() {
        if(gameType.equals("classic")) {
            if(randomColor == RED && randomDirection == LEFT)
                action = "right";
            else if(randomColor == RED && randomDirection == RIGHT)
                action = "left";
            else if(randomColor == RED && randomDirection == UP)
                action = "down";
            else if(randomColor == RED && randomDirection == DOWN)
                action = "up";
            else if(randomColor == GREEN && randomDirection == LEFT)
                action = "left";
            else if(randomColor == GREEN && randomDirection == RIGHT)
                action = "right";
            else if(randomColor == GREEN && randomDirection == UP)
                action = "up";
            else if(randomColor == GREEN && randomDirection == DOWN)
                action = "down";
        } else {
            if(lastRandomDirection == LEFT)
                action = "left";
            else if(lastRandomDirection == RIGHT)
                action = "right";
            else if(lastRandomDirection == UP)
                action = "up";
            else if(lastRandomDirection == DOWN)
                action = "down";
        }
    }

    private void checkSwipe(String value, int swipe_direction) {
        //check if the action is right or wrong
        if(progressWheel.getVisibility() != View.VISIBLE) {
            if (action.equals(value)) {
                ((MainActivity) getActivity()).score++;
                this.text_score.setText(String.valueOf(((MainActivity) getActivity()).score));
                ((MainActivity) getActivity()).replaceFragment(swipe_direction, randomDirection);
                if (horizontalProgressAsyncTask != null)
                    horizontalProgressAsyncTask.cancel(true);
            }
            else {
                showCircularProgress();
                saveScore();
            }
        }
    }

    @Override
    public void onSwipeLeft() {
        checkSwipe("left", MainActivity.SWIPE_LEFT);
    }

    @Override
    public void onSwipeRight() {
        checkSwipe("right", MainActivity.SWIPE_RIGHT);
    }

    @Override
    public void onSwipeTop() {
        checkSwipe("up", MainActivity.SWIPE_UP);
    }

    @Override
    public void onSwipeBottom() {
        checkSwipe("down", MainActivity.SWIPE_DOWN);
    }

    public class HorizontalProgress extends AsyncTask<Void, Integer, String> {
        int count = 0;

        @Override
        protected String doInBackground(Void... params) {
            while (count < 20) {
                SystemClock.sleep(50);
                count++;
                publishProgress(count * 5);
            }
            return "Complete";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            horizontalProgress.setProgress(values[0]);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            count = 20;
        }

        @Override
        protected void onPostExecute(String state) {
            if(state.equals("Complete")) {
                //((MainActivity) getActivity()).finishActivity();
                if(progressWheel.getVisibility() != View.VISIBLE)
                    showCircularProgress();

                saveScore();
            }
        }

    }

    public class CircularProgress extends AsyncTask<Void, Integer, String> {
        int count = 50;

        @Override
        protected String doInBackground(Void... params) {
            while (count > 0) {
                SystemClock.sleep(40);
                count--;
                publishProgress(count * 2);
            }
            return "Complete";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            circularProgress.setProgress(values[0]);
        }

        @Override
        protected  void onPostExecute(String state) {
            if(state.equals("Complete")) {
                try {
                    ((MainActivity) getActivity()).finishActivity();
                } catch (NullPointerException e) {
                }
            }
        }

    }

}
