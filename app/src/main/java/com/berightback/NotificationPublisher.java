package com.berightback;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by LENOVO on 8/13/2016.
 */
public class NotificationPublisher
        extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    private SharedPreferences sharedPreferences;

    public void onReceive(Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences("myPrefs", context.MODE_PRIVATE);

//        if (sharedPreferences.getBoolean("driving", false)) {
//            MainActivity.getInstance().enableDriving(false, true);
//            MainActivity.getInstance().enableDisableWhataApp();
//            MainActivity.getInstance().setTimeEnableWhenDriving(1);
//        }


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (intent.getAction().equals("com.berightback.SNOOZE")) {
//            Toast.makeText(context, "Working snooze Toast", Toast.LENGTH_SHORT).show();
//
//            Log.e("Test", "Test snooze Ok");
            int id = intent.getIntExtra("notificationId", 0);
            notificationManager.cancel(id);

//            String remind = context.getResources().getStringArray(R.array.reninderDelays)[sharedPreferences.getInt("remind", 0)];
//            int minsToRemindAfter = Integer.parseInt(remind.split(" ")[0]);

            int mins = 1000 * 60 * 10;

            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            String number = intent.getStringExtra("number");
            Random r = new Random();
            int ra = r.nextInt(1000);

            Utils.scheduleNotification(context, Utils.getCustomNotification(context), number, title, description, mins, ra);


            closeNotificationStatusBar(context);

        } else if (intent.getAction().equals("com.berightback.DISMIS")) {
//            Toast.makeText(context, "Working Dismis Toast", Toast.LENGTH_SHORT).show();
//
//            Log.e("Test", "Test dismiz Ok");
            int id = intent.getIntExtra("notificationId", 0);
            notificationManager.cancel(id);
            closeNotificationStatusBar(context);

        } else if (intent.getAction().equals("com.berightback.SENDNOTIFICATION")) {


            Notification notification = intent.getParcelableExtra(NOTIFICATION);

//            int id = intent.getIntExtra(NOTIFICATION_ID, 0);


            int id = intent.getIntExtra("notificationId", 0);
            notificationManager.notify(id, notification);

        } else if (intent.getAction().equals("com.berightback.CALL")) {
            int id = intent.getIntExtra("notificationId", 0);
            notificationManager.cancel(id);

            String title = intent.getStringExtra("title");

            Intent callIntent = new Intent(Intent.ACTION_CALL);

//            String phoneNumber = title.replaceAll("Call ", "");
//
//            phoneNumber = phoneNumber.replaceAll(" back", "");
            String phoneNumber = intent.getStringExtra("number");

            callIntent.setData(Uri.fromParts("tel", phoneNumber.trim().substring(2), null));
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            context.startActivity(callIntent);
//            String description = intent.getStringExtra("description");
            closeNotificationStatusBar(context);

        }

    }

    private void closeNotificationStatusBar(Context context) {
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }
}
