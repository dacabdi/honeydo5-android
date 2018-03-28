package com.honeydo5.honeydo.app;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.util.Log;

import com.honeydo5.honeydo.R;

public class SplashActivity extends HoneyDoActivity {

    CountDownTimer splashAnimationDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTag("SPLASHSCREEN");

        if (!isTaskRoot()
            && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
            && getIntent().getAction() != null
            && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return;
        }

        setContentView(R.layout.activity_splash);

        splashAnimationDelay = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                goToLogin();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        splashAnimationDelay.cancel();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void goToLogin() {
        Log.d(this.tag, "Go to login activity");
        Intent intent = new Intent(this, LoginScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
    }
}
