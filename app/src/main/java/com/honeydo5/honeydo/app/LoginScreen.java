package com.honeydo5.honeydo.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.honeydo5.honeydo.R;

import org.json.JSONObject;


public class LoginScreen extends AppCompatActivity {

    public JSONObject dummy;

    private EditText email, password;
    private Button loginBtn, newBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        getJSON();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        loginBtn = (Button)findViewById(R.id.login);
        newBtn = (Button)findViewById(R.id.signup);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

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
    }
}

