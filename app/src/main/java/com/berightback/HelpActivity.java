package com.berightback;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HelpActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    public static ArrayList<String> arrylist=new ArrayList<>();
    protected View view;
    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;

    private int[] imageResourceId = {
            R.drawable.help1,
            R.drawable.help2,
            R.drawable.help3
    };

    private ViewPagerAdapter1 mAdapter;
    private int currentPage = 0;

    private int NUM_PAGES = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // To make activity full screen.
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);

        arrylist.add("");

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setElevation(0);
//            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//            getSupportActionBar().setCustomView(R.layout.customactionbar);
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + "Features" + "</font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d3d3d3")));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setReference();


    }


    public void setReference() {
//        view = LayoutInflater.from(this).inflate(R.layout.activity_main);

        intro_images = (ViewPager) findViewById(R.id.pager_introduction);


        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);


        mAdapter = new ViewPagerAdapter1(this, imageResourceId);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.setOnPageChangeListener(this);
        setUiPageViewController();


//        final Handler handler = new Handler();
//
//        final Runnable update = new Runnable() {
//            public void run() {
//                if (currentPage == NUM_PAGES - 1) {
//                    currentPage = 0;
//                }
//                intro_images.setCurrentItem(currentPage++, true);
//            }
//        };
//
//
//        new Timer().schedule(new TimerTask() {
//
//            @Override
//            public void run() {
//                handler.post(update);
//            }
//        }, 100, 1500);


    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
//
//        if (position == intro_images.getAdapter().getCount() - 1) {
//            //start next Activity
//            Toast.makeText(HelpActivity.this, "Go to Another Activity", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}