package com.berightback;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by yasar on 15/2/17.
 */

public class ParallaxPageTransformer implements ViewPager.PageTransformer {

    public void transformPage(View view, float offset) {

        int pageWidth = view.getWidth();
        view.setTranslationY((float) ((1 - offset) * 0.32 * view.getWidth()) * offset);
        view.setRotation(360 * offset);
        view.setAlpha(1-offset);

//        if (position < -1) { // [-Infinity,-1)
//            // This page is way off-screen to the left.
//            view.setAlpha(0);
//
//        } else if (position <= 1) { // [-1,1]
//
//            if (position < 0) {
//                view.setTranslationX(-position * pageWidth);
//
//            } else {
//                view.setTranslationX(position * pageWidth);
//            }
//
////            view.setTranslationX(-position * pageWidth); //Half the normal speed
//
//        } else { // (1,+Infinity]
//            // This page is way off-screen to the right.
//            view.setAlpha(0);
//        }


    }
}