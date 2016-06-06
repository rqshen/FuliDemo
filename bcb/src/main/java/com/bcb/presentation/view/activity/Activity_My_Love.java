package com.bcb.presentation.view.activity;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.bcb.R;
import com.bcb.data.util.UIUtil;

/**
 * Created by Ray on 2016/6/6.
 *
 * @desc 我的聚爱
 */
public class Activity_My_Love extends Activity_Base_Fragment implements ViewPager.OnPageChangeListener{

    private PagerAdapter pagerAdapter;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setBaseContentView(R.layout.activity_my_love);
        setLeftTitleVisible(true);
        setTitleValue("我的聚爱");


    }

    private void init() {

//        // Initialize the ViewPager and set an adapter
//        ViewPager pager = (ViewPager) findViewById(R.id.pager);
//        pager.setAdapter(new TestAdapter(getSupportFragmentManager()));
//
//        // Bind the tabs to the ViewPager
//        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
//        tabs.setViewPager(pager);
//
//        tabs.setOnPageChangeListener(this);
//
//        //tab 宽度均分
//        tabs.setShouldExpand(true);
//        tabs.setDividerColor(Color.TRANSPARENT);
//        //设置选中的滑动指示
//        tabs.setIndicatorColor(Color.GREEN);
//        tabs.setIndicatorHeight(UIUtil.dp2px(this,48));
//        //设置背景颜色
//        tabs.setBackgroundColor(getResources().getColor(R.color.write));
//
//        viewpager.setAdapter(new FragmentPagterIconTabAdapter(getSupportFragmentManager()));
//        tabs.setOnPageChangeListener(mPagerChangerListener);
//        tabs.setViewPager(viewpager);


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
