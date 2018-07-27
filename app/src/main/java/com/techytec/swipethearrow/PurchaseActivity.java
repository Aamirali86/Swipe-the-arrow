package com.techytec.swipethearrow;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

import util.IabHelper;
import util.IabResult;
import util.Inventory;
import util.Purchase;

public class PurchaseActivity extends AppCompatActivity implements View.OnClickListener {

    private Button premium_buy, silver_buy, platinum_buy, gold_buy;
    private TextView total_stars;
    private MyPreferences myPreferences;

    IabHelper mHelper;

    static final int RC_REQUEST = 10001;

    //static final String SKU_PREMIUM = "android.test.purchased";
    static final String SKU_PREMIUM  = "com.techytec.swipethearrow.adfree";
    static final String SKU_50STARS  = "com.techytec.swipethearrow.silverupdate";
    static final String SKU_100STARS = "com.techytec.swipethearrow.platinumupdate";
    static final String SKU_500STARS = "com.techytec.swipethearrow.goldupdate";

    private boolean mIsPremium = false;

    public static final String TAG = "swipethearrow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        this.myPreferences = new MyPreferences();

        this.total_stars = (TextView) findViewById(R.id.total_stars);
        this.total_stars.setText(String.valueOf(myPreferences.retreiveTotalStars(this)));

        this.premium_buy = (Button) findViewById(R.id.premium_buy);
        this.premium_buy.setOnClickListener(this);

        this.silver_buy = (Button) findViewById(R.id.silver_buy);
        this.silver_buy.setOnClickListener(this);

        this.platinum_buy = (Button) findViewById(R.id.platinum_buy);
        this.platinum_buy.setOnClickListener(this);

        this.gold_buy = (Button) findViewById(R.id.gold_buy);
        this.gold_buy.setOnClickListener(this);

        //InAppPurchaseDelegatesMethods
        String base64EncodedPublicKey = getString(R.string.inapp_id);

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_purchase, menu);
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

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful");

                /*
                 * Check for items we own. Notice that for each purchase, we check
                 * the developer payload to see if it's correct! See
                 * verifyDeveloperPayload().
                 */

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);

            //mIsPremium = (premiumPurchase != null && inventory.hasPurchase(SKU_PREMIUM));
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            // Do we have the silver upgrade?
            Purchase silverPurchase = inventory.getPurchase(SKU_50STARS);

            if (silverPurchase != null && verifyDeveloperPayload(silverPurchase)) {
                Log.d(TAG, "We have stars. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(SKU_50STARS),
                        mConsumeFinishedListener);
            }

            // Do we have the silver upgrade?
            Purchase platinumPurchase = inventory.getPurchase(SKU_100STARS);

            if (platinumPurchase != null && verifyDeveloperPayload(platinumPurchase)) {
                Log.d(TAG, "We have stars. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(SKU_100STARS),
                        mConsumeFinishedListener);
            }

            // Do we have the silver upgrade?
            Purchase goldPurchase = inventory.getPurchase(SKU_500STARS);

            if (goldPurchase != null && verifyDeveloperPayload(goldPurchase)) {
                Log.d(TAG, "We have stars. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(SKU_500STARS),
                        mConsumeFinishedListener);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium ad free upgrade. Congratulating user.");
                alert("Thank you for upgrading to premium!");
                mIsPremium = true;
                myPreferences.setPremiumUser(mIsPremium, PurchaseActivity.this);

            } else if (purchase.getSku().equals(SKU_50STARS)) {
                // bought 50 stars. So consume it.
                Log.d(TAG, "Purchase is 50 stars. Starting stars consumption.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);

            } else if (purchase.getSku().equals(SKU_100STARS)) {
                // bought 100 stars. So consume it.
                Log.d(TAG, "Purchase is 100 stars. Starting stars consumption.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);

            } else if (purchase.getSku().equals(SKU_500STARS)) {
                // bought 500 stars. So consume it.
                Log.d(TAG, "Purchase is 500 stars. Starting stars consumption.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "50 stars" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                int totalStars = myPreferences.retreiveTotalStars(PurchaseActivity.this);

                if (purchase.getSku().equals(SKU_50STARS))
                    totalStars += 50;
                else if (purchase.getSku().equals(SKU_100STARS))
                    totalStars += 100;
                else if (purchase.getSku().equals(SKU_500STARS))
                    totalStars += 500;

                total_stars.setText(String.valueOf(totalStars));
                myPreferences.saveTotalStars(totalStars, PurchaseActivity.this);
                alert("You have total" + String.valueOf(totalStars) + "stars now");
            }
            else {
                complain("Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };

    // User clicked the "Upgrade to premium" button.
    public void onPremiumUpgradePurchase() {
        String payload = UUID.randomUUID().toString();

        mHelper.launchPurchaseFlow(PurchaseActivity.this, SKU_PREMIUM, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    // User clicked the "Upgrade to silver"(50 stars) button.
    public void onSilverUpgradePurchase() {
        String payload = UUID.randomUUID().toString();

        mHelper.launchPurchaseFlow(PurchaseActivity.this, SKU_50STARS, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    // User clicked the "Upgrade to platinum"(100 stars) button.
    public void onPlatinumUpgradePurchase() {
        String payload = UUID.randomUUID().toString();

        mHelper.launchPurchaseFlow(PurchaseActivity.this, SKU_100STARS, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    // User clicked the "Upgrade to gold"(500 stars) button.
    public void onGoldUpgradePurchase() {
        String payload = UUID.randomUUID().toString();

        mHelper.launchPurchaseFlow(PurchaseActivity.this, SKU_500STARS, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    void complain(String message) {
        Log.e(TAG, "Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

    @Override
    protected void onDestroy() {
        Log.v("Main", "onDestroy");
        super.onDestroy();

        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.premium_buy:
                onPremiumUpgradePurchase();
                break;
            case R.id.silver_buy:
                onSilverUpgradePurchase();
                break;
            case R.id.platinum_buy:
                onPlatinumUpgradePurchase();
                break;
            case R.id.gold_buy:
                onGoldUpgradePurchase();
                break;
            default:
                break;

        }
    }
}
