package com.berightback;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by LENOVO on 8/17/2016.
 */
public class Utils {

//    private static int count = 0;

    public static void scheduleNotification(final Context context, final Notification notification, final String number, final String title, final String description, final int delay, final int random) {

        Intent snooze = new Intent(context, NotificationPublisher.class);
        snooze.setAction("com.berightback.SNOOZE");
        snooze.putExtra("number", number);
        snooze.putExtra("title", title);
        snooze.putExtra("notificationId", random);
        snooze.putExtra("description", description);
        PendingIntent snoozependingIntent = PendingIntent.getBroadcast(context, random, snooze, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent dismis = new Intent(context, NotificationPublisher.class);
        dismis.setAction("com.berightback.DISMIS");
        dismis.putExtra("number", number);
        dismis.putExtra("title", title);
        dismis.putExtra("notificationId", random);
        dismis.putExtra("description", description);
        PendingIntent dismispendingIntent = PendingIntent.getBroadcast(context, random, dismis, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent call = new Intent(context, NotificationPublisher.class);
        call.setAction("com.berightback.CALL");
        call.putExtra("number", number);
        call.putExtra("title", title);
        call.putExtra("notificationId", random);
        call.putExtra("description", description);
        PendingIntent callpendingIntent = PendingIntent.getBroadcast(context, random, call, PendingIntent.FLAG_UPDATE_CURRENT);


        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notificationview);
        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteViews.setTextViewText(R.id.titlenotify, title);
        remoteViews.setTextViewText(R.id.description, description);

        remoteViews.setOnClickPendingIntent(R.id.snooze, snoozependingIntent);
        remoteViews.setOnClickPendingIntent(R.id.dismiss, dismispendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.call, callpendingIntent);


        notification.contentView = remoteViews;

        // Add a big content view to the notification if supported.
        // Support for expanded notifications was added in API level 16.
        // (The normal contentView is shown when the notification is collapsed, when expanded the
        // big content view set here is displayed.)
        if (Build.VERSION.SDK_INT >= 16) {
            // Inflate and set the layout for the expanded notification view
            RemoteViews expandedView =
                    new RemoteViews(context.getPackageName(), R.layout.notificationview);

            expandedView.setTextViewText(R.id.titlenotify, title);
            expandedView.setTextViewText(R.id.description, description);

            expandedView.setOnClickPendingIntent(R.id.snooze, snoozependingIntent);
            expandedView.setOnClickPendingIntent(R.id.dismiss, dismispendingIntent);
            expandedView.setOnClickPendingIntent(R.id.call, callpendingIntent);
            notification.bigContentView = expandedView;
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent notificationIntent = new Intent(context, NotificationPublisher.class);
                notificationIntent.setAction("com.berightback.SENDNOTIFICATION");
                notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
                notificationIntent.putExtra("title", title);
                notificationIntent.putExtra("notificationId", random);
                notificationIntent.putExtra("description", description);
                notificationIntent.putExtra("number", number);
                notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
                context.sendBroadcast(notificationIntent);
            }
        }, delay);

//        count++;
//        Toast.makeText(context, "Called...   " + count, Toast.LENGTH_SHORT).show();

    }


    public static Notification getCustomNotification(Context context) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSound(alarmSound);
//        builder.setColor(Color.BLACK);
//        builder.setContentTitle(title);
//        builder.setContentText(description);
        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setAutoCancel(false);
//        builder.setContent(remoteViews);
//        builder.addAction(R.drawable.alarms, "Snooze", snoozependingIntent)
//                .addAction(R.drawable.dismis, "Dismiz", dismispendingIntent);
        return builder.build();
    }


}
