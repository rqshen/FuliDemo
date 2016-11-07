package com.bcb.presentation.view.custom.GesturePatternLock;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/9/5 18:30
 */
public class GestureUtils {
	public static final String Gesture_FILE_NAME = "gesture";
	public static final String Gesture_KEY_NAME = "GesturePassword" + "|" + "";//可以为每个账户设置一个独立的手势密码
	private static final String ENTER_BACKGROUND_FILE_NAME = "App_Enter_Background_Time";
	private static final String ENTER_BACKGROUND_KEY_NAME = "AppOnBackGround";

	/**
	 * dp 转成为 px
	 */
	public static int dp2px(Context context, float dpValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
	}

	/**  获取屏幕宽  */
	public static int getScreenWidth(Context context) {
		DisplayMetrics metric = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}

	/**
	 * 获取手势密码
	 */
	public static String getGesturePassword(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Gesture_FILE_NAME, Context.MODE_PRIVATE);
		return sp.getString(Gesture_KEY_NAME, "");
	}

	/**
	 * 设置手势密码
	 */
	public static void setGesturePassword(Context context, String gesturePassword) {
		SharedPreferences sp = context.getSharedPreferences(Gesture_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(Gesture_KEY_NAME, gesturePassword);
		editor.commit();
	}

	/**
	 * 清除手势密码－－注意，这会清除所有账户设置的手势密码
	 */
	public static void clearGesturePassword(Context context, String gesturePassword) {
		SharedPreferences sp = context.getSharedPreferences(Gesture_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 判断是否存在手势密码
	 */
	public static boolean isHasGesturePassword(Context context) {
		return !getGesturePassword(context).isEmpty();//当且仅当 length() 为 0 时返回 true
	}

	/**
	 * 记录进入后台时间
	 */
	public static void setAppOnBackgroundTime(Context context, long time) {
		SharedPreferences sp = context.getSharedPreferences(ENTER_BACKGROUND_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(ENTER_BACKGROUND_KEY_NAME, time + "");
		editor.commit();
	}

	/**
	 * 获取上一次进入后台的时间
	 */
	public static long getAppOnBackgroundTIme(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ENTER_BACKGROUND_FILE_NAME, Context.MODE_PRIVATE);
		return sp.getLong(ENTER_BACKGROUND_KEY_NAME, 0);
	}

	/**
	 * 清除缓存的进入后台的时间
	 */
	public static void clearEnterTime(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ENTER_BACKGROUND_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 程序是否在前台运行
	 */
	public static boolean isAppOnForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = context.getApplicationContext().getPackageName();
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null) return false;
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) return true;
		}
		return false;
	}

	/**
	 * 判断程序退到后台是否超过指定秒
	 */
	public static boolean isLongerThanSomeSecond(Context context, int second) {
		long curTime = System.currentTimeMillis();
		long appEnterBackgroundTime = getAppOnBackgroundTIme(context);
		if (appEnterBackgroundTime != 0 && ((curTime - appEnterBackgroundTime) / 1000) >= second) return true;
		return false;
	}
}