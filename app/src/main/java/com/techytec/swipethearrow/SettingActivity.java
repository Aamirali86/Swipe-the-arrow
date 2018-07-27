package com.techytec.swipethearrow;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.net.URI;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    Button share, rate, contact, tutorial, moreapps;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.back = (ImageButton) findViewById(R.id.back);
        this.share = (Button) findViewById(R.id.share);
        this.rate = (Button) findViewById(R.id.rate);
        this.contact = (Button) findViewById(R.id.contactus);
        this.tutorial = (Button) findViewById(R.id.tutorial);
        this.moreapps = (Button) findViewById(R.id.moreapps);

        this.back.setOnClickListener(this);
        this.share.setOnClickListener(this);
        this.rate.setOnClickListener(this);
        this.contact.setOnClickListener(this);
        this.tutorial.setOnClickListener(this);
        this.moreapps.setOnClickListener(this);

    }

    public void btnShareApp() {

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Check this out\n\nhttps://play.google.com/store/apps/details?id=com.techytec.swipethearrow";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share link!"));
    }

    public void btnRateApp() {

        final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

        if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0)
        {
            startActivity(rateAppIntent);
        }
        else
        {
            Toast.makeText(this, "Could not open Rate Play Store", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnMoreApp() {
        String url = "https://play.google.com/store/apps/developer?id=techytecapps&hl=en";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void btnContactUs() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, "techytec@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");

        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    public void btnTutorial() {
        Intent i = new Intent(SettingActivity.this, TutorialActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share:
                btnShareApp();
                break;
            case R.id.rate:
                btnRateApp();
                break;
            case R.id.contactus:
                btnContactUs();
                break;
            case R.id.tutorial:
                btnTutorial();
                break;
            case R.id.moreapps:
                btnMoreApp();
                break;
            case R.id.back:
                finish();
            default:
                break;
        }
    }
}
