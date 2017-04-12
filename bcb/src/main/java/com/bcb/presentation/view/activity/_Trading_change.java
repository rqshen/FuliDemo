package com.bcb.presentation.view.activity;

import android.graphics.Color;
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
import com.bcb.base.BaseActivity;
import com.bcb.utils.MyActivityManager;
import com.bcb.presentation.adapter.MyFragmentPagerAdapter;

import java.util.ArrayList;

/**
 * 1、债权变更
 */
public class _Trading_change extends BaseActivity implements View.OnClickListener {
	private ViewPager vp;
	private ArrayList<Fragment> fragmentsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().pushOneActivity(_Trading_change.this);
		setBaseContentView(R.layout.activity_trading_change);
		setLeftTitleVisible(true);
		setTitleValue("债权变更");
		initUnderLine();
		InitViewPager();
	}

	private void InitViewPager() {
		vp = (ViewPager) findViewById(R.id.vp);
		fragmentsList = new ArrayList<Fragment>();
		//	状态 1 转让中 0已完成
		fragmentsList.add(_Change_InFragment.newInstance(1));
		fragmentsList.add(_Change_InFragment.newInstance(0));

		vp.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
		vp.setCurrentItem(0);
		vp.setOffscreenPageLimit(2);
		vp.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	@Override
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

	private void initUnderLine() {
		unusedTextView = (TextView) findViewById(R.id.tv_unused);
		usedTextView = (TextView) findViewById(R.id.tv_used);
		unusedTextView.setOnClickListener(this);
		usedTextView.setOnClickListener(this);
		ivBottomLine = (ImageView) findViewById(R.id.red);
		bottomLineWidth = ivBottomLine.getLayoutParams().width;
		DisplayMetrics dmDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dmDisplayMetrics);
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