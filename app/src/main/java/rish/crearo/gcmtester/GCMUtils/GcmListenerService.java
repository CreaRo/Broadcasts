package rish.crearo.gcmtester.GCMUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import rish.crearo.gcmtester.Database.Broadcast;
import rish.crearo.gcmtester.Database.EachGroup;
import rish.crearo.gcmtester.Utils.Constants;
import rish.crearo.gcmtester.Utils.NotificationMaker;

/**
 * Created by rish on 30/9/15.
 */
public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService implements EachGroup.RefreshGroupCallback {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("b_title");
        String sender = data.getString("b_sender");
        Log.d(TAG, message);
        Log.d(TAG, data.toString());

        if (data.getString("b_title").equals("BROADCAST_GROUPS_CHANGED")) {
            EachGroup.refreshGroups(getApplicationContext(), this);
        } else {
            /*
            New broadcast incoming. Save it to the database.
             */
            Broadcast broadcast = new Broadcast(data.getString(Constants.BC_TITLE), data.getString(Constants.BC_CONTENT), data.getString(Constants.BC_SENDER), data.getString(Constants.BC_FOR_GROUP), data.getString(Constants.BC_DATE_POST), data.getString(Constants.BC_DATE_EVENT), data.getString(Constants.BC_LOCATION));
            broadcast.save();
            NotificationMaker.sendNotification(getApplicationContext(), message + " | " + sender);
            Intent refreshAdapter = new Intent(Constants.LOCALBR_REFRESH_ADAPTERS);
            LocalBroadcastManager.getInstance(this).sendBroadcast(refreshAdapter);
        }
    }

    @Override
    public void onSuccessGroupRefresh() {
    }

    @Override
    public void onFailureGroupRefresh() {

    }
}