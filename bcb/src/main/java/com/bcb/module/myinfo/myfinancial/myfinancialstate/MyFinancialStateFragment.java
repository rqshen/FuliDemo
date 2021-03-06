package com.bcb.module.myinfo.myfinancial.myfinancialstate;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.old.BaseFragment1;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.adapter.MyFinancialStateAdapter;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.MyFinancialListFragment;

import java.util.ArrayList;

/**
 * 投资理财的状态,
 * 持有中，已结束，ViewPager
 */
public class MyFinancialStateFragment extends BaseFragment1 implements View.OnClickListener {
    private Context ctx;
    public TextView ztbj;
    public TextView yjsy;
    private ViewPager vp;
    private ArrayList<Fragment> fragmentsList;
    private String Status;//	【 0稳赢，打包】【1涨薪宝，三标】
    private static String EXTRA_STATUS = "status";

    //******************************************************************************************

    /**
     * 构造时把传入的参数带进来，
     */
    public static MyFinancialStateFragment newInstance(String Status) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_STATUS, Status);
        MyFinancialStateFragment fragment = new MyFinancialStateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Status = bundle.getString(EXTRA_STATUS);
        }
    }
    //******************************************************************************************

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tzjl_1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.ctx = view.getContext();
        ztbj = (TextView) view.findViewById(R.id.ztbj);
        yjsy = (TextView) view.findViewById(R.id.yjsy);
        initUnderLine(view);
        InitViewPager(view);
    }

    private void InitViewPager(View view) {
        vp = (ViewPager) view.findViewById(R.id.vp);
        fragmentsList = new ArrayList<Fragment>();
        fragmentsList.add(MyFinancialListFragment.newInstance(Status, 1));
        fragmentsList.add(MyFinancialListFragment.newInstance(Status, 2));
        vp.setAdapter(new MyFinancialStateAdapter(getChildFragmentManager(), fragmentsList));
        vp.setCurrentItem(0);
        vp.setOffscreenPageLimit(2);
        vp.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_unused:
                vp.setCurrentItem(0);
                break;
            case R.id.tv_used:
                vp.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    //********************************************************下面的一条红线**********************************
    private TextView unusedTextView, usedTextView;
    private int currIndex = 0;
    private int bottomLineWidth;
    private int offset = 0;
    private int position_one;
    private ImageView ivBottomLine;

    private void initUnderLine(View view) {
        unusedTextView = (TextView) view.findViewById(R.id.tv_unused);
        usedTextView = (TextView) view.findViewById(R.id.tv_used);
        unusedTextView.setOnClickListener(this);
        usedTextView.setOnClickListener(this);
        ivBottomLine = (ImageView) view.findViewById(R.id.red);
        bottomLineWidth = ivBottomLine.getLayoutParams().width;
        DisplayMetrics dmDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dmDisplayMetrics);
        int screenW = dmDisplayMetrics.widthPixels;
        offset = (int) ((screenW / 2.0 - bottomLineWidth) / 2);
        position_one = (int) (screenW / 2.0);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ivBottomLine.getLayoutParams();
        params.leftMargin = offset;
        params.rightMargin = 0;
        ivBottomLine.setLayoutParams(params);
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
                    unusedTextView.setTextColor(Color.RED);
                    break;

                case 1:
                    setTextColor();
                    usedTextView.setTextColor(Color.RED);
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
            unusedTextView.setTextColor(Color.GRAY);
            usedTextView.setTextColor(Color.GRAY);
        }
    }
}