package com.honeydo5.honeydo.util;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.honeydo5.honeydo.R;
import com.honeydo5.honeydo.app.NotificationActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationSystem extends BroadcastReceiver{
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)


    private static final String CHANNEL_ID = "ChannelDo";

    public static void initialize(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = "HoneyDo";
            String description = "Channel for HoneyDo";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel( CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager nm = (NotificationManager) context.getSystemService(
                    NOTIFICATION_SERVICE);
            nm.createNotificationChannel(mChannel);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        /*Intent intentNotification = new Intent(context, NotificationActivity.class);
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
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());*/




        NotificationCompat.Builder nb = (NotificationCompat.Builder) new NotificationCompat.Builder(context, CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
                .setContentTitle("NOTIFICATION")
                .setContentText("THIS IS A TEST");
        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(1, nb.build());

    }
}
