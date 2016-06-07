package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.bcb.R;
import com.bcb.data.util.UIUtil;
import com.bcb.presentation.view.fragment.Frag_Love_Apply;
import com.bcb.presentation.view.fragment.Frag_Love_Support;

/**
 * Created by Ray on 2016/6/6.
 *
 * @desc 我的聚爱
 */
public class Activity_My_Love extends Activity_Base_Fragment implements ViewPager.OnPageChangeListener{

    private MyPagerAdapter myPagerAdapter;
    private Fragment[] fragments;

    public static void launch(Context context){
        Intent intent = new Intent(context, Activity_My_Love.class);
        context.startActivity(intent);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setBaseContentView(R.layout.activity_my_love);
        setLeftTitleVisible(true);
        setTitleValue("我的聚爱");

        init();

    }

    private void init() {

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.my_love_viewpager);

        String[] titles = new String[]{"支持的项目","发起的项目"};
        Fragment fragmentSupport = new Frag_Love_Support();
        Fragment fragmentApply = new Frag_Love_Apply();

        fragments = new Fragment[]{fragmentSupport,fragmentApply};
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(),fragments,titles);
        pager.setAdapter(myPagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.my_love_tabs);
        tabs.setViewPager(pager);

        tabs.setOnPageChangeListener(this);

        //tab 宽度均分
        tabs.setShouldExpand(true);
        tabs.setDividerColor(Color.TRANSPARENT);
        //设置选中的滑动指示
        tabs.setIndicatorColor(Color.WHITE);
        tabs.setTextSize((int)getResources().getDimension(R.dimen.main_text_large));
        tabs.setTextColor(Color.WHITE);
        tabs.setIndicatorHeight(UIUtil.dp2px(this,3));
        //设置背景颜色
        tabs.setBackgroundColor(getResources().getColor(R.color.red));

        tabs.setOnPageChangeListener(this);
        tabs.setViewPager(pager);


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

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments;

        private String[] titles;

        public MyPagerAdapter(FragmentManager fm, Fragment[] fragments, String[] titles) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

    }
}
