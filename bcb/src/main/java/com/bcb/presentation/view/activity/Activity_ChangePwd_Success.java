package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.event.BroadcastEvent;
import com.bcb.data.util.MyActivityManager;

import de.greenrobot.event.EventBus;

public class Activity_ChangePwd_Success extends Activity_Base {

	private static final String TAG = "Activity_ChangePwd_Success";

	private Button go;
	private boolean isLoginPwd, isForgetPwd;
	
	public static void launche(Context ctx, boolean isLoginPwd, boolean isForgetPwd) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_ChangePwd_Success.class);
		intent.putExtra("isLoginPwd", isLoginPwd);
		intent.putExtra("isForgetPwd", isForgetPwd);
		ctx.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_ChangePwd_Success.this);
		setBaseContentView(R.layout.activity_change_pwd_success);
		setLeftTitleVisible(false);
		setTitleValue("修改密码");
		isLoginPwd = getIntent().getBooleanExtra("isLoginPwd", true);
		isForgetPwd = getIntent().getBooleanExtra("isForgetPwd", true);
		
		init();
	}

	private void init() {
		go = (Button) findViewById(R.id.go);
		go.setOnClickListener(onClickListener);
		//登陆密码
		if (isLoginPwd) {
			if (isForgetPwd) {
				go.setText("立即登录");
			} else {
				go.setText("重新登录");
			}
		}
		//不是登陆密码，设置为返回账户
		else {
			go.setText("返回账户");
		}	
	}
	
    View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				
			case R.id.go:
				//判断是否属于登陆密码
				if (isLoginPwd) {
					//如果是忘记密码，则跳回到登陆页面
					if (isForgetPwd) {
						Activity_Login.launche(Activity_ChangePwd_Success.this);
						finish();
					}
					//否则
					else {
						toReLogin();
					}
				}
				//交易密码
				else {
					//发送交易密码出来
					sendBroardCast();
					App.mUserDetailInfo.setHasTradePassword(true);
					finish();
				}
				break;
				
			}
		}
	};
		
	// 监听返回键退出
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {		
			if (isLoginPwd) {
				if (!isForgetPwd) {
					toReLogin();
				}			
			}					
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	protected void toReLogin() {
		Intent intent = new Intent();		
		intent.setAction("com.bcb.logout");
		sendBroadcast(intent);
		Activity_Login.launche(Activity_ChangePwd_Success.this);
		//通知切换到首页
		EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.HOME));
		finish();
	}


	//将设置交易密码成功的广播发送出去
	private void sendBroardCast() {
		Intent intent = new Intent();
		intent.setAction("com.bcb.passwd.setted");
		sendBroadcast(intent);
	}
}