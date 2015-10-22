package rish.crearo.gcmtester.ToServer;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

import rish.crearo.gcmtester.Database.User;
import rish.crearo.gcmtester.Utils.AppController;
import rish.crearo.gcmtester.Utils.Constants;
import rish.crearo.gcmtester.Utils.TheDate;

/**
 * Created by rish on 10/10/15.
 */
public class Register {

    public static void registerWithToken(String token, final RegisterCallback callback) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("u_studentid", User.getUserName());
        params.put("u_blue", User.getPassword());
        params.put("u_token", token);
        params.put("u_register_date", TheDate.getDate());
        params.put("u_permission_number", "" + User.getPermissionNumber());
        params.put("u_designation", User.getDesignation());

        JsonObjectRequest req = new JsonObjectRequest(Constants.BASE_URL_REGISTER, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("RESPONSE = " + response);
                        callback.onSuccessRegister();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                callback.onFailureRegister();
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

    public interface RegisterCallback {
        void onSuccessRegister();

        void onFailureRegister();
    }
}
