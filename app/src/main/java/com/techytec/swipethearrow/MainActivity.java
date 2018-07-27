package com.techytec.swipethearrow;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MainActivity extends AppCompatActivity {

    public int score;
    public boolean active;
    public int resumeGameCounter = 1;

    public final static int SWIPE_UP    = 1;
    public final static int SWIPE_DOWN  = 2;
    public final static int SWIPE_LEFT  = 3;
    public final static int SWIPE_RIGHT = 4;

    RelativeLayout layout;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (RelativeLayout) findViewById(R.id.llayout);

        sendTracker();
        boolean isPreminum = new MyPreferences().isPremiumUser(this);
        if(!isPreminum)
            showAds((LinearLayout) findViewById(R.id.rootViewGroup));

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {

//        MainActivityFragment fragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
//        fragment.dispatchTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    public void finishActivity() {
        Intent i = getIntent();
        i.putExtra("score", score);
        setResult(RESULT_OK, i);
        finish();
    }

    public void replaceFragment(int direction, int randomDirection) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(direction == SWIPE_LEFT) {
            ft.setCustomAnimations(R.anim.slide_left1, R.anim.slide_left2);
        } else if(direction == SWIPE_RIGHT) {
            ft.setCustomAnimations(R.anim.slide_right1, R.anim.slide_right2);
        } else if(direction == SWIPE_UP) {
            ft.setCustomAnimations(R.anim.slide_up1, R.anim.slide_up2);
        } else if(direction == SWIPE_DOWN)
            ft.setCustomAnimations(R.anim.slide_down1, R.anim.slide_down2);

        MainActivityFragment newFragment = MainActivityFragment.newInstance(randomDirection);
        ft.replace(R.id.fragment, newFragment, String.valueOf(direction));

        // Start the animated transition.
        ft.commit();

    }

    public void sendTracker() {
        // Get tracker.
        Tracker t = ((Swipe)getApplication()).getTracker(
                Swipe.TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName("MainActivity");

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public void showAds(final LinearLayout rootLayout) {

        // Create a banner ad. The ad size and ad unit ID must be set before calling loadAd.
        mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                // Add the AdView to the view hierarchy.
                if(rootLayout != null) {
                    rootLayout.removeAllViews();
                    rootLayout.addView(mAdView);
                }
            }
        });

        // Create an ad request.
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Optionally populate the ad request builder.
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        // Start loading the ad.
        mAdView.loadAd(adRequestBuilder.build());

        //setContentView(rootLayout);
    }

}
