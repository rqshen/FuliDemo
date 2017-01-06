package com.bcb.presentation.view.custom.AlertView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcb.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2017/1/6 14:34
 */
public class OpenSMRZDialog extends Dialog {
	@BindView(R.id.iv_close) ImageView ivClose;
	@BindView(R.id.tv_top) TextView tvTop;
	@BindView(R.id.button) Button button;

	public OpenSMRZDialog(Context context) {
		super(context, R.style.DialogTheme);
		initView();
	}

	private void initView() {
		setContentView(R.layout.open_smrz);
		ButterKnife.bind(this);
		setCanceledOnTouchOutside(false);
		ivClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	public void setValues(int visible, boolean cancleable, String tips) {
		ivClose.setVisibility(visible);
		tvTop.setText(tips);
		setCancelable(cancleable);
		setCanceledOnTouchOutside(cancleable);
	}

	public void setTVGravity(int gravity) {
		tvTop.setGravity(gravity);
	}

	@OnClick(R.id.button)
	public void onClick() {
	}
}
