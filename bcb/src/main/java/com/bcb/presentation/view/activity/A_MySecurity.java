package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.module.login.LoginActivity;
import com.bcb.module.myinfo.balance.FundCustodianAboutActivity;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.projectdetail.ProjectDetailActivity;
import com.bcb.presentation.view.custom.AlertView.OpenSMRZDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class A_MySecurity extends Activity_Base {

	@BindView(R.id.layout_car) RelativeLayout layoutCar;
	@BindView(R.id.layout_security) RelativeLayout layoutSecurity;
	private Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		setBaseContentView(R.layout.activity_car);
		ButterKnife.bind(this);
		setLeftTitleVisible(true);
		setTitleValue("我的保险");
		if (MyApplication.mUserDetailInfo == null ||TextUtils.isEmpty(MyApplication.mUserDetailInfo.CarInsuranceMyOrderPage)){
			layoutCar.setVisibility(View.GONE);
		}
	}

	@OnClick({R.id.layout_car, R.id.layout_security})
	public void onClick(View view) {
		if (MyApplication.saveUserInfo.getAccess_Token() == null) LoginActivity.launche(ctx);
		switch (view.getId()) {
			case R.id.layout_car:
				if (MyApplication.mUserDetailInfo == null || TextUtils.isEmpty(MyApplication.mUserDetailInfo.CarInsuranceMyOrderPage)) {
					Toast.makeText(ctx, "网络异常，请刷新后重试", Toast.LENGTH_SHORT).show();
				} else {
					ProjectDetailActivity.launche(ctx, "我的车险", MyApplication.mUserDetailInfo.CarInsuranceMyOrderPage);
				}
				break;
			case R.id.layout_security:
				if (MyApplication.mUserDetailInfo == null || TextUtils.isEmpty(MyApplication.mUserDetailInfo.GroupInsuranceUrl)) {
					Toast.makeText(ctx, "网络异常，请刷新后重试", Toast.LENGTH_SHORT).show();
				} else if (MyApplication.mUserDetailInfo == null || !MyApplication.mUserDetailInfo.HasOpenCustody) {
					new OpenSMRZDialog(ctx) {
						@Override
						public void onClick() {
							super.onClick();
							startActivity(new Intent(ctx, FundCustodianAboutActivity.class));
						}
					}.show();
				} else {
					ProjectDetailActivity.launche(ctx, "员工团险", MyApplication.mUserDetailInfo.GroupInsuranceUrl);
				}
				break;
		}
	}
}
