package com.honeydo5.honeydo.app;

import com.android.volley.VolleyError;

import org.json.JSONException;

public interface ILogin extends IActivityTag {
    void onLoginResponse(String response, Object ... args);
    void onLoginNetworkError(VolleyError e, Object ... args);
    void onLoginResponseParseError(JSONException e);
}
