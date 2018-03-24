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

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private String tag = "SIGNUPACTIVITY";

    //input fields
    private EditText inputEmail,
            inputName,
            inputPassword,
            inputPasswordRe;

    //labels
    private TextView labelEmail,
                     labelName,
                     labelPassword;

    private TextView textMessage;
    private Button buttonSubmit;


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

        //labels (for validation highlighting)
        labelEmail = findViewById(R.id.SignUpTextViewLabelEmail);
        labelName = findViewById(R.id.SignUpTextViewLabelName);
        labelPassword = findViewById(R.id.SignUpTextViewLabelPassword);


        // set event handlers --------------------------------------
        Log.d(tag, "Attaching event handlers.");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = getFieldsData();
                if(data != null) {
                    Log.d(tag, "Submitting input data.");
                    submitSignUpRequest(data);
                }
            }
        });
    }

    @Nullable
    private JSONObject getFieldsData(){
        JSONObject json = new JSONObject();

        Log.d(tag, "Gathering input data.");

        try{

            boolean valid = true;
            String errorMessage = "";

            /*
                "email"
                "name"
                "password"
             */

            String  email = inputEmail.getText().toString(),
                    name = inputName.getText().toString(),
                    password = inputPassword.getText().toString(),
                    passwordRe = inputPasswordRe.getText().toString();

            if(!InputValidation.validateEmail(email)){
                Log.d(tag, "Invalid email: " + email);
                errorMessage += getString(R.string.message_invalid_email);
                labelEmail.setTextColor(getResources().getColor(R.color.colorError));
                valid = false;
            } else {
                labelEmail.setTextColor(getResources().getColor(R.color.colorText));
            }

            if(!InputValidation.validateUsername(name)){
                Log.d(tag, "Invalid username: " + name);
                errorMessage += getString(R.string.message_invalid_username);
                labelName.setTextColor(getResources().getColor(R.color.colorError));
                valid = false;
            } else {
                labelName.setTextColor(getResources().getColor(R.color.colorText));
            }

            if(!password.equals(passwordRe) || InputValidation.checkIfEmpty(password)){
                Log.d(tag, "Password fields do not match : " + password + "->" + passwordRe);
                labelPassword.setTextColor(getResources().getColor(R.color.colorError));
                errorMessage += getString(R.string.message_passwords_do_not_match);
                valid = false;
            }else{
                labelPassword.setTextColor(getResources().getColor(R.color.colorText));
            }

            if(!valid) {
                textMessage.setText(errorMessage);
                textMessage.setVisibility(View.VISIBLE);
                return null;
            }else{
                textMessage.setText("");
                textMessage.setVisibility(View.INVISIBLE);
                Log.d(tag, "All input fields are valid.");
            }

            Log.d(tag, "Adding fields to JSON object.");
            json.put("email", email);
            json.put("name", name);
            json.put("password", password);
        } catch (JSONException e){
            Log.e(tag, e.getMessage());
            Log.e(tag, Log.getStackTraceString(e));
            return null;
        }

        return json;
    }

    private void submitSignUpRequest(JSONObject postMessage)
    {
        final String endpoint = "create_account";
        AppController.getInstance().cancelPendingRequests(tag + ":" + endpoint);

        Log.d(tag, "API /" + endpoint + " Request POST Body : " + postMessage.toString());

        // request object to be added to volley's request queue
        Log.d(tag, "API /" + endpoint + " creating request object.");
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // request method
                AppController.defaultBaseUrl + "/" + endpoint, // target url
                postMessage, // json object from hashmap
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(tag, "API /" + endpoint + " response is ready!");
                        try {
                            Log.d(tag, "API /" + endpoint + " raw response : " + response.toString());

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
                            Log.e(tag, "API /" + endpoint + " error parsing response: " + e.getMessage());
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

        Log.d(tag, "API /" + endpoint + " adding request object to request queue.");
        AppController.getInstance().addToRequestQueue(request, tag + ":" + endpoint);
    }
}
