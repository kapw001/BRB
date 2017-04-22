package com.berightback;

import android.Manifest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.kyleduo.switchbutton.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final String LOGTAG = "Callmelater";
    private SharedPreferences sharedPreferences;
    private CallReciever receiver;
    private CCSpinner spinner1;
    private CCSpinner spinnerMsg;
    private RadioGroup radioGroup;

    private String[] mMsgrray;

    private ArrayList<String> addCustomUserMessages;

    private MyCustomAdapter adapter;
    private boolean avoidLoadingFirstTime = false;
    private ImageView update;
    private EditText customTextMsg;
    private SwitchButton remindSwitch;
    public SwitchButton drivingEnable;
    private ImageView done;
    private RelativeLayout reminderSpinerDesign;

    private String mPhoneNumber;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private static MainActivity mainActivity;

    DevicePolicyManager deviceManger;
    ActivityManager activityManager;
    ComponentName compName;
    private TextView drivingTxt;
    private RelativeLayout replaySpinerDesign;

    private TelephonyManager tMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


//        Settings.Secure.putString(getContentResolver(),
//                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, "com.berightback");
//        Settings.Secure.putString(getContentResolver(),
//                Settings.Secure.ACCESSIBILITY_ENABLED, "1");


        replaySpinerDesign = (RelativeLayout) findViewById(R.id.replaySpinerDesign1);
        drivingTxt = (TextView) findViewById(R.id.drivingTxt);

        deviceManger = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE);
        compName = new ComponentName(this, DeviceAdminLock.class);


        sharedPreferences = getSharedPreferences("myPrefs", this.MODE_PRIVATE);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.customactionbar);


//        getSupportActionBar().setIcon(R.drawable.actionbar_space_between_icon_and_title);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d3d3d3")));
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.app_name) + "</font>"));
//

        reminderSpinerDesign = (RelativeLayout) findViewById(R.id.reminderSpinerDesign);
        done = (ImageView) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("BeRightBack")
                        .setMessage("Your preferences have been set successfully.")
                        .setCancelable(true)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create().show();

                // Toast.makeText(MainActivity.this, "Data saved", Toast.LENGTH_SHORT).show();

            }
        });


        AndroidNetworking.initialize(getApplicationContext());
        tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED
                    ||
                    checkSelfPermission(Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED

                    ||
                    checkSelfPermission(Manifest.permission.WRITE_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED

                    || checkSelfPermission(Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_CALL_LOG)
                    != PackageManager.PERMISSION_GRANTED
                    ) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_SMS, Manifest.permission.READ_CALL_LOG},
                        0);

            }
        } else {
            mPhoneNumber = tMgr.getLine1Number();
        }


        //Toast.makeText(MainActivity.this, mPhoneNumber, Toast.LENGTH_SHORT).show();
        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    //Getting the registration token from the intent
                    String token = intent.getStringExtra("token");
                    AndroidNetworking.get("http://ec2-52-77-217-117.ap-southeast-1.compute.amazonaws.com/pushtest/insert.php")
                            .addQueryParameter("token", token)
                            .addQueryParameter("email", UserEmailFetcher.getEmail(getApplicationContext()))
                            .addQueryParameter("phonenumber", mPhoneNumber)
                            .setTag("test")
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {


                                    try {
                                        if (response.getString("success").equalsIgnoreCase("success")) {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putBoolean("registeredGCM", true);
                                            editor.commit();
//                                            Toast.makeText(MainActivity.this, "GCM Registered.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onError(ANError ANError) {
//                                    Toast.makeText(MainActivity.this, "GCM Error.", Toast.LENGTH_SHORT).show();
                                }
                            });

                    //Displaying the token as toast
//                    Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
//                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };


        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            if (!sharedPreferences.getBoolean("registeredGCM", false)) {
                Intent itent = new Intent(this, GCMRegistrationIntentService.class);
                startService(itent);
            }

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, 22);
        }

        final View controlsView = findViewById(R.id.controls);
        final SwitchButton serviceSwitch = (SwitchButton) findViewById(R.id.switch1);


        remindSwitch = (SwitchButton) findViewById(R.id.switch2);

        drivingEnable = (SwitchButton) findViewById(R.id.drivingEnable);


        remindSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableDisiableRemindSwitch(isChecked);
                showHideDonebuttonActionBar(true);
            }
        });
        if (sharedPreferences.getBoolean("remindEnabled", true)) {
            remindSwitch.setChecked(true);
        } else {
            remindSwitch.setChecked(false);
        }
        update = (ImageView) findViewById(R.id.update);
        customTextMsg = (EditText) findViewById(R.id.customizeTextMsg);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = customTextMsg.getText().toString();
                if (msg != null && msg.length() > 0) {
                    ArrayList<String> list = (ArrayList<String>) retriveCustomUserMessages();

                    boolean isThere = false;

                    for (int i = 0; i < list.size(); i++) {
                        if (msg.equalsIgnoreCase(list.get(i))) {
                            isThere = true;
                        }
                    }

                    if (isThere) {
                        Toast.makeText(MainActivity.this, "Already Added.", Toast.LENGTH_SHORT).show();
                    } else {
                        saveCustomUserArraylistMessages(msg);
                        customTextMsg.setText("");
                        update.setVisibility(View.GONE);
                    }


                } else {
//                    Toast.makeText(MainActivity.this, "Enter a message", Toast.LENGTH_SHORT).show();
                }
                showHideDonebuttonActionBar(true);
            }
        });


        customTextMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s != null && s.length() > 0) {

                    update.setVisibility(View.VISIBLE);

                } else {
                    update.setVisibility(View.GONE);
                }

            }
        });


        spinner1 = (CCSpinner) findViewById(R.id.spinner);
        spinnerMsg = (CCSpinner) findViewById(R.id.spinnerMsg);


        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);


        radioGroup.setOnCheckedChangeListener(new custonRadioCheckedListener());
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        mMsgrray = getResources().getStringArray(R.array.Msg);

        addCustomUserMessages = new ArrayList<>();
        adapter = new MyCustomAdapter(this, R.layout.spinnerlayout, addCustomUserMessages);

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerMsg.setAdapter(adapter);
        ArrayList<String> list = (ArrayList<String>) retriveCustomUserMessages();
        if (list == null) {
            for (int i = 0; i < mMsgrray.length; i++) {
                saveCustomUserArraylistMessages(mMsgrray[i]);
            }
        } else {
            for (int j = 0; j < list.size(); j++) {
                addCustomUserMessages.add(list.get(j));
            }
        }

        spinnerMsg.setSelection(sharedPreferences.getInt("msgpos", 0));
//        spinnerMsg.setSelection(1);
        adapter.notifyDataSetChanged();

//        spinnerMsg.setOnItemSelectedListener(new CustomSpinnerMsgOnItemSelectedListener());


        // Set preferences


        radioGroup.check(sharedPreferences.getInt("radio", R.id.whatsappRadio));
        if (sharedPreferences.getInt("remind", -1) == -1) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("remind", 0);
            editor.commit();
        } else {
            spinner1.setSelection(sharedPreferences.getInt("remind", 0));
        }
        if (!sharedPreferences.getBoolean("serviceEnabled", false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("serviceEnabled", false);
            serviceSwitch.setChecked(false);
            editor.commit();
            controlsView.setVisibility(View.INVISIBLE);
        } else {
            if (!isAccessibilityEnabled()) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Whatever...
                                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                startActivity(intent);
                            }
                        }).create();
                dialog.setTitle("Turn On");
                dialog.setMessage("Please Enable Accessibility service for BeRightBack to help you");

                dialog.show();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("serviceEnabled", false);
                serviceSwitch.setChecked(false);
                editor.commit();
                controlsView.setVisibility(View.INVISIBLE);
            } else {
                serviceSwitch.setChecked(true);
                controlsView.setVisibility(View.VISIBLE);
            }


        }

        drivingEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
//                    remindSwitch.setClickable(false);
                    if (!sharedPreferences.getBoolean("driving", false)) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("BeRightBack")
                                .setMessage("Are you sure want enable automatically send sms.")
                                .setCancelable(true)
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        enableDriving(false, true);
                                        enableDisableWhataApp();
                                        setTimeEnableWhenDriving(1);
//                                        showHideDonebuttonActionBar(true);

                                    }
                                })
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        enableDriving(true, true);
                                        enableDisableWhataApp();
                                        int pos = getResources().getStringArray(R.array.reninderDelays).length - 1;
                                        setTimeEnableWhenDriving(pos);
//                                        enableDisiableRemindSwitch(true);

                                        showHideDonebuttonActionBar(true);

                                    }
                                })
                                .create().show();
                    }
                } else {
                    enableDriving(false, true);
                    enableDisableWhataApp();
                    setTimeEnableWhenDriving(1);
//                    remindSwitch.setClickable(true);
                    showHideDonebuttonActionBar(true);
                }


            }
        });


        //UI controlls
        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (!isAccessibilityEnabled()) {
                        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever...
                                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                        startActivity(intent);

                                    }
                                }).create();
                        dialog.setTitle("Turn On");
                        //Todo set buttons in dialog navigate to settings accessibility screen
                        dialog.setMessage("Please Enable Accessibility service for BeRightBack to help you");
                        dialog.show();
                        serviceSwitch.setChecked(false);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("serviceEnabled", false);
                        editor.commit();
                        controlsView.setVisibility(View.INVISIBLE);
                        showHideDonebuttonActionBar(true);
                        return;
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("serviceEnabled", true);
                    editor.commit();
                    controlsView.setVisibility(View.VISIBLE);
                    ComponentName component = new ComponentName(MainActivity.this, CallReciever.class);

                    getPackageManager()
                            .setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                    PackageManager.DONT_KILL_APP);
                    showHideDonebuttonActionBar(true);
                } else {
                    controlsView.setVisibility(View.INVISIBLE);
                    ComponentName component = new ComponentName(MainActivity.this, CallReciever.class);

                    getPackageManager()
                            .setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                    PackageManager.DONT_KILL_APP);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("serviceEnabled", false);
                    editor.commit();
                    showHideDonebuttonActionBar(true);
                }

            }
        });
        checkIsReminderEnabled();

        showHideDonebuttonActionBar(false);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mPhoneNumber = tMgr.getLine1Number();
        }
    }

    private void enableDisiableRemindSwitch(boolean isChecked) {


//        if (!drivingEnable.isChecked()) {
        if (isChecked) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("remindEnabled", true);
            remindSwitch.setChecked(true);
            editor.commit();

        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("remindEnabled", false);
            remindSwitch.setChecked(false);
            editor.commit();
            //  CalendarHelper.deleteEvent(getApplicationContext());
        }
        checkIsReminderEnabled();
//        }

    }


    public void setTimeEnableWhenDriving(int pos) {
        //int pos getResources().getStringArray(R.array.reninderDelays).length - 1;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("remind", pos);
        editor.commit();
        spinner1.setSelection(sharedPreferences.getInt("remind", 0));
//        Toast.makeText(MainActivity.this, "" + pos, Toast.LENGTH_SHORT).show();
    }


    public void enableDriving(boolean isChecked, boolean startUp) {
        if (isChecked) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("driving", true);
            drivingEnable.setChecked(true);
            editor.commit();
            drivingTxt.setVisibility(View.VISIBLE);
            replaySpinerDesign.setVisibility(View.GONE);
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("driving", false);
            drivingEnable.setChecked(false);
            editor.commit();
            drivingTxt.setVisibility(View.GONE);
            replaySpinerDesign.setVisibility(View.VISIBLE);
        }
        if (startUp) {
            showHideDonebuttonActionBar(isChecked);
        }

    }


    private void showHideDonebuttonActionBar(boolean isTrue) {

        if (isTrue) {
            done.setVisibility(View.VISIBLE);
        } else {
            done.setVisibility(View.GONE);
        }

    }

    private void checkIsReminderEnabled() {
        if (remindSwitch.isChecked()) {
            reminderSpinerDesign.setVisibility(View.VISIBLE);
        } else {
            reminderSpinerDesign.setVisibility(View.GONE);
        }
    }

    boolean whatsAppIsThere = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean("driving", false)) {
            drivingEnable.setChecked(true);
//            enableDisableWhataApp(false);
        } else {
            drivingEnable.setChecked(false);
//            enableDisableWhataApp(true);
        }
        whatsAppIsThere = isPackageInstalled("com.whatsapp", this);

        enableDisableWhataApp();


        enableDriving(drivingEnable.isChecked(), false);


        AnalyticsApplication.getInstance().trackScreenView("CallMeBack Screen");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));

    }

    public void enableDisableWhataApp() {


        if (whatsAppIsThere && drivingEnable.isChecked()) {
            RadioButton whatsappRadio = (RadioButton) findViewById(R.id.whatsappRadio);
            whatsappRadio.setChecked(false);
            whatsappRadio.setClickable(false);
            whatsappRadio.setAlpha(.5f);
            whatsappRadio.setEnabled(false);
            radioGroup.setClickable(false);
            radioGroup.setEnabled(false);
//            whatsappRadio.setVisibility(View.GONE);
            RadioButton messagingRadio = (RadioButton) findViewById(R.id.messagingRadio);
            messagingRadio.setChecked(true);
        } else if (!whatsAppIsThere && drivingEnable.isChecked()) {
            RadioButton whatsappRadio = (RadioButton) findViewById(R.id.whatsappRadio);
            whatsappRadio.setChecked(false);
            whatsappRadio.setClickable(false);
            whatsappRadio.setAlpha(.5f);
            whatsappRadio.setEnabled(false);
            radioGroup.setClickable(false);
            radioGroup.setEnabled(false);
//            whatsappRadio.setVisibility(View.GONE);
            RadioButton messagingRadio = (RadioButton) findViewById(R.id.messagingRadio);
            messagingRadio.setChecked(true);

        } else if (!whatsAppIsThere && !drivingEnable.isChecked()) {
            RadioButton whatsappRadio = (RadioButton) findViewById(R.id.whatsappRadio);
            whatsappRadio.setChecked(false);
            whatsappRadio.setClickable(false);
            whatsappRadio.setAlpha(.5f);
            whatsappRadio.setEnabled(false);
            radioGroup.setClickable(false);
            radioGroup.setEnabled(false);
//            whatsappRadio.setVisibility(View.GONE);
            RadioButton messagingRadio = (RadioButton) findViewById(R.id.messagingRadio);
            messagingRadio.setChecked(true);

        } else if (whatsAppIsThere && !drivingEnable.isChecked()) {
//            findViewById(R.id.whatsappRadio).setVisibility(View.VISIBLE);
            RadioButton whatsappRadio = (RadioButton) findViewById(R.id.whatsappRadio);
//            whatsappRadio.setChecked(true);
            whatsappRadio.setClickable(true);
            whatsappRadio.setEnabled(true);
            whatsappRadio.setAlpha(1f);
            radioGroup.setClickable(true);
            radioGroup.setEnabled(true);
            radioGroup.check(sharedPreferences.getInt("radio", R.id.whatsappRadio));
//            Toast.makeText(MainActivity.this, "" + sharedPreferences.getInt("radio", R.id.whatsappRadio), Toast.LENGTH_SHORT).show();
        }

    }

    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
//        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }


    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }


        @Override
        public View getDropDownView(final int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub

            View v = getCustomView(position, convertView, parent);

//            LayoutInflater inflater = getLayoutInflater();
//            View v = inflater.inflate(R.layout.simple_spinner_dropdown_item, parent, false);
            TextView label = (TextView) v.findViewById(R.id.text1);
            RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(5, 8, 0, 8);
            label.setLayoutParams(llp);
            label.setTextColor(Color.parseColor("#000000"));
            label.setSingleLine(false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    spinnerMsg.setSelection(position);
                    spinnerMsg.onDetachedFromWindow();

                    showHideDonebuttonActionBar(true);


                }
            });


            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Delete entry")
                            .setMessage("Are you sure you want to delete this entry?")
                            .setCancelable(true)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Whatever...
                                    clearAndAddCustomMsg(position);
                                    spinnerMsg.onDetachedFromWindow();
                                    showHideDonebuttonActionBar(true);
                                }
                            })
                            .create().show();
                    return false;
                }
            });

            return v;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }


        public View getCustomView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //return super.getView(position, convertView, parent);

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinnerlayout, parent, false);
            TextView label = (TextView) row.findViewById(R.id.text1);
//            RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            llp.setMargins(50, 0, 0, 0);
//            label.setLayoutParams(llp);
            label.setText(addCustomUserMessages.get(position));
            label.setSingleLine(false);

//            Toast.makeText(MainActivity.this, "First Time Called", Toast.LENGTH_SHORT).show();
            return row;
        }

    }


    public void saveCustomUserArraylistMessages(String msg) {
//        List<String> addMsg = new ArrayList<>();
        addCustomUserMessages.add(0, msg);

        Gson gson = new Gson();
        String json = gson.toJson(addCustomUserMessages);

//        LinkedHashSet<String> set = new LinkedHashSet<String>();
//        set.addAll(addCustomUserMessages);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("customusermsg", json);


        editor.putString("text", msg);
        editor.putInt("msgpos", 0);
        editor.commit();
        adapter.notifyDataSetChanged();
        spinnerMsg.setSelection(0);
    }


    public void reAddAfterDeteletedTheItem() {
        Gson gson = new Gson();
        String json = gson.toJson(addCustomUserMessages);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("customusermsg", json);
        editor.commit();

    }


    public void clearAndAddCustomMsg(int deleteposition) {
        addCustomUserMessages.remove(deleteposition);

        sharedPreferences.edit().remove("customusermsg").commit();

        reAddAfterDeteletedTheItem();

        adapter.notifyDataSetChanged();
        showHideDonebuttonActionBar(true);
    }

    public List<String> retriveCustomUserMessages() {

        Gson gson = new Gson();
        String json = sharedPreferences.getString("customusermsg", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        List<String> msg = gson.fromJson(json, type);
        if (msg != null) {
            Collections.reverseOrder();
        }

//        Set<String> set = sharedPreferences.getStringSet("customusermsg", null);
//        List<String> msg;

        if (json == null) {
            return null;
        } else {

//            for (int i = 0; i < msg.size(); i++) {
////                Log.e("Ta", "retriveCustomUserMessages: " + msg.get(i));
//            }
            return msg;
        }


    }

    public static MainActivity getInstance() {

        return mainActivity;
    }

    @Override
    public void onStart() {
        super.onStart();

        mainActivity = this;

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.berightback/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.berightback/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        String firstItem = String.valueOf(spinner1.getSelectedItem());

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("remind", pos);
            editor.commit();


            if (avoidLoadingFirstTime) {
                showHideDonebuttonActionBar(true);
            }
            avoidLoadingFirstTime = true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {

        }

    }


    /* function checks if accessibility is enabled or not*/
    public boolean isAccessibilityEnabled() {

        int accessibilityEnabled = 0;
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
//            Log.d(LOGTAG, "ACCESSIBILITY: " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
//            Log.d(LOGTAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
//            Log.d(LOGTAG, "***ACCESSIBILIY IS ENABLED***: ");


            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
//            Log.d(LOGTAG, "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
//                    Log.d(LOGTAG, "Setting: " + accessabilityService);
                    // Check if our app service is enabled
                    if (accessabilityService.equalsIgnoreCase("com.berightback/com.berightback.MyWindowAccessibilityService")) {
//                        Log.d(LOGTAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }

//            Log.d(LOGTAG, "***END***");
        } else {
//            Log.d(LOGTAG, "***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }


    private class custonRadioCheckedListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("radio", checkedId);
            editor.commit();
//            Toast.makeText(getApplicationContext(), "" + checkedId, Toast.LENGTH_SHORT).show();
            showHideDonebuttonActionBar(true);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rateus:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.berightback")));
                return true;
            case R.id.shareapp:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "BeRightBack");
                String sAux = "\nHi,\n" +
                        "\n" +
                        "I have use BRB to reject a call with a personalized message on WhatsApp and get reminded to call the dialer back.\n" +
                        "\n" +
                        "Doing so, These personalised messages will make your contacts realise how special they are. \n" +
                        "Also, it sends across the message that you do care about responding back to them, inspite of your being busy.\n" +
                        "\n" +
                        "Its Free..\n" +
                        "\n" +
                        "Download from\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.berightback \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
                return true;
            case R.id.help:
                startActivity(new Intent(this, HelpActivity1.class));
                return true;

            case R.id.privacypolicy:
                startActivity(new Intent(this, PrivacyPolicyActivity.class));
                return true;

            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
