package com.honeydo5.honeydo.app;

<<<<<<< HEAD
import com.honeydo5.honeydo.R;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.Response;

import java.util.Map;
import java.util.HashMap;
=======
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.honeydo5.honeydo.R;

import org.json.JSONObject;
>>>>>>> 3a0d4b3d5629e846e944ce02d0bf84e95ee7d4af


public class LoginScreen extends AppCompatActivity {

<<<<<<< HEAD
    private EditText email, password;
    private Button loginBtn, newBtn;
    private TextView message;
=======
    public JSONObject dummy;

    private EditText email, password;
    private Button loginBtn, newBtn;
>>>>>>> 3a0d4b3d5629e846e944ce02d0bf84e95ee7d4af

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
<<<<<<< HEAD

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login);
        newBtn = findViewById(R.id.signup);
        message = findViewById(R.id.message);
=======
        getJSON();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        loginBtn = (Button)findViewById(R.id.login);
        newBtn = (Button)findViewById(R.id.signup);
>>>>>>> 3a0d4b3d5629e846e944ce02d0bf84e95ee7d4af

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

<<<<<<< HEAD
    private void attemptLogin()
    {
        Log.d("DEBUG", "Trying to login.");

        // TODO use JSON (the backend is not using JSON!)

        StringRequest loginRequest = new StringRequest(
                Request.Method.POST, AppController.defaultBaseUrl + "/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO this is ugly, talk to backend...
                        Log.d("DEBUG", response);

                        switch(response)
                        {
                            case "logged in" :
                                message.setText("Logged in!");
                                Log.d("DEBUG", "Logged in!");
                                break;

                            case "you logged in already" :
                                message.setText("You are already logged in.");
                                Log.d("DEBUG", "You are already logged in.");
                                break;

                            case "wrong email/password" :
                                message.setText("Incorrect email or password.");
                                Log.d("DEBUG", "Incorrect email or password.");
                                break;
                        }

                    /*Context context = getApplicationContext();
                    Intent intent = new Intent(context, MainScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(intent);
                    context.finish();*/
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR","Something happened in the way of heaven:" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());

                Log.d("DEBUG", params.toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(loginRequest);
=======
    private void getJSON(){
        String dummyStr = "{\"user\":\"test\",\"password\":\"admin\"}";
        try {
            dummy = new JSONObject(dummyStr);
        } catch (Throwable t)
        {

        }

    }

    private void attemptLogin()
    {
        String emailStr = email.getText().toString();
        String passStr = password.getText().toString();

        Intent intent = new Intent(this, MainScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
>>>>>>> 3a0d4b3d5629e846e944ce02d0bf84e95ee7d4af
    }
}

