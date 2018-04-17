package com.honeydo5.honeydo.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.honeydo5.honeydo.R;
import com.honeydo5.honeydo.util.LruBitmapCache;
import com.honeydo5.honeydo.util.NotificationSystem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class AppController extends Application {
    private AppController mInstance;
    private Context context = this;

    public static final String TAG = AppController.class.getSimpleName();
    public static final String defaultBaseUrl = "http://api.honeydo5.com";

    public static String notifChannelId;
    public static String notifChannelName;
    public static String notifChannelDesc;

    NotificationChannel nChannel;
    NotificationManager nManager;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        NotificationSystem.initialize(this);

        // Use the default CookieManager
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);

        // register application's notification channel
        notifChannelId   = getString(R.string.notification_channel_id);
        notifChannelName = getString(R.string.notification_channel_name);
        notifChannelDesc = getString(R.string.notification_channel_description);

        nManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // if in the appropriate build, set the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            nChannel = new NotificationChannel(notifChannelId, notifChannelName, importance);
            nChannel.setDescription(notifChannelDesc);
            // register the channel with the system
            try{nManager.createNotificationChannel(nChannel);}
            catch(NullPointerException e){
                Log.e(TAG, e.getMessage());
            }
        }


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

    public Calendar parseDateTimeString(String str)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyyhh:mm aa", Locale.US);
        try {
            cal.setTime(sdf.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void login(final ILogin activity, String login, String password) {
        final String endpoint = "login";
        final String activityTag = activity.getTag();

        AppController.getInstance().cancelPendingRequests(activityTag + ":" + endpoint);

        // create request body (key : value) pairs
        HashMap<String, String> postMessage = new HashMap<>(); // assumes <String, String>
        postMessage.put("email", login);
        postMessage.put("password", password);

        Log.d(activityTag, "API /" + endpoint + " Request POST Body : " + postMessage.toString());

        // request object to be added to volley's request queue
        Log.d(activityTag, "API /" + endpoint + " creating request object.");
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // request method
                AppController.defaultBaseUrl + "/" + endpoint, // target url
                new JSONObject(postMessage), // json object from hashmap
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(activityTag, "API /" + endpoint + " raw response : " + response.toString());
                        /*  Possible endpoint responses :
                                {‘status’: ‘logged in’},
                                {‘status’: ‘already logged in’},
                                {‘status’: ‘wrong email/password’},
                                {‘status’: ‘you must specify email and password’},
                                {‘status’: ‘invalid request’}
                        */
                        try {
                            String status = response.get("status").toString();
                            activity.onLoginResponse(status);
                        } catch(JSONException e) {
                            // log and do a stack trace
                            Log.e(activityTag, "API /" + endpoint + " error parsing response: " + e.getMessage());
                            Log.getStackTraceString(e);
                            activity.onLoginResponseParseError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // log the error
                AppController.getInstance().requestNetworkError(error, activityTag, "/" + endpoint);
                // print a message for the user
                activity.onLoginNetworkError(error);
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

    public void sessionExpired(HoneyDoActivity from_activity)
    {
        Log.d(from_activity.getTag(), "Session expired, intent to LoginScreenActivity");
        Intent intent = new Intent(from_activity, LoginScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
    }
}
