package com.honeydo5.honeydo.app;

import com.android.volley.VolleyError;
import com.honeydo5.honeydo.R;
import com.honeydo5.honeydo.util.NotificationSystem;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Calendar;

public class LoginScreenActivity extends HoneyDoActivity implements ILogin {
    // views and components
    private EditText inputEmail, inputPassword;
    private Button  buttonLogin,
                    buttonSignup,
                    buttonHitServer,
                    buttonTestLogin; // used for notification testing temporarily
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

        buttonTestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*testLogin();*/
                testNotification();
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

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreenActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
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

    /*private void testLogin() {
        Intent intent = new Intent(this, MainScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
    }*/

    private void testNotification() {
        Log.d(tag, "Test notification.");

        Intent intent = new Intent(this, MainScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                AppController.getInstance(),  // context
                AppController.notifChannelId) // notification channel id
                .setSmallIcon(R.drawable.ic_honeydo_logo)
                .setContentTitle("testing-title")
                .setContentText("this is a notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);

        AppController.getInstance().nManager.notify(001, mBuilder.build());
    }
}