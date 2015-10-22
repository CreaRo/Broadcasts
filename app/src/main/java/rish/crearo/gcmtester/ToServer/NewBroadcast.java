package rish.crearo.gcmtester.ToServer;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import rish.crearo.gcmtester.Database.Broadcast;
import rish.crearo.gcmtester.Database.EachGroup;
import rish.crearo.gcmtester.Database.User;
import rish.crearo.gcmtester.Utils.AppController;
import rish.crearo.gcmtester.Utils.Constants;

/**
 * Created by rish on 10/10/15.
 */
public class NewBroadcast {

    public static void sendBroadcast(Broadcast broadcast, final NewBroadcastCallback callback) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("b_title", broadcast.title);
        params.put("b_content", broadcast.content);
        params.put("b_sender", broadcast.sender);
        params.put("b_for_group", broadcast.forGroup);
        params.put("b_date_post", broadcast.datePost);
        params.put("b_date_event", broadcast.dateEvent);
        params.put("b_location", broadcast.location);

        JsonObjectRequest req = new JsonObjectRequest(Constants.BASE_URL_NEW_BROADCAST, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("RESPONSE = " + response);
                        callback.onSuccessBroadcast();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                callback.onFailureBroadcast();
            }
        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                String creds = String.format("%s:%s", Constants.API_USERNAME, Constants.API_PASSWD);
//                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                params.put("Authorization", auth);
//                return params;
//            }
        };

        // add the request object to the queue to be executed
        AppController.getInstance().addToRequestQueue(req);
    }

    public interface NewBroadcastCallback {
        void onSuccessBroadcast();

        void onFailureBroadcast();
    }


    public static void groupBroadcastableTo(final Context context, final BroadcastToListener callback) {

        final ArrayList<EachGroup> canBroadcastTo = new ArrayList<>();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Constants.BASE_URL_GROUPS_BROADCASTABLE + User.getUserName(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
                try {
                    JSONArray postsArray = response.getJSONArray(Constants.TAG_GROUPS);
                    for (int i = 0; i < postsArray.length(); i++) {
                        JSONObject jsonObject = postsArray.getJSONObject(i);
                        canBroadcastTo.add(new EachGroup(jsonObject.getString("g_name"), jsonObject.getString("g_owner"), jsonObject.getString("g_created_date"), true));
                    }
                    callback.onSuccessGroupBroadcastTo(canBroadcastTo);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailureGroupBroadcastTo();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error message man " + error.getMessage());
                VolleyLog.d("error man", "Error: " + error.getMessage());
                callback.onFailureGroupBroadcastTo();
            }
        });
        AppController.getInstance().addToRequestQueue(objectRequest);
    }

    public interface BroadcastToListener {
        void onSuccessGroupBroadcastTo(ArrayList<EachGroup> canBroadcastTo);

        void onFailureGroupBroadcastTo();
    }
}