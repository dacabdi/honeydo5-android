package com.honeydo5.honeydo.app;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.honeydo5.honeydo.util.LruBitmapCache;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import android.os.NetworkOnMainThreadException;


public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    public static final String defaultBaseUrl = "http://api.honeydo5.com";

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    private Context context = this;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // Use the default CookieManager
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public boolean tryEndpoint(String tag) throws NetworkOnMainThreadException
    {
        return tryEndpoint(tag, "", Request.Method.GET);
    }

    public boolean tryEndpoint(String tag, String endpoint, int method) throws NetworkOnMainThreadException
    {
        if(Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Log.e(tag, "Calling a blocking request from main thread!");
            throw new NetworkOnMainThreadException();
        }

        // BEWARE: this request is blocking! Adjust timeout!

        String testTarget = defaultBaseUrl + endpoint;
        String testTag = tag + ":test:";

        String response = null;

        Log.d(testTag, "API test "+ testTarget +" creating request object.");
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(method, testTarget, future, future);
        Log.d(testTag, "API test " + testTarget + ", adding request object to request queue.");
        this.addToRequestQueue(request, testTag);

        try {
            while(response == null){
                try {
                    // block thread waiting for response (handles timeout)
                    response = future.get(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    // received interrupt signal, but still don't have response
                    // restore thread's interrupted status to use higher up on the call stack
                    Thread.currentThread().interrupt();
                    // keep waiting...
                }
            }

            // treat response
            Log.i(testTag, "API " + testTarget + " Response : " + response);
            return true;

        } catch(ExecutionException e){
            // treat error
            requestNetworkError(new VolleyError(e), testTag, testTarget);
            Log.e(tag, Log.getStackTraceString(e));
        } catch(TimeoutException e){
            // treat timeout
            Log.e(testTag,"API test " + testTarget + " timeout.");
            requestNetworkError(new VolleyError(e), testTag, testTarget);
            Log.e(tag, Log.getStackTraceString(e));
        }

        return false;
    }

    public void requestNetworkError(VolleyError error, String tag, String endpoint)
    {
        String responseBody;

        Log.e(tag,"API " + endpoint + " Request Failed.");
        // on no response, return immediately
        if (error == null || error.networkResponse == null){
            Log.e(tag,"Null response from server at "+ endpoint +", no error information.");
            return;
        }

        // get response body and parse with appropriate encoding
        try {
            responseBody = new String(error.networkResponse.data,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(tag, "Error network response error at "+ endpoint +": " + e.getMessage());
            Log.e(tag, Log.getStackTraceString(e));
            return;
        }

        Log.e(tag,"Network Response at "+ endpoint +": (STATUS:" +
                String.valueOf(error.networkResponse.statusCode)
                + ") " + responseBody);
    }

    public void writeLocalFile(String fileName, String data) {
        try {
            File file = new File(context.getFilesDir(), fileName);
            FileOutputStream out = new FileOutputStream(file, false);
            byte[] contents = data.getBytes();
            out.write(contents);
            out.flush();
            out.close();
        } catch(FileNotFoundException e){
            Log.e(TAG, "writeLocalFile : " + e.getMessage());
            e.printStackTrace();
        } catch(IOException e){
            Log.e(TAG, "writeLocalFile : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String readLocalFile(String fileName) {
        String result = null;

        File file = new File(context.getFilesDir(), fileName);
        if(file.exists()){
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                char current;
                result = "";
                while (fis.available() > 0) {
                    current = (char) fis.read();
                    result = result + String.valueOf(current);
                }
            } catch (Exception e) {
                Log.e(TAG, "Reading local file : " + e.toString());
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException e) {
                        Log.w(TAG, "Error closing local file : " + e.toString());
                    }
            }
        }

        return result;
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
