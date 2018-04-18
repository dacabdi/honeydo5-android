package com.honeydo5.honeydo.app;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.honeydo5.honeydo.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends PreferenceActivity implements IActivityTag {
    protected String tag = "";

    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private Preference.OnPreferenceClickListener clickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTag("SETTINGS");

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefFragment()).commit();

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                // Set summary to be the user-description for the selected value
                if(s == "pref_mute") {
                    AppController.muteNotifications = sharedPreferences.getBoolean("pref_mute", false);
                }

            }
        };

        clickListener = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                deleteAccount();
                return true;
            }
        };
    }

    private void deleteAccount() {
        final String endpoint = "delete_account";
        final String activityTag = getTag();

        AppController.getInstance().cancelPendingRequests(activityTag + ":" + endpoint);

        // create request body (key : value) pairs
       // HashMap<String, String> postMessage = new HashMap<>(); // assumes <String, String>

        Log.d(activityTag, "API /" + endpoint);

        // request object to be added to volley's request queue
        Log.d(activityTag, "API /" + endpoint + " creating request object.");
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // request method
                AppController.defaultBaseUrl + "/" + endpoint,
                null,// target url
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(activityTag, "API /" + endpoint + " raw response : " + response.toString());

                        try {
                            String status = response.get("status").toString();
                        } catch(JSONException e) {
                            // log and do a stack trace
                            Log.e(activityTag, "API /" + endpoint + " error parsing response: " + e.getMessage());
                            Log.getStackTraceString(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // log the error
                AppController.getInstance().requestNetworkError(error, activityTag, "/" + endpoint);
                // print a message for the user
                //this.onLoginNetworkError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>(); // assumes <String, String> template params
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        Log.d(activityTag, "API /" + endpoint + " adding request object to request queue.");
        AppController.getInstance().addToRequestQueue(request, activityTag + ":" + endpoint);
    }



    public static class PrefFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.layout.preferences);
        }
    }

    @Override
    public String getTag(){
        return tag;
    }

    @Override
    public String setTag(String tag){
        this.tag = tag;
        return this.tag;
    }
}
