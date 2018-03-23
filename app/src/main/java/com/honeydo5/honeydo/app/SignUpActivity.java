package com.honeydo5.honeydo.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.honeydo5.honeydo.R;

import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {
    private String tag = "SIGNUPACTIVITY";
    private Button buttonSubmit;
    private EditText inputEmail,
                     inputName,
                     inputPassword,
                     inputPasswordRe;

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

        // intent to sign up activity
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateFieldsInput())
                {
                   //TODO: submit data
                }
            }
        });
    }

    private boolean validateFieldsInput(){
        // TODO: validate input fields
        return true;
    }

    /*private JSONObject getFieldsData(){
        //return json;
    }   */
}
