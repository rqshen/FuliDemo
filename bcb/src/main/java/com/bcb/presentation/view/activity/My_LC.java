package com.bcb.presentation.view.activity;

import android.content.Context;
import android.os.Bundle;

import com.bcb.R;

import butterknife.ButterKnife;

public class My_LC extends Activity_Base {

	//@BindView(R.id.layout_car) RelativeLayout layoutCar;
	private Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		setBaseContentView(R.layout.activity_my_lc);
		ButterKnife.bind(this);
		setLeftTitleVisible(true);
		setTitleValue("我的理财--没用");
	}
}
