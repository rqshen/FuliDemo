package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.project.SimpleProjectDetail;
import com.bcb.data.util.MyActivityManager;
import com.bcb.presentation.view.custom.HorizontalProgressBarWithNumber;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * setTitleValue("产品详情"。setTitleValue("详情"。setTitleValue("项目详情"。setTitleValue("立即购买"。setTitleValue("立即申购"。
 */
public class Activity_CPXQ extends Activity_Base {

	@BindView(R.id.tv_rate) TextView tvRate;
	@BindView(R.id.tv_rate_add) TextView tvRateAdd;
	@BindView(R.id.top_amount) TextView topAmount;
	@BindView(R.id.right) TextView right;
	@BindView(R.id.tv_limite) TextView tvLimite;
	@BindView(R.id.pb) HorizontalProgressBarWithNumber pb;
	@BindView(R.id.tv_u1) TextView tvU1;
	@BindView(R.id.tv_u2) TextView tvU2;
	@BindView(R.id.tv_u3) TextView tvU3;
	@BindView(R.id.tv_d1) TextView tvD1;
	@BindView(R.id.tv_d2) TextView tvD2;
	@BindView(R.id.tv_d3) TextView tvD3;
	@BindView(R.id.tv_buy1) TextView tvBuy1;
	@BindView(R.id.ll_buy1) LinearLayout llBuy1;
	@BindView(R.id.tv_buy2) TextView tvBuy2;
	@BindView(R.id.ll_buy2) LinearLayout llBuy2;
	@BindView(R.id.tv_more) TextView tvMore;
	@BindView(R.id.layout_scrollview) ScrollView layoutScrollview;
	@BindView(R.id.tv_buy) TextView tvBuy;
	//标的数据
	private SimpleProjectDetail mSimpleProjectDetail;

	private String packageId = "";

	private int CouponType = 0;
	private int countDate = 0;

	//初始化******************************************************************************************
	//0正常标，1转让标，2福鸡包
	private int type = 0;

	public static void launche2(Context ctx, String pid, int type) {
		Intent intent = new Intent();
		intent.putExtra("pid", pid);
		intent.putExtra("type", type);
		intent.setClass(ctx, Activity_CPXQ.class);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance()
				.pushOneActivity(Activity_CPXQ.this);
		if (getIntent() != null) {
			packageId = getIntent().getStringExtra("pid");
			CouponType = getIntent().getIntExtra("CouponType", 0);
			type = getIntent().getIntExtra("type", 0);
		}
		setBaseContentView(R.layout.activity_cpxq);
		setTitleValue("产品详情");
		layout_title.setBackgroundColor(getResources().getColor(R.color.red));
		title_text.setTextColor(getResources().getColor(R.color.white));
		dropdown.setImageDrawable(getResources().getDrawable(R.drawable.right_more));
	}

	@OnClick({R.id.ll_buy1, R.id.ll_buy2, R.id.tv_more, R.id.tv_buy})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ll_buy1:
				break;
			case R.id.ll_buy2:
				break;
			case R.id.tv_more:
				break;
			case R.id.tv_buy:
				break;
		}
	}
}