package rish.crearo.gcmtester.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import rish.crearo.gcmtester.Utils.AppController;
import rish.crearo.gcmtester.Utils.Constants;

/**
 * Created by rish on 10/10/15.
 */
public class User {

    private static String userName, password, designation;
    private static int permissionNumber;

    public static void setUsername(Context context, String username) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.USERNAME, username);
        editor.apply();
        editor.commit();
    }

    public static String getUsername(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String name = preferences.getString(Constants.USERNAME, null);
        return name;
    }

    public static void setPassword(String pwd, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.PASSWORD, pwd);
        editor.apply();
        editor.commit();
    }

    public static String getPassword(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Constants.PASSWORD, null);
    }

    public static int getPermissionNumber() {
        return 1;
    }

    public static String getDesignation() {
        return "student";
    }

    //verifies user through server
    public static void verifyUser(String username, String pwd, final UserAuthenticationListener listener) {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Constants.BASE_URL_VERIFY + "/" + username + "/" + pwd, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("Printing the response => ");
                Log.d("response", response.toString());
                if (response.toString().contains("true"))
                    listener.AuthenticationResult(true);
                else
                    listener.AuthenticationResult(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error message man " + error.getMessage());
                VolleyLog.d("error man", "Error: " + error.getMessage());
                listener.AuthenticationResult(false);
            }
        });
        AppController.getInstance().addToRequestQueue(objectRequest);
    }

    public interface UserAuthenticationListener {
        void AuthenticationResult(boolean auth);
    }
}