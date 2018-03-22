package com.honeydo5.honeydo.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.honeydo5.honeydo.R;

public class SignUpActivity extends AppCompatActivity {
    private String tag = "SIGNUPSCREEN";

    private Button buttonSubmit;
    private EditText email, username, password, confpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "Setting SignupScreen activity content view.");
        setContentView(R.layout.activity_sign_up);

        Log.d(tag, "Finding components and views.");
        buttonSubmit = findViewById(R.id.SignUpButtonSubmit);
        email = findViewById(R.id.SignUpEditTextEmail);
        username = findViewById(R.id.SignUpEditTextName);
        password = findViewById(R.id.SignUpEditTextPassword);
        confpassword = findViewById(R.id.SignUpEditTextPasswordRe);

        // intent to sign up activity
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
