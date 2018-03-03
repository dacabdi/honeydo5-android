package com.honeydo5.honeydo.util;

import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class ApiConnectorJSON extends ApiConnector {

    /**
     * Creates a new JSON request API Connector object for HoneyDo.
     * This connector will be used to poll APIs treating both request
     * and response JSON objects.
     *
     * @param baseUrl baseUrl for the requests.
     * @param method HTTP request method.
     * @param api corresponding API resource identifier under base URL.
     * @param tag used to identify the request in volley's requests queue.
     * @param jsonRequest A {@link JSONObject} to post with the request. Null is allowed and
     *   indicates no parameters will be posted along with request.
     * @param listener A {@link Listener<JSONObject>} to receive and handle the JSON response
     * @param errorListener An {@link ErrorListener} Error listener to handle errors,
     *   or null to ignore errors.
     */
    public ApiConnectorJSON(String baseUrl, String api, int method, String tag,
                            JSONObject jsonRequest, Listener<JSONObject> listener,
                            ErrorListener errorListener) {
        // init superclass
        super(baseUrl, api, method, tag);
        // create request object for the api
        request = new JsonObjectRequest(getMethod(), getUrl(), jsonRequest, listener, errorListener);
    }
}