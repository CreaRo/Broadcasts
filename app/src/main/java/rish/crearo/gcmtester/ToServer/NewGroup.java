package rish.crearo.gcmtester.ToServer;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

import rish.crearo.gcmtester.Database.EachGroup;
import rish.crearo.gcmtester.Utils.AppController;
import rish.crearo.gcmtester.Utils.Constants;

/**
 * Created by rish on 10/10/15.
 */
public class NewGroup {

    public static void createGroup(EachGroup group, final NewGroupCallback callback) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("g_name", group.name);
        params.put("g_owner", group.owner);
        params.put("g_created_date", group.createdOn);

        JsonObjectRequest req = new JsonObjectRequest(Constants.BASE_URL_NEW_GROUP, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("RESPONSE = " + response);
                        if (response.toString().contains("success")) {
                            callback.onSuccessGroup();
                        } else {
                            callback.onFailureGroup("Group Name already exists");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                callback.onFailureGroup("Error in connection");
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

    public interface NewGroupCallback {
        void onSuccessGroup();

        void onFailureGroup(String message);
    }
}