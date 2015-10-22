package rish.crearo.gcmtester.GCMUtils;

import android.content.Intent;

/**
 * Created by rish on 30/9/15.
 */
public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    private static final String TAG = "MyInstanceIDLS";

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
