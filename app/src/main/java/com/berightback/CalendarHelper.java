package com.berightback;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;

public class CalendarHelper {

    //Remember to initialize this activityObj first, by calling initActivityObj(this) from
//your activity
    private static final String TAG = "CalendarHelper";
    public static final int CALENDARHELPER_PERMISSION_REQUEST_CODE = 99;


    public static void MakeNewCalendarEntry(Context caller, String title, String description, String location, long startTime, long endTime, boolean allDay, boolean hasAlarm, int calendarId, int selectedReminderValue) {
//        MainActivity.getInstance().drivingEnable.setChecked(false);
//        MainActivity.getInstance().enableDriving(false);



        ContentResolver cr = caller.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startTime);
//        values.put(Events.DURATION, "P3600S");

//        values.put(Events.RDATE, endTime); // when the event should stop recurring
//        values.put(Events.RRULE, "FREQ=WEEKLY;COUNT=10;WKST=SU");

        values.put(Events.DTEND, endTime);
        values.put(Events.TITLE, title);
        values.put(Events.DESCRIPTION, description);
        values.put(Events.CALENDAR_ID, calendarId);
        values.put(Events.STATUS, Events.STATUS_CONFIRMED);


        if (allDay) {
            values.put(Events.ALL_DAY, true);
        }

        if (hasAlarm) {
            values.put(Events.HAS_ALARM, true);
        }


        //Get current timezone
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
//        Log.i(TAG, "Timezone retrieved=>" + TimeZone.getDefault().getID());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(caller, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                //requestCalendarReadWritePermission(caller);
                return;
            }
        }
        Uri uri = cr.insert(Events.CONTENT_URI, values);
//        Log.i(TAG, "Uri returned=>" + uri.toString());
        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());

        if (hasAlarm) {
            ContentValues reminders = new ContentValues();
            reminders.put(Reminders.EVENT_ID, eventID);
            reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
            reminders.put(Reminders.MINUTES, 0);
            Uri uri2 = cr.insert(Reminders.CONTENT_URI, reminders);
        }

        int repeatToRemindAfter = 10 / 3;
//
        if (hasAlarm) {
            ContentValues reminders = new ContentValues();
            reminders.put(Reminders.EVENT_ID, eventID);
            reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
            reminders.put(Reminders.MINUTES, -repeatToRemindAfter);
            Uri uri2 = cr.insert(Reminders.CONTENT_URI, reminders);
        }

        int repeatToReminder = repeatToRemindAfter + repeatToRemindAfter;

        if (hasAlarm) {
            ContentValues reminders = new ContentValues();
            reminders.put(Reminders.EVENT_ID, eventID);
            reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
            reminders.put(Reminders.MINUTES, -repeatToReminder);
            Uri uri2 = cr.insert(Reminders.CONTENT_URI, reminders);
        }

        int repeatToReminder1 = repeatToRemindAfter + repeatToRemindAfter + 2;

        if (hasAlarm) {
            ContentValues reminders = new ContentValues();
            reminders.put(Reminders.EVENT_ID, eventID);
            reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
            reminders.put(Reminders.MINUTES, -repeatToReminder1);
            Uri uri2 = cr.insert(Reminders.CONTENT_URI, reminders);
        }

    }


    public static void deleteEvent(Context context) {
        ContentResolver cr = context.getContentResolver();
        Uri EVENTS_URI = Uri.parse("content://com.android.calendar/" + "events");
        deleteEvent(cr, EVENTS_URI, 1);
    }

    private static void deleteEvent(ContentResolver resolver, Uri eventsUri, int calendarId) {
        Cursor cursor;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            cursor = resolver.query(eventsUri, new String[]{"_id"}, "Calendars_id=" + calendarId, null, null);
        } else {
            cursor = resolver.query(eventsUri, new String[]{"_id"}, "calendar_id=" + calendarId, null, null);
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long eventId = cursor.getLong(cursor.getColumnIndex("_id"));
                resolver.delete(ContentUris.withAppendedId(eventsUri, eventId), null, null);
            }
            cursor.close();
        }

    }

    public static void requestCalendarReadWritePermission(Context caller) {
        List<String> permissionList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(caller, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_CALENDAR);

        }

        if (ContextCompat.checkSelfPermission(caller, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_CALENDAR);

        }

        if (permissionList.size() > 0) {
            String[] permissionArray = new String[permissionList.size()];

            for (int i = 0; i < permissionList.size(); i++) {
                permissionArray[i] = permissionList.get(i);
            }

            ActivityCompat.requestPermissions((Activity) caller,
                    permissionArray,
                    CALENDARHELPER_PERMISSION_REQUEST_CODE);
        }

    }

    public static Hashtable listCalendarId(Context c) {

        if (haveCalendarReadWritePermissions(c)) {

            String projection[] = {"_id", "calendar_displayName"};
            Uri calendars;
            calendars = Uri.parse("content://com.android.calendar/calendars");

            ContentResolver contentResolver = c.getContentResolver();
            Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

            if (managedCursor.moveToFirst()) {
                String calName;
                String calID;
                int cont = 0;
                int nameCol = managedCursor.getColumnIndex(projection[1]);
                int idCol = managedCursor.getColumnIndex(projection[0]);
                Hashtable<String, String> calendarIdTable = new Hashtable<>();

                do {
                    calName = managedCursor.getString(nameCol);
                    calID = managedCursor.getString(idCol);
//                    Log.v(TAG, "CalendarName:" + calName + " ,id:" + calID);
                    calendarIdTable.put(calName, calID);
                    cont++;
                } while (managedCursor.moveToNext());
                managedCursor.close();

                return calendarIdTable;
            }

        }

        return null;

    }

    public static boolean haveCalendarReadWritePermissions(Context caller) {
        int permissionCheck = ContextCompat.checkSelfPermission(caller,
                Manifest.permission.READ_CALENDAR);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            permissionCheck = ContextCompat.checkSelfPermission(caller,
                    Manifest.permission.WRITE_CALENDAR);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }

        return false;
    }

}
