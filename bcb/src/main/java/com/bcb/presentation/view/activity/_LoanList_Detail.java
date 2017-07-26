package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.old.BaseActivity1;
import com.bcb.util.MyActivityManager;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.adapter.MyFinancialStateAdapter;
import com.bcb.presentation.view.fragment._LoanDetailFragment1;
import com.bcb.presentation.view.fragment._RepaymentFragment1;

import java.util.ArrayList;

/**
 * Created by cain on 16/1/13.
 * 我的借款
 */
public class _LoanList_Detail extends BaseActivity1 {

    private String uniqueId;//项目编号
    private String assetCode;//资产编号

    //ViewPager
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    //借款详情、还款计划
    private TextView detailTextView, paymentTextView;

    //下面的白色横条
    private ImageView ivBottomLine;
    private int bottomLineWidth;
    private int currIndex = 0;
    private int offset = 0;
    private int position_one;

    public static void launche(Context context, String uniqueId, String assetCode) {
        Intent intent = new Intent();
        intent.putExtra("uniqueId", uniqueId);
        intent.putExtra("assetCode", assetCode);
        intent.setClass(context, _LoanList_Detail.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager.getInstance().pushOneActivity(_LoanList_Detail.this);
        uniqueId = getIntent().getStringExtra("uniqueId");
        assetCode = getIntent().getStringExtra("assetCode");
        setBaseContentView(R.layout.activity_loanlist_detail);
        setLeftTitleVisible(true);
        setTitleValue("我的借款");
        setupView();
        initWidth();
        setupViewPager();
    }

    //初始化界面元素
    private void setupView() {
        //借款详情、还款计划
        detailTextView = (TextView) findViewById(R.id.detail_TextView);
        detailTextView.setOnClickListener(new MyOnClickListener(0));
        paymentTextView = (TextView) findViewById(R.id.payment_textView);
        paymentTextView.setOnClickListener(new MyOnClickListener(1));
    }

    //设置下面的白色横条的长度
    private void initWidth() {
        ivBottomLine = (ImageView) findViewById(R.id.coupons_cursor);
        //获取像素宽度
        DisplayMetrics dmDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dmDisplayMetrics);
        int screenW = dmDisplayMetrics.widthPixels;
        //设置宽度
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)ivBottomLine.getLayoutParams();
        layoutParams.width = screenW / 2;
        ivBottomLine.setLayoutParams(layoutParams);
        bottomLineWidth = ivBottomLine.getLayoutParams().width;
        offset = (int) ((screenW / 2.0 - bottomLineWidth) / 2);
        position_one = (int) (screenW / 2.0);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ivBottomLine.getLayoutParams();
        params.leftMargin = offset;
        params.rightMargin = 0;
        ivBottomLine.setLayoutParams(params);
    }

    //初始化ViewPager
    private void setupViewPager() {
        mPager = (ViewPager) findViewById(R.id.loan_viewpager);
        //fragment
        fragmentsList = new ArrayList<Fragment>();
        Fragment detailFragment = new _LoanDetailFragment1(_LoanList_Detail.this, uniqueId);
        Fragment repaymentFragment = new _RepaymentFragment1(_LoanList_Detail.this, assetCode);
        fragmentsList.add(detailFragment);
        fragmentsList.add(repaymentFragment);

        //ViewPager
        mPager.setAdapter(new MyFinancialStateAdapter(getSupportFragmentManager(), fragmentsList));
        mPager.setCurrentItem(0);
        mPager.setOffscreenPageLimit(2);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }

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
                    detailTextView.setTextColor(getResources().getColor(R.color.white));
                    paymentTextView.setTextColor(0xaaffffff);
                    break;

                case 1:
                    detailTextView.setTextColor(0xaaffffff);
                    paymentTextView.setTextColor(getResources().getColor(R.color.white));
                    break;
            }
            //将当前位置设置为目标位置
            currIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(100);
            ivBottomLine.startAnimation(animation);
        }
    }
}