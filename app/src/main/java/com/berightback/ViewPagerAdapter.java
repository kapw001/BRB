package com.berightback;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by LENOVO on 7/25/2016.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<HelpModel> helpModelArrayList;

    public ViewPagerAdapter(Context mContext, ArrayList<HelpModel> helpModelArrayList) {
        this.mContext = mContext;
        this.helpModelArrayList = helpModelArrayList;
    }

    @Override
    public int getCount() {
        return helpModelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.pager_item, null);

//        View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item, container, false);
//
//        LinearLayout layout = (LinearLayout) itemView.findViewById(R.id.layout);

        HelpModel helpModel = helpModelArrayList.get(position);
        TextView quesition = (TextView) itemView.findViewById(R.id.quesition);
        quesition.setText(helpModel.getQuestion());
        quesition.setTextColor(Color.BLACK);

//        Toast.makeText(mContext, helpModel.getQuestion(), Toast.LENGTH_SHORT).show();

        TextView ans1 = (TextView) itemView.findViewById(R.id.ans1);
        ans1.setText(helpModel.getAns1());

        TextView ans2 = (TextView) itemView.findViewById(R.id.ans2);
        ans2.setText(helpModel.getAns2());

        TextView ans3 = (TextView) itemView.findViewById(R.id.ans3);
        ans3.setText(helpModel.getAns3());

        TextView ans4 = (TextView) itemView.findViewById(R.id.ans4);
        ans4.setText(helpModel.getAns4());

        TextView ans5 = (TextView) itemView.findViewById(R.id.ans5);
        ans5.setText(helpModel.getAns5());

//        layout.addView(quesition);
//        layout.addView(ans1);
//        layout.addView(ans2);
//        layout.addView(ans3);
//        layout.addView(ans4);
//        layout.addView(ans5);
//
//
//        container.addView(layout);
//        container.addView(ans1);
//        container.addView(ans2);
//        container.addView(ans3);
//        container.addView(ans4);
//        container.addView(ans5);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((RelativeLayout) object);
        ((ViewPager) container).removeView((View) object);
    }
}