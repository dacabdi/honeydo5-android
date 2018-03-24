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
import com.honeydo5.honeydo.util.DateHelper;
import com.honeydo5.honeydo.util.Task;
import com.honeydo5.honeydo.util.TaskSystem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class LoginScreenActivity extends AppCompatActivity {
    private String tag = "LOGINSCREEN";

    // views and components
    private EditText inputEmail, inputPassword;
    private Button buttonLogin, buttonSignup, buttonHitServer, buttonTestLogin;
    private TextView textMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(tag, "Setting LoginScreenActivity activity content view.");
        setContentView(R.layout.activity_login_screen);

        // grab in order top to bottom of page
        Log.d(tag, "Finding components and views.");
        inputEmail = findViewById(R.id.LoginScreenEditTextEmail);
        inputPassword = findViewById(R.id.LoginScreenEditTextPassword);
        textMessage = findViewById(R.id.LoginScreenTextViewMessage);
        buttonLogin = findViewById(R.id.LoginScreenButtonLogin);
        buttonSignup = findViewById(R.id.LoginScreenButtonSignup);
        buttonHitServer = findViewById(R.id.loginScreenButtonHitServer);
        buttonTestLogin = findViewById(R.id.loginScreenButtonTestLogin);

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
                  Intent intent = new Intent(LoginScreenActivity.this, SignUpActivity.class);
                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  Log.d(tag, "Starting SignUpActivity.");
                  startActivity(intent);
                  // TODO: determine if we should finish the current activity?
              }
        });

        buttonHitServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch a new thread,
                // the test request is sync (blocking)
                // and we don't wanna block the main thread
                new Thread(new Runnable() { public void run() {
                    String message = getString(R.string.message_endpoint_200);
                    if(!AppController.getInstance().tryEndpoint(tag)) //will land at baseUrl
                        message = getString(R.string.message_communication_problem);

                    Log.i(tag, message);
                }}).start();
            }
        });

        buttonTestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dummy tasks
                TaskSystem.addTask(new Task("Test body", "Get Eggs", true, null, DateHelper.getDate(2018, 2, 5, 12, 15), null));
                TaskSystem.addTask(new Task("Test body", "Do software engineering hw", false, null, DateHelper.getDate(2018, 2, 7, 7, 45), null));
                TaskSystem.addTask(new Task("Test body", "Study COP", true, null, DateHelper.getDate(2018, 2, 23, 6, 30), null));
                TaskSystem.addTask(new Task("Test body", "do laundry!!!!!", false, null, DateHelper.getDate(2018, 3, 5, 4, 0), null));
                TaskSystem.addTask(new Task("Test body", "test this app (meta!)", true, null, DateHelper.getDate(2018, 4, 17, 2, 15), null));

                Intent intent = new Intent(LoginScreenActivity.this, MainScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(intent);
                LoginScreenActivity.this.finish();
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreenActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void attemptLogin() {
        final String endpoint = "login";
        AppController.getInstance().cancelPendingRequests(tag + ":" + endpoint);

        // create request body (key : value) pairs
        HashMap<String, String> postMessage = new HashMap<>(); // assumes <String, String>
        postMessage.put("email", inputEmail.getText().toString());
        postMessage.put("password", inputPassword.getText().toString());

        Log.d(tag, "API /" + endpoint + " Request POST Body : " + postMessage.toString());

        // request object to be added to volley's request queue
        Log.d(tag, "API /" + endpoint + " creating request object.");
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // request method
                AppController.defaultBaseUrl + "/" + endpoint, // target url
                new JSONObject(postMessage), // json object from hashmap
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(tag, "API /" + endpoint + " raw response : " + response.toString());
                        try {
                            switch (response.get("status").toString())
                            {
                                case "success" : case "logged in" :
                                    // login success or already on session (no need for method,
                                    // this code is only called from one place)
                                    Log.d(tag, "Successful Login, intent onto MainScreenActivity, finish LoginScreenActivity");
                                    Intent intent = new Intent(LoginScreenActivity.this, MainScreenActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                                    startActivity(intent);
                                    LoginScreenActivity.this.finish();
                                break;

                                case "wrong email/password" :
                                    textMessage.setText(R.string.message_wrong_credentials);
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
                            Log.e(tag, "API /" + endpoint + " error parsing response: " + e.getMessage());
                            Log.getStackTraceString(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // log the error
                AppController.getInstance().requestNetworkError(error, tag, "/" + endpoint);
                // print a message for the user
                String errorMessage = getString(R.string.message_communication_problem);
                textMessage.setText(errorMessage);
                textMessage.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>(); // assumes <String, String> template params
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        Log.d(tag, "API /" + endpoint + " adding request object to request queue.");
        AppController.getInstance().addToRequestQueue(request, tag + ":" + endpoint);
    }
}