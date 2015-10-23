package rish.crearo.gcmtester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import rish.crearo.gcmtester.Database.User;
import rish.crearo.gcmtester.Dialogs.Login;
import rish.crearo.gcmtester.GCMUtils.RegistrationIntentService;
import rish.crearo.gcmtester.Utils.Constants;

public class RegistrationActivity extends AppCompatActivity implements User.UserAuthenticationListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);

        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(Constants.PREF_SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                    finish();
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Constants.PREF_REGISTRATION_COMPLETE));

        if (User.getUsername(getApplicationContext()) != null) {
            if (checkPlayServices()) {
                Log.d(TAG, "checked PlayServices");
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            } else {
                Snackbar.make(findViewById(R.id.main_rellay), "This device isn't supported", Snackbar.LENGTH_LONG).show();
            }
        } else {
            new Login(RegistrationActivity.this, RegistrationActivity.this).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Constants.PREF_REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        System.out.println("Checking play services");
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void AuthenticationResult(boolean auth) {
        if (auth) {
            if (checkPlayServices()) {
                Log.d(TAG, "checked PlayServices");
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            } else {
                Snackbar.make(findViewById(R.id.main_rellay), "This device isn't supported", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(findViewById(R.id.main_rellay), "Login using your Webmail ID!", Snackbar.LENGTH_LONG).show();
        }
    }
}