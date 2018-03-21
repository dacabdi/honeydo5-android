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
    private EditText email, password;
    private Button loginBtn, newBtn;
    private TextView message;
    private TextView errorText, serverErrorText;

    private Button testLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        email = findViewById(R.id.LoginScreenEditTextEmail);
        password = findViewById(R.id.LoginScreenEditTextPassword);
        loginBtn = findViewById(R.id.LoginScreenButtonLogin);
        newBtn = findViewById(R.id.LoginScreenButtonSignup);
        message = findViewById(R.id.message);

        // for when the server isn't responding
        testLoginBtn = (Button) findViewById(R.id.test_login);

        errorText = findViewById(R.id.loginErrorText);
        serverErrorText = findViewById(R.id.serverErrorText);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptLogin();
            }
        });

        
        newBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Intent intent = new Intent(LoginScreen.this, SignUpActivity.class);
                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  startActivity(intent);
              }
        });

        testLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSuccess();
            }
        });
    }

    private void attemptLogin() {
        // create request body (key : value) pairs
        HashMap<String, String> postMessage = new HashMap<>(); // assumes <String, String>
        postMessage.put("email", email.getText().toString());
        postMessage.put("password", password.getText().toString());

        Log.d("API-LOGIN-POST", postMessage.toString());

        // request object to be added to volley's request queue
        JsonObjectRequest loginReq = new JsonObjectRequest(
                Request.Method.POST, // request method
                AppController.defaultBaseUrl + "/login", // target url
                new JSONObject(postMessage), // json object from hashmap
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // NOTE: use the logs for now
                        // attach a debugger, look at logcat.

                        Log.d("API-LOGIN-RESPONSE", response.toString());


                        // test@ufl.edu
                        // password
                        try {
                            switch (response.get("status").toString()){
                                case "success" :
                                    loginSuccess();
                                break;
                                case "logged in":
                                    loginSuccess();
                                break;
                                case "wrong email/password" :
                                    errorText.setVisibility(View.VISIBLE);
                                break;

                                case "you must specify email and password" :
                                    errorText.setVisibility(View.VISIBLE);
                                break;
                            }
                        } catch(JSONException e)
                        {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API-LOGIN-ERROR", "Something happened in the way of heaven : " + error.getMessage());
                serverErrorText.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>(); // assumes <String, String> template params
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };


        try {
            Log.d("API-LOGIN-BODY", new String(loginReq.getBody(), "UTF-8"));
            Log.d("API-LOGIN-BODYCTYPE", loginReq.getBodyContentType());
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AppController.getInstance().addToRequestQueue(loginReq, "login");
    }

    void loginSuccess()
    {
        Intent intent = new Intent(this, MainScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }
}

