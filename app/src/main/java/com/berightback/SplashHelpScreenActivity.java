package com.berightback;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SplashHelpScreenActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    protected View view;
    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;

    private int[] imageResourceId = {
            R.drawable.shelp1,
            R.drawable.shelp2,
            R.drawable.shelp3,
            R.drawable.shelp4,
            R.drawable.shelp5,
            R.drawable.shelp6,
            R.drawable.shelp7,
            R.drawable.shelp8,
            R.drawable.shelp10
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

        setContentView(R.layout.splashscreenhelppage);

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


        TextView skip = (TextView) findViewById(R.id.skip);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(SplashHelpScreenActivity.this, MainActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                finish();
            }
        });


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

//        if (position == intro_images.getAdapter().getCount() - 1) {
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    startActivity(new Intent(SplashHelpScreenActivity.this, MainActivity.class));
//                    finish();
//                }
//            }, 1500);
//
//
//            //start next Activity
////            Toast.makeText(SplashHelpScreenActivity.this, "Go to Another Activity", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}