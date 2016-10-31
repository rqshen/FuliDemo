package com.bcb.presentation.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.umeng.analytics.MobclickAgent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Activity_Base extends Activity {

	public String token;

	private static final String FILE_NAME = "App_Enter_Background_Time";
	private static final String ENTER_BACKGROUND_KEY = "AppOnBackGround";

	private AlertView alertView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		token = TokenUtil.getEncodeToken(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_base);
	}

	@SuppressLint("NewApi")
	public void setBaseContentView(int layoutResId) {
		LinearLayout llContent = (LinearLayout) findViewById(R.id.content);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(layoutResId, null);
		llContent.addView(v);
	}

	@SuppressLint("NewApi")
	public void setLeftTitleVisible(boolean visible) {
		if (visible) {
			(findViewById(R.id.back_img)).setVisibility(View.VISIBLE);
			(findViewById(R.id.back_img)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		} else {
			(findViewById(R.id.back_img)).setVisibility(View.GONE);
		}
	}

	@SuppressLint("NewApi")
	public void setLeftTitleListener(View.OnClickListener onClickListener) {
		(findViewById(R.id.back_img)).setVisibility(View.VISIBLE);
		(findViewById(R.id.back_img)).setOnClickListener(onClickListener);
	}

	@SuppressLint("NewApi")
	public void setLeftTitleValue(String leftTitle, boolean dropdownVisible, View.OnClickListener onClickListener) {
		if (leftTitle.isEmpty()) {
			setLeftTitleVisible(true);
			(findViewById(R.id.dropdown)).setVisibility(View.GONE);
		} else {
			((TextView) findViewById(R.id.left_text)).setText(leftTitle);
			(findViewById(R.id.left_text)).setOnClickListener(onClickListener);
			if (dropdownVisible) {
				(findViewById(R.id.dropdown)).setVisibility(View.VISIBLE);
			} else {
				(findViewById(R.id.dropdown)).setVisibility(View.GONE);
			}
		}
	}

	@SuppressLint("NewApi")
	public void setTitleValue(String title) {
		((TextView) findViewById(R.id.title_text)).setText(title);
	}

	@SuppressLint("NewApi")
	public void setTitleVisiable(int visibility) {
		findViewById(R.id.layout_title).setVisibility(visibility);
	}

	@SuppressLint("NewApi")
	public void setRightTitleValue(String rightTitle, View.OnClickListener onClickListener) {
		((TextView) findViewById(R.id.right_text)).setText(rightTitle);
		(findViewById(R.id.right_text)).setOnClickListener(onClickListener);
	}

	@SuppressLint("NewApi")
	public void setRightBtnVisiable(int visibility) {
		findViewById(R.id.right_img).setVisibility(visibility);
	}

	@SuppressLint("NewApi")
	public void setRightBtnImg(int drawable, View.OnClickListener onClickListener) {
		((ImageView) findViewById(R.id.right_img)).setImageResource(drawable);
		(findViewById(R.id.right_img)).setOnClickListener(onClickListener);
	}

	@Override
	protected void onStart() {
		super.onStart();
		token = TokenUtil.getEncodeToken(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		//判断是否进入后台
		if (!isAppOnForeground()) {
			clearEnterTime();
			//获取当前时间
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());
			//存储退到后台的时间
			setAppOnBackgroundTime(formatter.format(curDate));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		token = TokenUtil.getEncodeToken(this);
		//判断APP返回前台，存在手势密码，并且退到后台时间超过一分钟，则提示
		if (isAppOnForeground() && isLongerThanOneMinute()) {
			//存在手势密码
			if (hasGesturePassword()) {
				Activity_Gesture_Lock.launche(Activity_Base.this, false, true);
			}
			//如果不存在手势密码，并且是第一次登陆，则提醒去设置手势密码
			else if (App.saveUserInfo.getAccess_Token() != null && App.saveUserInfo.isFirstLogin()) {
				App.saveUserInfo.setFirstLogin(false);
				AlertView.Builder ibuilder = new AlertView.Builder(this);
				ibuilder.setTitle("是否设置手势密码？");
				ibuilder.setMessage("设置手势密码可以提高账户安全");
				ibuilder.setPositiveButton("立即设置", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						Activity_Gesture_Lock.launche(Activity_Base.this, true, true);
						startActivity(new Intent(Activity_Base.this, Activity_Gesture_Lock.class));
						alertView.dismiss();
						alertView = null;
					}
				});
				ibuilder.setNegativeButton("取消", null);
				alertView = ibuilder.create();
				alertView.show();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//退出页面使用向右退出
		overridePendingTransition(0, R.anim.push_right_out);
	}

	/**
	 * 程序是否在前台运行
	 *
	 * @return
	 */
	private boolean isAppOnForeground() {
		ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();

		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null) {
			return false;
		}
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否存在手势密码
	 */
	private boolean hasGesturePassword() {
		return !App.saveUserInfo.getGesturePassword().isEmpty();
	}

	/**
	 * 判断程序退到后台是否超过一分钟
	 */
	public boolean isLongerThanOneMinute() {
		//判断时间是否已经过了一分钟
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		Date appEnterBackgroundTime = null;
		try {
			if (!getAppOnBackgroundTIme().isEmpty()) {
				//需要将当前时间转成相应的格式，否则很可能会出现因格式不对等导致的时间计算不正确的情况
				String currentString = formatter.format(curDate);
				curDate = formatter.parse(currentString);
				appEnterBackgroundTime = formatter.parse(getAppOnBackgroundTIme());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (appEnterBackgroundTime != null && (curDate.getTime() / 1000 - appEnterBackgroundTime.getTime() / 1000) > 60) {
			return true;
		}
		return false;
	}

	/**
	 * 记录进入后台时间
	 *
	 * @param time
	 */
	private void setAppOnBackgroundTime(String time) {
		SharedPreferences sp = this.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		//缓存进入后台的时间
		editor.putString("AppOnBackGround", time);
		editor.commit();
	}

	/**
	 * 获取上一次进入后台的时间
	 *
	 * @return 时间
	 */
	private String getAppOnBackgroundTIme() {
		SharedPreferences sp = this.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		return sp.getString(ENTER_BACKGROUND_KEY, "");
	}

	/**
	 * 清除缓存的进入后台的时间
	 */
	public void clearEnterTime() {
		//清空SharedPreference
		SharedPreferences sp = this.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}
}
