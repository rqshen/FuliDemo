package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.network.UrlsTwo;
import com.bcb.module.myinfo.financial.financialdetail.projectdetail.ProjectDetailActivity;

public class Activity_Open_Account extends Activity_Base {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView(R.layout.activity_open_account);
		setTitleValue("资金托管");
		setLeftTitleVisible(true);
		setRightBtnVisiable(View.VISIBLE);
		setRightTitleValue("关于汇付", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ProjectDetailActivity.launche(Activity_Open_Account.this, "关于汇付", UrlsTwo.UrlAboutHF);
			}
		});
		findViewById(R.id.tv_open).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Activity_Open_Account.this, Activity_Open_Account2.class));
			}
		});
	}
}