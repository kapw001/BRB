package com.berightback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * FIX ME: Across all the classes
 * 1. Move constants to String.xml
 * 2. Method level refactoring skipped. There may be some memory leaks.
 */
public class Main2Activity extends AppCompatActivity {

//    private static boolean firstTimeShowHelperScreen = true;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        sharedPreferences = getSharedPreferences("MyWelcome", Context.MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!sharedPreferences.getBoolean("firstTimeShowHelperScreen", true)) {
                    startActivity(new Intent(Main2Activity.this, SplashHelpScreenActivity.class));
                    Main2Activity.this.finish();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("firstTimeShowHelperScreen", false);
                    editor.commit();
//                    firstTimeShowHelperScreen = false;
                } else {
                    startActivity(new Intent(Main2Activity.this, MainActivity.class));
                    Main2Activity.this.finish();
                }
            }
        }, 2000);
    }
}
