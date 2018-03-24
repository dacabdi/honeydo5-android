package com.honeydo5.honeydo.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.honeydo5.honeydo.R;
import com.honeydo5.honeydo.app.NotificationActivity;

public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentNotification = new Intent(context, NotificationActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(intentNotification);

        PendingIntent pi = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentTitle("Test")
                .setContentText("This is a test")
                .setTicker("TEST ALERT")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher) //TODO: replace with our logo
                .setContentIntent(pi).build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
