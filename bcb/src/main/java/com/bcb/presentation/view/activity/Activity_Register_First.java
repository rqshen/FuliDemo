package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.module.login.LoginActivity;
import com.bcb.utils.MQCustomerManager;
import com.bcb.utils.MyActivityManager;
import com.bcb.utils.RegexManager;
import com.bcb.utils.UmengUtil;

public class Activity_Register_First extends Activity_Base {

	private static final String TAG = "Activity_Register_First";

	private TextView error_tips, login;
	private EditText phone;
	private Button next;

	public static void launche(Context ctx) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Register_First.class);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
		MyActivityManager myActivityManager = MyActivityManager.getInstance();
		myActivityManager.pushOneActivity(Activity_Register_First.this);
		setBaseContentView(R.layout.activity_register_first);
		setLeftTitleListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				UmengUtil.eventById(Activity_Register_First.this, R.string.reg_back);
				finish();
			}
		});
		setTitleValue("注册");
//		setRightTitleValue("登录", new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				LoginActivity.launche(Activity_Register_First.this);
//				//销毁当前页面
//				finish();
//			}
//		});

		setRightTitleValue("联系客服", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MQCustomerManager.getInstance(Activity_Register_First.this).showCustomer(null);
			}
		});
		init();
		UmengUtil.eventById(this, R.string.reg);
	}

	private void init() {

		error_tips = (TextView) findViewById(R.id.error_tips);
		login = (TextView) findViewById(R.id.login);

		phone = (EditText) findViewById(R.id.phone);
		phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!RegexManager.isPhoneNum(phone.getText().toString())) {
//					error_tips.setVisibility(View.VISIBLE);
//					error_tips.setText("请输入正确的手机号码");
				} else {
//					error_tips.setVisibility(View.GONE);
//					error_tips.setText("");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		next = (Button) findViewById(R.id.button_confirm);
		next.setOnClickListener(onClickListener);
		login.setOnClickListener(onClickListener);

	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				//点击下一步，销毁当前页面
				case R.id.button_confirm:
					UmengUtil.eventById(Activity_Register_First.this, R.string.reg2);
					String phonenum = phone.getText().toString().trim();
					if (!checkNumIsValid(phonenum)) {
						break;
					}
					toNext();

					break;
				case R.id.login:
					startActivity(new Intent(Activity_Register_First.this, LoginActivity.class));
					break;
			}
		}
	};

	//点击下一步按钮
	private void toNext() {
		String inputPhone = phone.getText().toString().trim();
		Intent intent = new Intent(Activity_Register_First.this, Activity_Register_Next.class);
		intent.putExtra("phone", inputPhone);
		startActivity(intent);
		//销毁当前页面
	}
	
	private boolean checkNumIsValid(String str) {
		if (!RegexManager.isPhoneNum(str)) {
			Toast.makeText(Activity_Register_First.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
//			error_tips.setVisibility(View.VISIBLE);
//			error_tips.setText("请输入正确的手机号码");
			return false;
		} else {
//			error_tips.setVisibility(View.GONE);
//			error_tips.setText("");
			return true;
		}
	}

	//点击返回按键，销毁当前页面
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		UmengUtil.eventById(Activity_Register_First.this, R.string.reg_back);
		finish();
	}
}