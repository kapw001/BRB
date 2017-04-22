package com.berightback;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by LENOVO on 7/25/2016.
 */
public class ViewPagerAdapter1 extends PagerAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private int[] imageArray;

    public ViewPagerAdapter1(Context mContext, int[] imageArray) {
        this.mContext = mContext;
        this.imageArray = imageArray;
    }

    @Override
    public int getCount() {
        return imageArray.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.pager_itemimage, null);

//

        ImageView image = (ImageView) itemView.findViewById(R.id.imagehelp);

        Picasso.with(mContext).load(imageArray[position]).into(image);

//        image.setImageResource(imageArray[position]);


        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((RelativeLayout) object);
        ((ViewPager) container).removeView((View) object);
    }
}