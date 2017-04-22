package com.berightback;

import android.Manifest;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Random;


/**
 * Created by Think42Labs on 6/2/16.
 */

public class CallRecieverBackup extends BroadcastReceiver {
    private static String previousState = null;
    private ContentResolver mResolver;
    private SharedPreferences sharedPreferences;
    public static String message;
    private static String number;
    public static boolean doFillMessage = false;

    public Context context;

    DevicePolicyManager deviceManger;
    ActivityManager activityManager;
    ComponentName compName;


    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;

        deviceManger = (DevicePolicyManager) context.getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        compName = new ComponentName(context, DeviceAdminLock.class);

        sharedPreferences = context.getSharedPreferences("myPrefs", context.MODE_PRIVATE);
        mResolver = context.getApplicationContext()
                .getContentResolver();

        if (sharedPreferences.getBoolean("serviceEnabled", false)) {

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String msg = "Phone state " + state + " ";
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                CallRecieverBackup.previousState = TelephonyManager.EXTRA_STATE_RINGING;
                number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                msg += intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state) && TelephonyManager.EXTRA_STATE_RINGING.equals(CallRecieverBackup.previousState)) {

                Intent in = new Intent(context, UnlockScreen.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);

//                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                              in.putExtra("unlockscreen", true);
                context.startActivity(in);

//                Intent in = new Intent(context, MainActivity.class);
//                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////                in.putExtra("unlockscreen", true);
//                context.startActivity(in);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (context.checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(context, "Permission not granted", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        Cursor managedCursor = mResolver.query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " = ? ",
                                new String[]{number}, CallLog.Calls.DATE + " DESC");

                        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                        if (managedCursor.getCount() > 0) {
                            managedCursor.moveToFirst();
                            if (managedCursor.getInt(type) != CallLog.Calls.MISSED_TYPE) {


                                if (sharedPreferences.getBoolean("driving", false)) {
                                    message = context.getResources().getString(R.string.driving);
                                    String contactName = getContactName(number, context);
                                    message = contactName + ",\n" + message;
                                    String contactNum = number;
                                    if (contactNum.charAt(0) == '+') {
                                        contactNum = contactNum.substring(1, contactNum.length());
                                    }
                                    openSms(context, message, contactNum, contactName);

                                } else {

                                    message = sharedPreferences.getString("text", "I am busy right now will call u later!");
                                    String contactName = getContactName(number, context);
                                    message = contactName + ",\n" + message;

//                                    if (sharedPreferences.getBoolean("driving", false)) {
//                                        message = " ,\n" + context.getResources().getString(R.string.driving);
//                                    }

                                    deviceManger.removeActiveAdmin(compName);

                                    String contactNum = number;
                                    if (contactNum.charAt(0) == '+') {
                                        contactNum = contactNum.substring(1, contactNum.length());
                                    }
                                    try {
                                        if (sharedPreferences.getInt("radio", R.id.whatsappRadio) == R.id.whatsappRadio) {

                                            //Todo check for network connection and choose messenging as default
                                            openWhatsApp(context, contactNum + "@s.whatsapp.net", contactName, contactNum);
                                            doFillMessage = true;
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    doFillMessage = false;
                                                }
                                            }, 5000);
                                        } else {
                                            openSms(context, message, contactNum, contactName);
                                        }


                                    } catch (Exception e) {
//                                    Log.e("Call reciever", e.getMessage());
                                        Toast.makeText(context, "Whatsapp not installed", Toast.LENGTH_SHORT).show();
                                    }

                                    CallRecieverBackup.previousState = null;
                                }

                            }

                        }
                        if (managedCursor != null) {
                            managedCursor.close();
                        }

                    }
                }, 1000);

            }


        } else {
            CallRecieverBackup.previousState = null;
        }


    }

    private void openWhatsApp(Context ctx, String id, String contactName, String contactNum) {


        Cursor c = ctx.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Contacts.Data._ID}, ContactsContract.Data.DATA1 + "=?",
                new String[]{id}, null);
        if (c.moveToFirst()) {


            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.contacts/data/" + c.getString(0)));

            ctx.startActivity(i
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
//            Toast.makeText(ctx, "Called", Toast.LENGTH_SHORT).show();

            calendarNotificationCall(contactNum, contactName);

        } else {
            Toast.makeText(ctx, "Contact not added or not a whatsapp number", Toast.LENGTH_SHORT).show();
        }
        c.close();

        trackSendMsg("WhatsApp");

    }

    private void openSms(Context ctx, String message, String number, String contactName) {

//        Toast.makeText(ctx, "" + isValidLocalSMSSend(number), Toast.LENGTH_SHORT).show();

        if (isValidMobile(number) && isValidLocalSMSSend(number)) {

            Uri uri = Uri.parse("smsto:" + number);
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            String msg = message;


            if (contactName.equalsIgnoreCase("")) {
                msg = "Hi " + msg;     //msg.substring(2);
            }


            it.putExtra("sms_body", msg);
            ctx.startActivity(it
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            trackSendMsg("SMS");
            calendarNotificationCall(number, contactName);
        } else {
            Toast.makeText(ctx, "Cannot send sms to landline number", Toast.LENGTH_SHORT).show();
        }

    }

    public void sendSMS(String phoneNo, String msg) {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(context, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public void calendarNotificationCall(String contactNum, String contactName) {
        if (sharedPreferences.getBoolean("remindEnabled", true)) {

            String remind = context.getResources().getStringArray(R.array.reninderDelays)[sharedPreferences.getInt("remind", 0)];
            int minsToRemindAfter = Integer.parseInt(remind.split(" ")[0]);

            int mins = 1000 * 60 * minsToRemindAfter;
            String reminderName = contactNum + "";
            if (contactName.length() > 0) {
                reminderName = contactName;
            }
            Random r = new Random();
            int ra = r.nextInt(1001);
            Utils.scheduleNotification(context, Utils.getCustomNotification(context), contactNum, "Call " + reminderName + " back", "Call " + contactName + " back", mins, ra);
//            Utils.scheduleNotification(context, Utils.getCustomNotification(context, "Call " + reminderName + " back", "Call " + contactName + " back"), mins);
//            int repeatToRemindAfter = 10 + minsToRemindAfter;
//
//            long startTime = (new Date()).getTime() + (minsToRemindAfter * 1000 * 60);
//            long endTime = (new Date()).getTime() + (repeatToRemindAfter * 1000 * 60);
//            Hashtable calenders = CalendarHelper.listCalendarId(context);
//            if (calenders == null) {
//                return;
//            }
//            Enumeration enu = calenders.keys();
//            String reminderName = contactNum + "";
//            if (contactName.length() > 0) {
//                reminderName = contactName;
//            }
//            if (enu.hasMoreElements()) {
//                CalendarHelper.MakeNewCalendarEntry(context, "Call " + reminderName + " back", "Call " + contactName + " back", null, startTime, endTime, false, true, 1, 2);
//            }
        }
    }


    private boolean isValidLocalSMSSend(String phone) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber numberProto = null;
        TelephonyManager telephonyManager = null;
        try {
            numberProto = phoneUtil.parse(phone, "IN");
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            Toast.makeText(context, "" + numberProto.getCountryCode() + "   " + Iso2Phone.getPhone(telephonyManager.getSimCountryIso()), Toast.LENGTH_SHORT).show();
        } catch (NumberParseException e) {
            e.printStackTrace();
        }

        String incomingCallCode = numberProto.getCountryCode() + "";
        String currentPhoneCode = Iso2Phone.getPhone(telephonyManager.getSimCountryIso());

        if (incomingCallCode.equalsIgnoreCase(currentPhoneCode)) {
            return true;
        } else {
            return false;
        }
    }


    private boolean isValidMobile(String phone) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber numberProto = null;
        try {
            numberProto = phoneUtil.parse(phone, "IN");
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        PhoneNumberUtil.PhoneNumberType type = phoneUtil.getNumberType(numberProto);

        if (type.toString().equalsIgnoreCase("FIXED_LINE")) {
            return false;
        } else {
            return true;
        }

    }

    /*
     * Returns contact's id
     */
    private String getContactName(String phoneNumber, Context context) {
        ContentResolver mResolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));

        Cursor cursor = mResolver.query(uri, new String[]{
                ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);

        String contactId = "";

        if (cursor.moveToFirst()) {
            do {
                contactId = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            } while (cursor.moveToNext());
        }

        cursor.close();
        cursor = null;
        return contactId;
    }

    public void trackSendMsg(String msg) {


    }

}
