package com.honeydo5.honeydo.util;

import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.StringRequest;

public class ApiConnectorString extends ApiConnector {

    /**
     * Creates a new request API Connector object for HoneyDo.
     * This connector will be used to poll APIs treating response
     * as plain String object.
     *
     * @param baseUrl baseUrl for the requests.
     * @param method HTTP request method.
     * @param api corresponding API resource identifier under base URL.
     * @param tag used to identify the request in volley's requests queue.
     * @param listener A {@link Listener<String>} to receive and handle the JSON response
     * @param errorListener An {@link ErrorListener} Error listener to handle errors,
     *   or null to ignore errors.
     */
    public ApiConnectorString(String baseUrl, String api, int method, String tag,
                              Listener<String> listener, ErrorListener errorListener) {
        // init superclass
        super(baseUrl, api, method, tag);
        // create request object for the api
        request = new StringRequest(getMethod(), getUrl(), listener, errorListener);
    }
}