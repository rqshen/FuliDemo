
package com.bcb.module.discover.financialproduct;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.BaseActivity1;
import com.bcb.module.discover.financialproduct.adapter.MyFragmentPagerAdapter;
import com.bcb.util.MyActivityManager;

import java.util.ArrayList;

/**
 * 投资理财 列表
 */
public class InvestmentFinanceActivity extends BaseActivity1 implements View.OnClickListener {
    private ViewPager vp;
    private ArrayList<Fragment> fragmentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushOneActivity(InvestmentFinanceActivity.this);
        setBaseContentView(R.layout.activity_cp);
        setLeftTitleVisible(true);
        setTitleValue("投资理财");
        initUnderLine();
        InitViewPager();
    }

    private void InitViewPager() {
        vp = (ViewPager) findViewById(R.id.vp);
        fragmentsList = new ArrayList<Fragment>();
        fragmentsList.add(FinanceListFragment.newInstance(0));
        fragmentsList.add(FinanceListFragment.newInstance(1));
        fragmentsList.add(FinanceListFragment.newInstance(2));

        vp.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
        vp.setCurrentItem(0);
        vp.setOffscreenPageLimit(3);
        vp.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_wyb:
                vp.setCurrentItem(0);
                break;
            case R.id.tv_zxb:
                vp.setCurrentItem(1);
                break;
            case R.id.tv_zyb:
                vp.setCurrentItem(2);
                break;
            default:
                break;
        }
    }

    /**
     * 设置横线的点击事件并获取横线的长度
     */
    private TextView tvWYB, tvZXB, tvZYB;
    private int currIndex = 0;
    private int position_one;
    private ImageView ivBottomLine;

    private void initUnderLine() {
        tvWYB = (TextView) findViewById(R.id.tv_wyb);
        tvZXB = (TextView) findViewById(R.id.tv_zxb);
        tvZYB = (TextView) findViewById(R.id.tv_zyb);
        tvWYB.setOnClickListener(this);
        tvZXB.setOnClickListener(this);
        tvZYB.setOnClickListener(this);
        ivBottomLine = (ImageView) findViewById(R.id.red);
        DisplayMetrics dmDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dmDisplayMetrics);
        int screenW = dmDisplayMetrics.widthPixels;
        position_one = screenW / 3;
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            //根据当前位置和将要移动到的位置创建动画
            Animation animation = new TranslateAnimation(currIndex * position_one, arg0 * position_one, 0, 0);
            //设置字体颜色
            switch (arg0) {
                case 0:
                    setTextColor();
                    tvWYB.setTextColor(Color.RED);
                    break;
                case 1:
                    setTextColor();
                    tvZXB.setTextColor(Color.RED);
                    break;
                case 2:
                    setTextColor();
                    tvZYB.setTextColor(Color.RED);
                    break;
            }
            //将当前位置设置为目标位置
            currIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(100);
            ivBottomLine.startAnimation(animation);
        }

        //设置所有的字体颜色为灰色
        private void setTextColor() {
            tvWYB.setTextColor(Color.GRAY);
            tvZXB.setTextColor(Color.GRAY);
            tvZYB.setTextColor(Color.GRAY);
        }
    }
}