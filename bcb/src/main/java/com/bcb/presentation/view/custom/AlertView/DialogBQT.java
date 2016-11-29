package com.bcb.presentation.view.custom.AlertView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;

public class DialogBQT extends Dialog implements OnClickListener {
	private TextView title, message;//标题和消息内容
	private ImageView iv_icon;//图标
	private Button cancel, middle, confirm;//3个按钮
	private LinearLayout line_left, line_right;//2条线

	/**
	 * 仿iOS风格的AlertView
	 */
	public DialogBQT(Context context) {
		this(context, R.style.alertviewstyle);
	}

	public DialogBQT(Context context, int theme) {
		super(context, theme);
		initView();
	}

	private void initView() {
		setContentView(R.layout.dialog_withicon);
		title = (TextView) findViewById(R.id.title);
		message = (TextView) findViewById(R.id.message);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		cancel = (Button) findViewById(R.id.cancel);
		middle = (Button) findViewById(R.id.middle);
		confirm = (Button) findViewById(R.id.confirm);
		line_left = (LinearLayout) findViewById(R.id.line_left);
		line_right = (LinearLayout) findViewById(R.id.line_right);
		cancel.setOnClickListener(this);
		middle.setOnClickListener(this);
		confirm.setOnClickListener(this);
	}

	//***************************************************常用设置***************************************

	/**
	 * 标题、消息内容、图标，为空时不显示，为-1时不显示
	 */
	public void setTitleAndMessageAndIcon(String titleString, String messageString, int res) {
		if (titleString == null) title.setVisibility(View.GONE);
		else title.setText(titleString);

		if (messageString == null) message.setVisibility(View.GONE);
		else message.setText(messageString);

		if (res == -1) iv_icon.setVisibility(View.GONE);
		else iv_icon.setImageResource(res);
	}

	/**
	 * 显示三两个按钮时，确认（右）和取消（左）文本及颜色，为-1时不设置
	 */
	public void setButtonText(String confirmString, String cancelString, int confirmColor, int cancelColor) {
		confirm.setText(confirmString);
		cancel.setText(cancelString);
		if (confirmColor != -1) confirm.setTextColor(confirmColor);
		if (cancelColor != -1) cancel.setTextColor(cancelColor);
	}

	/**
	 * 显示三个按钮时，把中间的按钮显示出来
	 */
	public void setMiddleButtonText(String text, int color) {
		middle.setVisibility(View.VISIBLE);
		line_right.setVisibility(View.VISIBLE);
		middle.setText(text);
		if (color != -1) middle.setTextColor(color);
	}

	/**
	 * 显示一个按钮时
	 */
	public void setOneButtonText(String text, int color) {
		cancel.setVisibility(View.GONE);
		line_left.setVisibility(View.GONE);
		confirm.setText(text);
		if (color != -1) confirm.setTextColor(color);
		confirm.setBackgroundResource(R.drawable.single_btn_select);
	}

	//***************************************************三个按钮的点击事件***************************************
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.confirm:
				dismiss();
				onSureClick(v);
				break;
			case R.id.cancel:
				dismiss();
				onCancleClick(v);
				break;
			case R.id.middle:
				dismiss();
				onMiddleClick(v);
				break;
			default:
				break;
		}
	}

	private void onMiddleClick(View v) {
	}

	public void onSureClick(View v) {
	}

	public void onCancleClick(View v) {
	}

	//***************************************************获取控件***************************************
	public TextView getTitle() {
		return title;
	}

	public TextView getMessage() {
		return message;
	}

	public ImageView getIv_icon() {
		return iv_icon;
	}

	public Button getCancel() {
		return cancel;
	}

	public Button getMiddle() {
		return middle;
	}

	public Button getConfirm() {
		return confirm;
	}

	public LinearLayout getLine_left() {
		return line_left;
	}

	public LinearLayout getLine_right() {
		return line_right;
	}
}

