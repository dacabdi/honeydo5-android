package com.honeydo5.honeydo.util;

import com.android.volley.Request;
import com.honeydo5.honeydo.app.AppController;

import java.lang.String;

abstract public class ApiConnector{
    public static String defaultUrl = "http://api.honeydo5.com";
    protected int method;      // HTTP request method, GET, POST, etc...
    protected String baseUrl;  // probably just http://api.honeydo.com
    protected String api;      // api string identifier, ie: login, create_account, etc
    protected Request request; // holds the volley request object
    protected String tag;      // tag used to cancel request

    /*TODO add a criteria for tagging*/

    /**
     * Creates new HoneyDo ApiConnector object using API identifier.
     * Allows for custom baseUrl if external APIs are to be polled.
     *
     * @param baseUrl baseUrl for the requests.
     * @param api corresponding API resource identifier under base URL.
     * @param method HTTP request method.
     * @param tag used to identify the request in volley's requests queue.
     */
    public ApiConnector(String baseUrl, String api, int method, String tag) {
        this.baseUrl = baseUrl;
        this.api = api;
        this.request = null;
        this.tag = tag;
        this.method = method;
    }

    /**
     * Add the Api request to the request queue.
     */

    public void submit(){
        AppController.getInstance().addToRequestQueue(request, tag);
    }

    /**
     * Cancel the Api request in the request queue.
     */

    public void cancel(){
        AppController.getInstance().getRequestQueue().cancelAll(tag);
    }

    /**
     * Get Api name.
     */

    public String getApi()
    {
        return this.api;
    }

    /**
     * Get Api full url.
     */

    public String getUrl(){
        return getBaseUrl() + '/' + this.api;
    }

    /**
     * Get Api base url.
     */

    public String getBaseUrl(){
        return this.baseUrl;
    }

    /**
     * Get Request tag name.
     */

    public String getTag()
    {
        return this.tag;
    }

    /**
     * Get Request method.
     */

    public int getMethod()
    {
        return this.method;
    }
}