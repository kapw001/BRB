package com.berightback;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class HelpActivity1 extends AppCompatActivity {

    TextView whatapptxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help1);
        if (getSupportActionBar() != null) {
//            getSupportActionBar().setElevation(0);
//            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//            getSupportActionBar().setCustomView(R.layout.customactionbar);
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + "Help" + "</font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d3d3d3")));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        whatapptxt = (TextView) findViewById(R.id.whatapptext);

//        String myHtml = "This will display an image to the right <img src='whatsappimag' />";

        final String img = String.format("<img src=\"%s\"/>", R.drawable.whatsappimag_opt);
        final String html = String.format("%s if you have selected ‘WhatsApp’ as your mode of messaging.", img);
        whatapptxt.setText(Html.fromHtml(html, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(final String source) {
                Drawable d = null;
                try {
                    d = getResources().getDrawable(Integer.parseInt(source));
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                } catch (Resources.NotFoundException e) {
                    Log.e("log_tag", "Image not found. Check the ID.", e);
                } catch (NumberFormatException e) {
                    Log.e("log_tag", "Source string not a valid resource ID.", e);
                }

                return d;
            }
        }, null));
//        whatapptxt.setText(Html.fromHtml(myHtml, new ImageGetter(), null));
    }

    private class ImageGetter implements Html.ImageGetter {

        public Drawable getDrawable(String source) {
            int id;

            id = getResources().getIdentifier(source, "drawable", getPackageName());

            if (id == 0) {
                // the drawable resource wasn't found in our package, maybe it is a stock android drawable?
                id = getResources().getIdentifier(source, "drawable", "android");
            }

            if (id == 0) {
                // prevent a crash if the resource still can't be found
                return null;
            } else {
                Drawable d = getResources().getDrawable(id);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        if (item.getItemId() == R.id.more) {
            Intent in = new Intent(this, HelpActivity.class);
            startActivity(in);
        }
        if (item.getItemId() == R.id.quicktour) {
            startActivity(new Intent(this, SplashHelpScreenActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.helpmenu, menu);//Menu Resource, Menu
        return true;
    }

}
