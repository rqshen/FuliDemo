package com.bcb.presentation.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

/**
 * Created by cain on 16/3/15.
 */
public class MyPagerAdapter extends PagerAdapter {
    private Context mContext;

    private List<View> views;

    public MyPagerAdapter(Context context,List<View> views){
        this.mContext = context;
        this.views=views;
    }


    @Override
    public int getCount() {
		return views.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager)container).removeView(views.get(position % views.size()));
    }

    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView(views.get(position % views.size()), 0);
        return views.get(position % views.size());
    }
}
