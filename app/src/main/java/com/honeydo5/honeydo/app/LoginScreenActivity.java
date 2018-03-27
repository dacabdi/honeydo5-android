package com.honeydo5.honeydo.app;

import com.android.volley.VolleyError;
import com.honeydo5.honeydo.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.honeydo5.honeydo.util.DateHelper;
import com.honeydo5.honeydo.util.Task;
import com.honeydo5.honeydo.util.TaskSystem;

import org.json.JSONException;

public class LoginScreenActivity extends HoneyDoActivity implements ILogin {
    // views and components
    private EditText inputEmail, inputPassword;
    private Button buttonLogin, buttonSignup, buttonHitServer, buttonTestLogin;
    private TextView textMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTag("LOGINSCREEN");

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
            @Override public void onClick(View v) {
                String username = inputEmail.getText().toString(),
                       password = inputPassword.getText().toString();
                AppController.getInstance().login(LoginScreenActivity.this, username, password);
            }
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
                TaskSystem.addTask(new Task( "Get Eggs","Test body", true, null, DateHelper.getDate(2018, 2, 5, 12, 15), null));
                TaskSystem.addTask(new Task( "Do software engineering hw","Test body", false, null, DateHelper.getDate(2018, 2, 7, 7, 45), null));
                TaskSystem.addTask(new Task( "Study COP","Test body", true, null, DateHelper.getDate(2018, 2, 23, 6, 30), null));
                TaskSystem.addTask(new Task( "do laundry!!!!!","Test body", false, null, DateHelper.getDate(2018, 3, 5, 4, 0), null));
                TaskSystem.addTask(new Task( "test this app (meta!)","Test body", true, null, DateHelper.getDate(2018, 4, 17, 2, 15), null));

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

    @Override public void onLoginResponse(String loginStatus, Object ... args) {
        String errorMessage = null;

        switch(loginStatus)
        {
            case "already logged in" : case "logged in":
                Log.d(this.tag, "Successful Login, intent onto MainScreenActivity, finish LoginScreenActivity");
                Intent intent = new Intent(this, MainScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(intent);
                this.finish();
            break;

            case "wrong email/password" :
                errorMessage = getString(R.string.message_wrong_credentials);
            break;

            case "you must specify email and password" :
                errorMessage = getString(R.string.message_specify_email_password);
            break;

            default :
                errorMessage = getString(R.string.message_unexpected_response);
            break;
        }

        if(errorMessage != null){
            textMessage.setText(errorMessage);
            textMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override public void onLoginNetworkError(VolleyError e, Object ... args) {
        // print a message for the user
        String errorMessage = getString(R.string.message_communication_problem);
        textMessage.setText(errorMessage);
        textMessage.setVisibility(View.VISIBLE);
    }

    @Override public void onLoginResponseParseError(JSONException e) {
        // print a message for the user
        String errorMessage = getString(R.string.message_communication_problem);
        textMessage.setText(errorMessage);
        textMessage.setVisibility(View.VISIBLE);
    }
}