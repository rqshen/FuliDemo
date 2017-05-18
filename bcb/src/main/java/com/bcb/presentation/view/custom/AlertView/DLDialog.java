package com.bcb.presentation.view.custom.AlertView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.bcb.R;
import com.bcb.module.login.LoginActivity;
import com.bcb.presentation.view.activity.Activity_Register_First;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2017/1/6 14:34
 */
public class DLDialog extends Dialog {
	@BindView(R.id.dl) Button dl;
	@BindView(R.id.zc) Button zc;


	public DLDialog(Context context) {
		super(context, R.style.DialogTheme);
		initView();
	}

	private void initView() {
		setContentView(R.layout.dz_dialog);
		ButterKnife.bind(this);
		setCanceledOnTouchOutside(false);
		dl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				LoginActivity.launche(getContext());
			}
		});
		zc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				Activity_Register_First.launche(getContext());
			}
		});
	}
}
