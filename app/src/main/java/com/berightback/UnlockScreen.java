package com.berightback;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

public class UnlockScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
//                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//        );


        setContentView(R.layout.activity_main2);

//        boolean unLockScreen = getIntent().getBooleanExtra("unlockscreen", false);
//
//        Toast.makeText(UnlockScreen.this, "" + unLockScreen, Toast.LENGTH_SHORT).show();
//
//        if (unLockScreen) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                getIntent().putExtra("unlockscreen", false);
                finish();
//                    finishAndRemoveTask();
//                    int pid = android.os.Process.myPid();
//                    android.os.Process.killProcess(pid);
//                    System.exit(0);
            }
        }, 500);
//        }


    }
}
