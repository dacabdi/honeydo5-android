package com.honeydo5.honeydo.app;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.honeydo5.honeydo.R;
import com.honeydo5.honeydo.util.InputValidation;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private String tag = "SIGNUPACTIVITY";
    private Button buttonSubmit;
    private EditText inputEmail,
                     inputName,
                     inputPassword,
                     inputPasswordRe;
    private TextView textMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "Setting SignUpActivity content view.");
        setContentView(R.layout.activity_sign_up);

        Log.d(tag, "Finding components and views.");
        buttonSubmit = findViewById(R.id.SignUpButtonSubmit);
        inputEmail = findViewById(R.id.SignUpEditTextEmail);
        inputName = findViewById(R.id.SignUpEditTextName);
        inputPassword = findViewById(R.id.SignUpEditTextPassword);
        inputPasswordRe = findViewById(R.id.SignUpEditTextPasswordRe);
        textMessage = findViewById(R.id.SignUpTextViewMessage);

        // set event handlers --------------------------------------
        Log.d(tag, "Attaching event handlers.");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateFieldsInput()) {
                    JSONObject data = getFieldsData();
                    if(data != null) submitSignUpRequest(data);
                    else {
                        textMessage.setVisibility(View.VISIBLE);
                        textMessage.setText(getString(R.string.message_communication_problem));
                    }
                }
            }
        });
    }

    private boolean validateFieldsInput(){
        if(InputValidation.validateEmail(inputEmail.getText().toString())){
            textMessage.setText(getString(R.string.message_passwords_do_not_match));
            textMessage.setVisibility(View.VISIBLE);
            return false;
        }

        if(InputValidation.validateUsername(inputName.getText().toString())){
            textMessage.setText(getString(R.string.message_invalid_username));
            textMessage.setVisibility(View.VISIBLE);
            return false;
        }

        // check on passwords being equal
        if(inputPassword.getText().toString() != inputPasswordRe.getText().toString()){
            textMessage.setText(getString(R.string.message_passwords_do_not_match));
            textMessage.setVisibility(View.VISIBLE);
            return false;
        }

        return true;
    }

    @Nullable
    private JSONObject getFieldsData(){
        JSONObject json = new JSONObject();

        try{
            json.put("email", inputEmail.getText().toString());
            json.put("name", inputName.getText().toString());
            json.put("password", inputPassword.getText().toString());
        } catch (JSONException e){
            Log.e(tag, e.getMessage());
            Log.e(tag, Log.getStackTraceString(e));
            return null;
        }

        return json;
    }

    private void submitSignUpRequest(JSONObject postMessage)
    {
        AppController.getInstance().cancelPendingRequests(tag);

        Log.d(tag, "API /create_account Request POST Body : " + postMessage.toString());

        // request object to be added to volley's request queue
        Log.d(tag, "API /create_account creating request object.");
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // request method
                AppController.defaultBaseUrl + "/create_account", // target url
                postMessage, // json request object
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(tag, "API /create_account response is ready!");
                        try {
                            Log.d(tag, "API /create_account raw response : " + response.toString());

                            /*
                            API specification responses:

                            {‘status’: ‘success’}
                            {‘status’: ‘cannot add dup account’}
                            {‘status’: ‘you must specify name, email, password’}
                            */

                            // TODO: set intents and make a decision on response...
                            String status = response.get("status").toString();
                            textMessage.setText(status);
                            textMessage.setVisibility(View.VISIBLE);
                        } catch(JSONException e) {
                            // print a message for the user
                            textMessage.setText(getString(R.string.message_communication_problem));
                            textMessage.setVisibility(View.VISIBLE);
                            // log and do a stack trace
                            Log.e(tag, "API /create_account error parsing response: " + e.getMessage());
                            Log.getStackTraceString(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // log the error
                AppController.getInstance().requestNetworkError(error, tag, "/login");
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

        Log.d(tag, "API /create_account adding request object to request queue.");
        AppController.getInstance().addToRequestQueue(request, tag);
    }
}
