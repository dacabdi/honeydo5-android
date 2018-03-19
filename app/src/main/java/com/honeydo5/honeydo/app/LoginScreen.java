package com.honeydo5.honeydo.app;

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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.honeydo5.honeydo.R;

import org.json.JSONObject;

public class LoginScreen extends AppCompatActivity {
    private EditText email, password;
    private Button loginBtn, newBtn;
    private TextView message;
    public JSONObject dummy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login);
        newBtn = findViewById(R.id.signup);
        message = findViewById(R.id.message);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        StringRequest loginRequest = new StringRequest(
                Request.Method.POST, AppController.defaultBaseUrl + "/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Response made", Toast.LENGTH_LONG);

                        /*Context context = getApplicationContext();
                        Intent intent = new Intent(context, MainScreen.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                        startActivity(intent);
                        context.finish();*/
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", "Something happened in the way of heaven:" + error.getMessage());
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

        Intent intent = new Intent(this, MainScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }
}

