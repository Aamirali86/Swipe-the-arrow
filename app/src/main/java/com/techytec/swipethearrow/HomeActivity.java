package com.techytec.swipethearrow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private ImageButton classic, expert, home, setting, cart;
    private ImageView icon;
    private TextView score, bestscore;
    private final int ScoreRequest = 100;
    private String gameType = "classic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.icon = (ImageView) findViewById(R.id.icon);
        this.icon.setVisibility(View.VISIBLE);

        this.classic = (ImageButton) findViewById(R.id.classic);
        this.classic.setTag("classic");
        this.classic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getTag().equals(classic)) {
                    gameType = "classic";
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("type", gameType);
                    startActivityForResult(intent, ScoreRequest);
                } else {
                    // replay
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("type", gameType);
                    startActivityForResult(intent, ScoreRequest);
                }
            }
        });
        this.expert = (ImageButton) findViewById(R.id.expert);
        this.expert.setTag("expert");
        this.expert.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getTag().equals("expert")) {
                    int bestScore = new MyPreferences().retreiveClassicBestScore(HomeActivity.this);
                    if(bestScore >= 150) {
                        gameType = "expert";
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        intent.putExtra("type", gameType);
                        startActivityForResult(intent, ScoreRequest);
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                        alert.setNegativeButton("Ok", null);
                        alert.setTitle("Expert Level");
                        alert.setMessage("You need to score more then 150 to unlock this level");
                        alert.show();
                    }
                } else {
                    //share
                    invokeShare(HomeActivity.this);
                }
            }
        });
        this.home = (ImageButton) findViewById(R.id.home);
        this.home.setVisibility(View.INVISIBLE);
        this.home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //home
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        this.setting = (ImageButton) findViewById(R.id.setting);
        this.setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //setting
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
            }
        });

        this.cart = (ImageButton) findViewById(R.id.cart);
        this.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent purchaseintent = new Intent(HomeActivity.this, PurchaseActivity.class);
                startActivity(purchaseintent);
            }
        });

        this.score = (TextView) findViewById(R.id.score);
        this.bestscore = (TextView) findViewById(R.id.bestscore);
        this.score.setVisibility(View.INVISIBLE);
        this.bestscore.setVisibility(View.INVISIBLE);

        final boolean isNewUser = new MyPreferences().isNewUser(this);

        if (isNewUser) {
            new MyPreferences().setNewUser(false, this);
            Intent i = new Intent(HomeActivity.this, TutorialActivity.class);
            startActivity(i);
        }

    }

    public void invokeShare(Activity activity) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        int score = new MyPreferences().retreiveClassicBestScore(this);

        String text = "I scored " +score+ " @SwipeTheArrow\n http://play.google.com/store/apps/details?id=" + getPackageName();
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.share_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        activity.startActivity(Intent.createChooser(shareIntent, "Classic Arrow"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setBestscore(String type) {
        if(type.equals("expert")) {
            int  bestScore = new MyPreferences().retreiveExpertBestScore(this);
            this.bestscore.setText("NEW Best: " + String.valueOf(bestScore));
        } else {
            int  bestScore = new MyPreferences().retreiveClassicBestScore(this);
            this.bestscore.setText("NEW Best: " + String.valueOf(bestScore));
        }
    }

    private void setCurrentScore(int score) {
        this.score.setText("Score: "+ String.valueOf(score));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ScoreRequest) {
            if(resultCode == RESULT_OK) {
                //change the ui and tags
                score.setVisibility(View.VISIBLE);
                setCurrentScore(data.getIntExtra("score", 0));
                bestscore.setVisibility(View.VISIBLE);
                setBestscore(gameType);
                home.setVisibility(View.VISIBLE);
                icon.setVisibility(View.INVISIBLE);
                classic.setImageResource(R.drawable.btn_replay);
                classic.setTag("replay");
                expert.setImageResource(R.drawable.btn_share);
                expert.setTag("share");
            }
        }
    }

}
