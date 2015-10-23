package rish.crearo.gcmtester.GCMUtils;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import rish.crearo.gcmtester.R;
import rish.crearo.gcmtester.ToServer.Register;
import rish.crearo.gcmtester.Utils.Constants;

/**
 * Created by rish on 30/9/15.
 */
public class RegistrationIntentService extends IntentService implements Register.RegisterCallback {

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(TAG, "Handling Registration Intent");
        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                Log.d(TAG, "SENDER ID = " + getString(R.string.gcm_defaultSenderId));
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(TAG, "GCM Registration Token: " + token);
                if (!sharedPreferences.getBoolean(Constants.PREF_SENT_TOKEN_TO_SERVER, false)) {
                    sendRegistrationToServer(token);
                    return;
                }
                Intent registrationComplete = new Intent(Constants.PREF_REGISTRATION_COMPLETE);
                LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(Constants.PREF_SENT_TOKEN_TO_SERVER, false).apply();
            Intent registrationComplete = new Intent(Constants.PREF_REGISTRATION_COMPLETE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        }
    }

    private void sendRegistrationToServer(String token) {
        Register.registerWithToken(getApplicationContext(), token, this);
    }

    @Override
    public void onSuccessRegister() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(TAG, "Success!");
        sharedPreferences.edit().putBoolean(Constants.PREF_SENT_TOKEN_TO_SERVER, true).apply();
        Intent registrationComplete = new Intent(Constants.PREF_REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    @Override
    public void onFailureRegister() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(TAG, "Failed to complete token refresh");
        sharedPreferences.edit().putBoolean(Constants.PREF_SENT_TOKEN_TO_SERVER, false).apply();
        Intent registrationComplete = new Intent(Constants.PREF_REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}