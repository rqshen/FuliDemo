package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.util.MQCustomerManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/11/29 19:30
 */
public class Activity_Open_Account3 extends Activity_Base {
	
	@BindView(R.id.tv_title) TextView tv_title;//消息
	@BindView(R.id.tv_open) TextView tv_open;//更换手机号码登录
	@BindView(R.id.customer_service) TextView customer_service;//联系客服
	@BindView(R.id.rl_idcard) RelativeLayout rl_idcard;//隐藏身份证号

	public static void launche(Context ctx, String message) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Open_Account3.class);
		intent.putExtra("message", message);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView(R.layout.activity_open_account2);
		setTitleValue("资金托管");
		setLeftTitleVisible(true);
		ButterKnife.bind(this);

		tv_title.setText(getIntent().getStringExtra("message"));//您的身份信息已绑定手机号
		tv_open.setText("更换手机号码登录");
		rl_idcard.setVisibility(View.INVISIBLE);
		customer_service.setVisibility(View.VISIBLE);
	}
	
	@OnClick({R.id.customer_service, R.id.tv_open})
	public void click(View v) {
		switch (v.getId()) {
			case R.id.customer_service:
				MQCustomerManager.getInstance(this).showCustomer(null);
				break;
			case R.id.tv_open:
				Activity_Account_Setting.launche(this);
				break;
			default:
				break;
		}
	}
}