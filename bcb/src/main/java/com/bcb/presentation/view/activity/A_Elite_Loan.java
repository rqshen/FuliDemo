package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bcb.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/11/28 10:17
 */
public class A_Elite_Loan extends Activity_Base {
	@BindView(R.id.iv_brand) ImageView iv_brand;
	@BindView(R.id.iv_it) ImageView iv_it;
	@BindView(R.id.iv_signed) ImageView iv_signed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView(R.layout.activity_elite_loan);
		ButterKnife.bind(this);
		setLeftTitleVisible(true);
		setTitleValue("精英贷");
		setRightTitleValue("我的借款", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(A_Elite_Loan.this, Activity_LoanList.class));
			}
		});
	}

	@OnClick({R.id.iv_brand, R.id.iv_it, R.id.iv_signed})
	public void onClickIv(View v) {
		switch (v.getId()) {
			case R.id.iv_brand:
				Activity_LoanRequest_Borrow.launche(A_Elite_Loan.this, 1);
				break;
			case R.id.iv_it:
				Activity_LoanRequest_Borrow.launche(A_Elite_Loan.this, 2);
				break;
			case R.id.iv_signed:
				Activity_LoanRequest_Borrow.launche(A_Elite_Loan.this, 3);
				break;
			default:
				break;
		}
	}
}
