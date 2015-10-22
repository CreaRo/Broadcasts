package rish.crearo.gcmtester.Database;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import rish.crearo.gcmtester.Utils.AppController;
import rish.crearo.gcmtester.Utils.Constants;

/**
 * Created by rish on 10/10/15.
 */
public class EachGroup extends SugarRecord<EachGroup> {

    public String name, owner, createdOn;
    public boolean subscribed;

    public EachGroup() {

    }

    public EachGroup(String name, String owner, String createdOn, boolean subscribed) {
        this.name = name;
        this.owner = owner;
        this.createdOn = createdOn;
        this.subscribed = subscribed;
    }

    public static void refreshGroups(final Context context, final RefreshGroupCallback callback) {

        JsonObjectRequest objectRequest = new JsonObjectRequest(Constants.BASE_URL_GROUPS, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
                try {
                    EachGroup.deleteAll(EachGroup.class);
                    JSONArray postsArray = response.getJSONArray(Constants.TAG_GROUPS);
                    for (int i = 0; i < postsArray.length(); i++) {
                        JSONObject jsonObject = postsArray.getJSONObject(i);
                        EachGroup group = new EachGroup(jsonObject.getString("g_name").toLowerCase(), jsonObject.getString("g_owner").toLowerCase(), jsonObject.getString("g_created_date"), false);
                        group.save();
                    }
                    callback.onSuccessGroupRefresh();
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailureGroupRefresh();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error message man " + error.getMessage());
                VolleyLog.d("error man", "Error: " + error.getMessage());
                callback.onFailureGroupRefresh();
            }
        });
        AppController.getInstance().addToRequestQueue(objectRequest);
    }

    public interface RefreshGroupCallback {
        void onSuccessGroupRefresh();

        void onFailureGroupRefresh();
    }
}