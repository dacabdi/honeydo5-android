package com.honeydo5.honeydo.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RunTasksServiceAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceIntent = new Intent("com.honeydo5.com.NotifyService");
            context.startService(serviceIntent);
        }
    }
}
