package com.honeydo5.honeydo.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.honeydo5.honeydo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;



public class NotificationSystem extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    private static final String tag          = "NOTIF-ENG";
    private static final String CHANNEL_ID   = "HONEYDO5-NOTIF-CHANNEL";
    private static final String CHANNEL_NAME = "HoneyDo";
    private static final String desc         = "Channel for HoneyDo";

    private static Context notificationContext;
    private static NotificationManager notMag;
    private static NotificationChannel notChannel;
    private static AlarmManager alarmManager;

    public static void initialize(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationContext = context;

            // Create the NotificationChannel
            notChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notChannel.setDescription(desc);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notMag = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
            notMag.createNotificationChannel(notChannel);

            alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
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

        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
                .setContentTitle("NOTIFICATION")
                .setContentText("THIS IS A TEST");
        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(1, nb.build());

    }

    public static void setNotify(Calendar alarmDateTime, JSONObject task){
        Log.d(tag, "Setting notification.");

        try {
            // get relevant info to the notification
            boolean priority = task.getBoolean("priority");
            Log.d(tag, "Notification with priority=" + Boolean.toString(priority));

            // if priority, set a notification for five minutes before the
            // scheduled normal notification of the due task
            if(priority)
            {
                Log.d(tag, "Priority previous notification setup (for 5 mins before)...");

                // five minutes before
                alarmDateTime.add(alarmDateTime.MINUTE, -5);

                Intent notifyIntent = new Intent(notificationContext, NotificationSystem.class);

                PendingIntent pend = PendingIntent.getBroadcast(
                        notificationContext,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmDateTime.getTimeInMillis(), pend);

                // set back to actual date
                alarmDateTime.add(alarmDateTime.MINUTE, 5);
            }

            Log.d(tag, "Setting regular on task time notification.");

            Intent notifyIntent = new Intent(notificationContext, NotificationSystem.class);
            PendingIntent pend = PendingIntent.getBroadcast(
                    notificationContext,
                    0,
                    notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmDateTime.getTimeInMillis(), pend);

        } catch(JSONException e){
            // log and do a stack trace
            Log.e(tag, "Parsing task JSON for notification:" + e.getMessage());
            Log.getStackTraceString(e);
        }
    }
}


