package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.utils.MyActivityManager;
import com.bcb.utils.PackageUtil;
import com.bcb.utils.ToastUtil;
import com.bcb.utils.TokenUtil;
import com.bcb.utils.UmengUtil;

import org.json.JSONException;
import org.json.JSONObject;

//设置交易密码
public class Activity_Setting_Pay_Pwd  extends Activity_Base {

	private static final String TAG = "Activity_Setting_Pay_Pwd";

	private TextView error_tips;
	private Button settingPasswd;
	
	private EditText newpwd, confirmpwd;

	//转圈提示
	private ProgressDialog progressDialog;

    private BcbRequestQueue requestQueue;
	
	public static void launche(Context ctx) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Setting_Pay_Pwd.class);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Setting_Pay_Pwd.this);
		setBaseContentView(R.layout.activity_setting_pay_pwd);
        setLeftTitleVisible(true);
		setTitleValue("设置福利金融交易密码");
		UmengUtil.eventById(Activity_Setting_Pay_Pwd.this, R.string.set_f_key);
		requestQueue = MyApplication.getInstance().getRequestQueue();
        init();
	}

	private void init() {
		newpwd = (EditText) findViewById(R.id.newpwd);
		confirmpwd = (EditText) findViewById(R.id.confirmpwd); 
		error_tips = (TextView) findViewById(R.id.error_tips);
		settingPasswd = (Button) findViewById(R.id.button_confirm);
		settingPasswd.setOnClickListener(onClickListener);
		
		newpwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (newpwd.getText().toString().length() != 6) {
					error_tips.setVisibility(View.VISIBLE);
					error_tips.setText("请输入6位新密码");
				} else {
					error_tips.setVisibility(View.GONE);
					error_tips.setText("");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
		confirmpwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (confirmpwd.getText().toString().length() != 6) {
					error_tips.setVisibility(View.VISIBLE);
					error_tips.setText("请输入6位确认密码");
				} else {
					error_tips.setVisibility(View.GONE);
					error_tips.setText("");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_confirm:
                if (judgePwd()) {
                	// 设置立即修改密码按钮为不可以点击
                	settingPasswd.setEnabled(false);
                	settingPasswd();
				}
				break;

			}
		}
	};

	//判断输入密码是否正确
	private boolean judgePwd() {
		if (newpwd.getText().toString().length() != 6) {
			error_tips.setVisibility(View.VISIBLE);
			error_tips.setText("请输入6位新密码");
			newpwd.requestFocus();
			newpwd.setSelection(newpwd.getText().toString().length());
			return false;
		}
		if (confirmpwd.getText().toString().length() != 6) {
			error_tips.setVisibility(View.VISIBLE);
			error_tips.setText("请输入6位确认密码");
			confirmpwd.requestFocus();
			confirmpwd.setSelection(confirmpwd.getText().toString().length());
			return false;
		}
		if (!newpwd.getText().toString().equals(confirmpwd.getText().toString())) {
			error_tips.setVisibility(View.VISIBLE);
			error_tips.setText("两次密码输入不一致");
			return false;
		}
		return true;
	}

	//设置密码
	private void settingPasswd() {
		JSONObject obj = new org.json.JSONObject();
		try {
			showProgressBar();
			obj.put("TradePassword", newpwd.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.SetPayPwd, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressBar();
                try {
                    // 设置立即修改密码按钮为可以点击
                    settingPasswd.setEnabled(true);

                    String message = response.getString("message");
                    if (PackageUtil.getRequestStatus(response, Activity_Setting_Pay_Pwd.this)) {
                        UmengUtil.eventById(Activity_Setting_Pay_Pwd.this, R.string.set_f_key_y);
                        ToastUtil.alert(Activity_Setting_Pay_Pwd.this, "交易密码设置成功");
                        //将本地的支付密码设置状态为true，表示已经设置了交易密码
                        if (MyApplication.mUserDetailInfo != null) {
                            MyApplication.mUserDetailInfo.setHasTradePassword(true);
                        }

                        Activity_ChangePwd_Success.launche(Activity_Setting_Pay_Pwd.this, false, false);
                        finish();
                    }else{
                        ToastUtil.alert(Activity_Setting_Pay_Pwd.this, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_Setting_Pay_Pwd.this, "网络异常，请稍后重试");
                // 设置立即修改密码按钮为可以点击
                settingPasswd.setEnabled(true);
                hideProgressBar();
            }
        });
        jsonRequest.setTag(BcbRequestTag.SetPayPasswordTag);
        requestQueue.add(jsonRequest);
	}
	


	/********************* 转圈提示 **************************/
	//显示转圈提示
	private void showProgressBar() {
		if(null == progressDialog) progressDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
		progressDialog.setMessage("正在设置交易密码....");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}
	//隐藏转圈提示
	private void hideProgressBar() {
		if(!isFinishing() && null != progressDialog && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}

}

