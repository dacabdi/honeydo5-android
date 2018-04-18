package com.honeydo5.honeydo.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.honeydo5.honeydo.R;
import com.honeydo5.honeydo.app.AppController;
import com.honeydo5.honeydo.app.MainScreenActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!AppController.getInstance().muteNotifications) {
            int id = intent.getIntExtra("id", 0);
            String name = intent.getStringExtra("name");
            String desc = intent.getStringExtra("desc");

            Intent destIntent = new Intent(context, MainScreenActivity.class);
            destIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, destIntent, 0);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    AppController.getInstance(),  // context
                    AppController.notifChannelId) // notification channel id
                    .setSmallIcon(R.drawable.ic_honeydo_logo)
                    .setContentTitle(name)
                    .setContentText(desc)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(uri);

            AppController.getInstance().nManager.notify(id, mBuilder.build());
        }
    }
}