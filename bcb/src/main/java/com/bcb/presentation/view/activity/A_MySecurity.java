package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
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
	}

	@OnClick({R.id.layout_car, R.id.layout_security})
	public void onClick(View view) {
		if (App.saveUserInfo.getAccess_Token() == null) Activity_Login.launche(ctx);
		switch (view.getId()) {
			case R.id.layout_car:
				if (App.mUserDetailInfo == null || TextUtils.isEmpty(App.mUserDetailInfo.CarInsuranceMyOrderPage)) {
					Toast.makeText(ctx, "网络异常，请刷新后重试", Toast.LENGTH_SHORT).show();
				} else {
					Activity_Browser.launche(ctx, "我的车险", App.mUserDetailInfo.CarInsuranceMyOrderPage);
				}
				break;
			case R.id.layout_security:
				if (App.mUserDetailInfo == null || TextUtils.isEmpty(App.mUserDetailInfo.GroupInsuranceUrl)) {
					Toast.makeText(ctx, "网络异常，请刷新后重试", Toast.LENGTH_SHORT).show();
				} else if (App.mUserDetailInfo == null || !App.mUserDetailInfo.HasOpenCustody) {
					new OpenSMRZDialog(ctx) {
						@Override
						public void onClick() {
							super.onClick();
							startActivity(new Intent(ctx, Activity_Open_Account.class));
						}
					}.show();
				} else {
					Activity_Browser.launche(ctx, "员工团险", App.mUserDetailInfo.GroupInsuranceUrl);
				}
				break;
		}
	}
}
