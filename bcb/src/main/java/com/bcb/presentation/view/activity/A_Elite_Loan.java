package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bcb.R;
import com.bcb.presentation.view.custom.AlertView.DialogBQT;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 精英贷
 * 作者：baicaibang
 * 时间：2016/11/28 10:17
 */
public class A_Elite_Loan extends Activity_Base {
	@BindView(R.id.iv_brand) ImageView iv_brand;
	@BindView(R.id.iv_it) ImageView iv_it;
	@BindView(R.id.iv_signed) ImageView iv_signed;
	private String message;
	
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
		if (true) showDialog();
		else {
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
	
	private void showDialog() {
		message = "您已申请了我们公司的aaa，\n正在处理中，请勿重复申请。";
		DialogBQT diaolog = new DialogBQT(this) {
			@Override
			public void onSureClick(View v) {
				super.onSureClick(v);
				startActivity(new Intent(A_Elite_Loan.this, Activity_LoanList.class));
			}
		};
		diaolog.setTitleAndMessageAndIcon(null, message, R.drawable.icon_email);
		diaolog.setButtonText("查看我的借款", "返回", -1, 0xff999999);
		diaolog.show();
	}
}