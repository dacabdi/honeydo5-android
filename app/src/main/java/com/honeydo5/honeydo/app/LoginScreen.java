package com.honeydo5.honeydo.app;

import com.android.volley.toolbox.JsonObjectRequest;
import com.honeydo5.honeydo.R;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.Response;

import org.json.JSONObject;
import org.json.JSONException;


public class LoginScreen extends AppCompatActivity {
    private EditText email, password;
    private Button loginBtn, newBtn;
    private TextView message;

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

        JSONObject postMessage = new JSONObject();
        try {
            postMessage.put("email", email.getText());
            postMessage.put("password", password.getText());
        } catch (JSONException e) {
            // TODO: do something about this exception, log it or something like that...
        }

        JsonObjectRequest loginReq = new JsonObjectRequest(
                Request.Method.POST,
                AppController.defaultBaseUrl + "/login",
                postMessage,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSON-RESPONSE-LOGIN", response.toString());

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
        });

        AppController.getInstance().addToRequestQueue(loginReq);
    }
}

