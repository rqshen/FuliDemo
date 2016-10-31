package com.bcb.presentation.view.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.GesturePatternLock.View.ContentView;
import com.bcb.presentation.view.custom.GesturePatternLock.View.Drawl;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cain on 16/3/2.
 */
public class Activity_Gesture_Lock extends Activity_Base {
	private FrameLayout body_layout;
	private ContentView content;
	private boolean isSettingPasswd = true;
	private boolean isSettingPasswd2 = false;
	private boolean isYZ = false;
	private TextView phone_description; //设置时的提示和输入手势密码时的手机号码
	private LinearLayout forgetpwd; //忘记密码
	private String password;    //暂存密码

	private AlertView alertView;

	public static void launche(Context context, boolean isSettingPasswd, boolean isYZ) {
		Intent intent = new Intent();
		intent.setClass(context, Activity_Gesture_Lock.class);
		intent.putExtra("isSettingPasswd", isSettingPasswd);
		intent.putExtra("isYZ", isYZ);
		context.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_lock);
		if (getIntent() != null) {
			isSettingPasswd = getIntent().getBooleanExtra("isSettingPasswd", true);
			isSettingPasswd2 = isSettingPasswd;
			isYZ = getIntent().getBooleanExtra("isYZ", false);
		}
		//清除上一次进入后台的时间
		clearEnterTime();

		phone_description = (TextView) findViewById(R.id.phone_description);
		if (isSettingPasswd) {
			phone_description.setText("绘制解锁图案");
		} else {
			phone_description.setText("请滑动输入密码");
		}

		//忘记密码
		forgetpwd = (LinearLayout) findViewById(R.id.layout_foget);
		//表示设置手势密码
		if (isSettingPasswd) {
			forgetpwd.setVisibility(View.GONE);
		}
		//表示需要输入手势密码
		else {
			forgetpwd.setVisibility(View.VISIBLE);
			forgetpwd.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					UmengUtil.eventById(Activity_Gesture_Lock.this, R.string.gesture_forget);
					gotoLoginPageView();

				}
			});
		}

		//手势密码的父容器
		body_layout = (FrameLayout) findViewById(R.id.body_layout);

		// 初始化一个显示各个点的viewGroup
		LogUtil.i("bqt", "【是否是设置密码】" + isSettingPasswd);
		if (isSettingPasswd) {//设置手势密码
			//设置手势密码时的构造器
			content = new ContentView(this, new Drawl.GestureCallBack() {
				@Override
				public void checkedSuccess() {
					if (isSettingPasswd) {
						Toast.makeText(Activity_Gesture_Lock.this, "密码设置成功!", Toast.LENGTH_SHORT).show();
						isSettingPasswd = false;
						App.saveUserInfo.setGesturePassword(password);
					}

					Intent intent = new Intent();
					intent.putExtra("SettingGestureSuccess", true);
					setResult(1, intent);
					finish();
				}

				@Override
				public void checkedFail() {
					if (isSettingPasswd) {
						phone_description.setTextColor(getResources().getColor(R.color.red));
						phone_description.setText("与上次绘制不一致，请重新绘制");
					}
				}

				@Override
				public void settingPasswdSuccess(StringBuilder stringBuilder, boolean settingPasswdStatus) {
					if (settingPasswdStatus) {
						isSettingPasswd = settingPasswdStatus;
						phone_description.setTextColor(getResources().getColor(R.color.txt_gray));
						phone_description.setText("再次绘制解锁图案");
						password = stringBuilder.toString();
					}else{
					phone_description.setTextColor(getResources().getColor(R.color.red));
					phone_description.setText("请连接至少4个点");
				}}
			});
		} else {//清除手势密码

			//输入手势密码的构造器

			content = new ContentView(this, App.saveUserInfo.getGesturePassword(), new Drawl.GestureCallBack() {
				@Override
				public void checkedSuccess() {
					//清除上一次退到后台时保存的时间
					clearEnterTime();
					if (!isYZ) {
						App.saveUserInfo.setGesturePassword("");
						LogUtil.i("bqt", "【Activity_Gesture_Lock】【onCreate】清除手势密码");
					} else LogUtil.i("bqt", "【验证手势密码】" + "，时间" + new SimpleDateFormat("mm-ss-S").format(new Date()));
					finish();
				}

				@Override
				public void checkedFail() {
					phone_description.setTextColor(getResources().getColor(R.color.red));
					phone_description.setText("输入密码错误");
					clearEnterTime();
				}

				@Override
				public void settingPasswdSuccess(StringBuilder stringBuilder, boolean settingPasswdStatus) {
//					if (!settingPasswdStatus) {
						phone_description.setTextColor(getResources().getColor(R.color.red));
						phone_description.setText("请连接至少4个点");
//					}
				}
			});
		}

		isSettingPasswd = false;
		//设置手势解锁显示到哪个布局里面
		content.setParentView(body_layout);
	}

	@Override
	public void onBackPressed() {
		Intent intent = getIntent();
		LogUtil.i("bqt", "【设置手势密码】" + isSettingPasswd2);
		if (intent != null && intent.getBooleanExtra("isCanBack", false) || isSettingPasswd2) {
			finish();
		} else {
			showExitAlertView();
		}
	}
	//提示是否退出APP
	private void showExitAlertView() {
		AlertView.Builder ibuilder = new AlertView.Builder(this);
		ibuilder.setTitle("提示");
		ibuilder.setMessage("确定要退出福利金融吗?");
		ibuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertView.dismiss();
				alertView = null;
				finish();
				MyActivityManager.getInstance().finishAllActivity();
				App.instance.activity_main.finish();
//				android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
//				System.exit(0);
				ActivityManager am = (ActivityManager)getSystemService (Context.ACTIVITY_SERVICE);
				am.killBackgroundProcesses(getPackageName());
			}
		});
		ibuilder.setNegativeButton("取消", null);
		alertView = ibuilder.create();
		alertView.show();
	}
	//退出登录
	private void gotoLoginPageView() {
		AlertView.Builder ibuilder = new AlertView.Builder(this);
		ibuilder.setTitle("提示");
		ibuilder.setMessage("退出账号可以解除密码保护");//，退出和取消按钮，点击退出退出当前账号，点击取消则弹出框消失，留在手势密码界面
		ibuilder.setNegativeButton("取消", null);
		ibuilder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				alertView.dismiss();
				alertView = null;
				MyActivityManager myActivityManager = MyActivityManager.getInstance();
				myActivityManager.finishAllActivity();
				App.saveUserInfo.removeGesturePassword();
				/* 清空当前用户的信息 */
				App.saveUserInfo.clear();
				App.mUserWallet = null;
				App.mUserDetailInfo = null;
				App.viewJoinBanner = true;
				Activity_Login.launche(Activity_Gesture_Lock.this);
				sendBroadcast(new Intent("com.bcb.logout.success"));
				//销毁当前页面
				finish();
			}
		});
		alertView = ibuilder.create();
		alertView.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isYZ&&(App.saveUserInfo.getGesturePassword().isEmpty()|| App.saveUserInfo.getAccess_Token() == null)) {
			finish();
		}
	}
}