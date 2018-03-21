package com.honeydo5.honeydo.app;

import com.honeydo5.honeydo.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;



public class LoginScreen extends AppCompatActivity {
    private String tag = "LOGINSCREEN";

    // views and components
    private EditText inputEmail, inputPassword;
    private Button buttonLogin, buttonSignup, buttonTestLogin;
    private TextView textMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(tag, "Setting LoginScreen activity content view.");
        setContentView(R.layout.activity_login_screen);

        // grab in order top to bottom of page
        Log.d(tag, "Finding components and views.");
        inputEmail = findViewById(R.id.LoginScreenEditTextEmail);
        inputPassword = findViewById(R.id.LoginScreenEditTextPassword);
        textMessage = findViewById(R.id.LoginScreenTextViewMessage);
        buttonLogin = findViewById(R.id.LoginScreenButtonLogin);
        buttonSignup = findViewById(R.id.LoginScreenButtonSignup);
        buttonTestLogin = findViewById(R.id.LoginScreenButtonTestLogin);

        // set event handlers --------------------------------------
        Log.d(tag, "Attaching event handlers.");

        // attempt login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { attemptLogin(); }
        });

        // intent to sign up activity
        buttonSignup.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Intent intent = new Intent(LoginScreen.this, SignUpActivity.class);
                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  Log.d(tag, "Starting SignUpActivity.");
                  startActivity(intent);
                  // TODO: determine if we should finish the current activity?
              }
        });

        buttonTestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSuccess();
            }
        });
    }

    private void attemptLogin() {
        // create request body (key : value) pairs
        HashMap<String, String> postMessage = new HashMap<>(); // assumes <String, String>
        postMessage.put("email", inputEmail.getText().toString());
        postMessage.put("password", inputPassword.getText().toString());

        Log.d(tag, "API /login Request POST Body : " + postMessage.toString());


        // request object to be added to volley's request queue
        Log.d(tag, "API /login creating request object.");
        JsonObjectRequest loginReq = new JsonObjectRequest(
                Request.Method.POST, // request method
                AppController.defaultBaseUrl + "/login", // target url
                new JSONObject(postMessage), // json object from hashmap
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(tag, "API /login raw response : " + response.toString());

                        try {
                            switch (response.get("status").toString())
                            {
                                case "success" : case "logged in" :
                                    loginSuccess();
                                break;

                                case "wrong email/password" :
                                    textMessage.setText("FIX THIS!"); // TODO: use string!
                                    textMessage.setVisibility(View.VISIBLE);
                                break;

                                case "you must specify email and password" :
                                    textMessage.setText(R.string.message_specify_email_password);
                                    textMessage.setVisibility(View.VISIBLE);
                                break;
                            }
                        } catch(JSONException e) {
                            // print a message for the user
                            String errorMessage = getString(R.string.message_communication_problem);
                            textMessage.setText(errorMessage);
                            textMessage.setVisibility(View.VISIBLE);

                            // log and do a stack trace
                            Log.e(tag, "API /login error parsing response: " + e.getMessage());
                            Log.getStackTraceString(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(tag,"API /login Request Failed.");

                // on no response, return immediately
                if (error == null || error.networkResponse == null){
                    Log.e(tag,"Null response from server, no error information.");
                    return;
                }

                String responseBody = null;
                // get response body and parse with appropriate encoding
                try {
                    responseBody = new String(error.networkResponse.data,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e(tag, "Error network response error: " + e.getMessage());
                    Log.getStackTraceString(e);
                }

                //get status code here
                String statusCode = String.valueOf(error.networkResponse.statusCode);

                // print a message for the user
                String errorMessage = getString(R.string.message_communication_problem);
                textMessage.setText(errorMessage);
                textMessage.setVisibility(View.VISIBLE);

                // log everything
                Log.e(tag, "Network Response: (STATUS:" + statusCode + ") " + responseBody);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>(); // assumes <String, String> template params
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        Log.d(tag, "API /login adding request object to request queue.");
        AppController.getInstance().addToRequestQueue(loginReq, "tag");
    }

    void loginSuccess()
    {
        Intent intent = new Intent(this, MainScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }
}

