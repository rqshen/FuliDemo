package com.bcb.presentation.view.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.data.util.UmengUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/** 启动的Activity */
public class Activity_Start_Up extends Activity {

	private static final String TAG = "Activity_Start_Up";

	private static Handler handler = null;
	//判断是否是第一次运行的
	public static final String PREFS_NAME = "FirstInstallationTime";
	public static final String FIRST_RUN = "first";
	public static final String LOCAL_PHONE = "local_phone";
	public static final String VERSION_CODE = "version_code";
	private boolean first;
	private LoginReceiver loginReceiver;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx=Activity_Start_Up.this;
		setContentView(R.layout.activity_startup);
//		requestUserWallet();
//		requestUserMessage();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = this.getWindow();
            //顶部状态栏
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //底部NavigationBar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

		//注册友盟
		UmengUtil.init(this);

		//注册广播，用于监听登陆成功的信息
		loginReceiver = new LoginReceiver();
		IntentFilter intentFilter = new IntentFilter("com.bcb.login.success");
		registerReceiver(loginReceiver, intentFilter);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {

				case 1:
                    Intent intent = new Intent();
                    intent.setClass(Activity_Start_Up.this, Activity_Main.class);
                    startActivity(intent);
                    finish();
					break;

				default:
					break;

				}

			}
		};
		new Thread(new MyThread()).start();

	}

	@Override
	protected  void onStart() {
		super.onStart();
		//第一次运行
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		first = settings.getBoolean(FIRST_RUN, true);
		//当前版本号
		int currentVersionCode = getVersionCode(Activity_Start_Up.this);
		int savedVersionCode = settings.getInt(VERSION_CODE, currentVersionCode);

		//第一次启动或者当前的VersionCode大于储存的VersionCode，则储存时间
		if (first || currentVersionCode > savedVersionCode) {
            App.viewJoinBanner = true;
			//存储第一次安装时间，包括升级
			setupFirstInstallation();
		}
		//否则比较时间是否大于7天
		else {
			compareTime();
		}
	}

	class MyThread implements Runnable {
		public void run() {
			try {
				Thread.sleep(1500);
				handler.sendEmptyMessage(0x1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//设置第一次运行的时间
	private void setupFirstInstallation() {
		//获取当前时间
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddhhmmss");
		Date curDate = new Date(System.currentTimeMillis());
		String time = formatter.format(curDate);
		//储存时间，以便判断加入公司的Banner消失的时间
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("firstInstallationTime", time);
		editor.commit();
	}

	//比较两个时间
	private void compareTime() {
		//获取当前时间
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddhhmmss");
		Date curDate = new Date(System.currentTimeMillis());
		String time = formatter.format(curDate);
		//获取第一次安装的时间
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String firstTime = settings.getString("firstInstallationTime", time);
		try {
			Date firstInstallationTime = formatter.parse(firstTime);
			if (((curDate.getTime() - firstInstallationTime.getTime())/1000/60/60/24) > 7) {
				App.viewJoinBanner = false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	//退到后台时，储存第一次安装的时间
	@Override
	protected void onStop() {
		super.onStop();
		//保存第一次安装的时间、版本号和手机号
		saveFirstTime();

	}

	//存储第一次安装的时间、VersionCode和手机号
	private void saveFirstTime() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		if (first) {
			//第一次登陆
			editor.putBoolean(FIRST_RUN, false);
			//存VersionCode
			editor.putInt(VERSION_CODE, getVersionCode(Activity_Start_Up.this));
			//存本地手机号码
			editor.putString(LOCAL_PHONE, App.saveUserInfo.getLocalPhone());
		}
		editor.commit();
	}

	//获取VersionCode
	private int getVersionCode(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	//注册广播，登陆成功之后，比较登陆手机号码是多少，如果两个手机号码不一样，则存储时间。
	class LoginReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			//获取登陆成功的广播
			if(intent.getAction().equals("com.bcb.login.success")) {
				//比较手机号码是否相同，如果不相同则存储当前时间，并且将显示加入公司Banner的状态置为true
				if (!compareLocalPhone()) {
					saveFirstTime();
					App.viewJoinBanner = true;
				}
			}
		}
	}

	//比较两个手机号码是否一样
	private boolean compareLocalPhone() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String savedPhone = settings.getString(LOCAL_PHONE, App.saveUserInfo.getLocalPhone());
		if (savedPhone.equalsIgnoreCase(App.saveUserInfo.getLocalPhone())) {
			return true;
		}
		return false;
	}

	//销毁广播
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(loginReceiver);
	}

	//******************************************************************************************
	private Context ctx;
//	private UserWallet mUserWallet;
//	private UserDetailInfo mUserDetailInfo;
//	private void requestUserWallet() {
//		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserWalletMessage, null, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
//			@Override
//			public void onResponse(JSONObject response) {
//				LogUtil.i("bqt", "请求用户资产账户返回：" + response.toString());
//				if (PackageUtil.getRequestStatus(response, ctx)) {
//					JSONObject data = PackageUtil.getResultObject(response);
//					//判断JSON对象是否为空
//					if (data != null) {
//						//注意数据结构变了，2016-7-26
//						mUserWallet = App.mGson.fromJson(data.toString(), UserWallet.class);
//					}
//					if (null != mUserWallet) {
//						//先将数据写入全局静态数据区
//						App.mUserWallet = mUserWallet;
//					}
//				}
//			}
//
//			@Override
//			public void onErrorResponse(Exception error) {
//			}
//		});
//		jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
//		App.getInstance().getRequestQueue().add(jsonRequest);
//	}
//
//	private void requestUserMessage() {
//		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserMessage, null, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
//			@Override
//			public void onResponse(JSONObject response) {
//				LogUtil.i("bqt", "用户信息返回数据：" + response.toString());
//				if (PackageUtil.getRequestStatus(response, ctx)) {
//					JSONObject data = PackageUtil.getResultObject(response);
//					//判断JSON对象是否为空
//					if (data != null) {
//						mUserDetailInfo = App.mGson.fromJson(data.toString(), UserDetailInfo.class);
//					}
//					//用户银行卡信息不为空，则将其存放到静态数据区
//					if (null != mUserDetailInfo) {
//						//将获取到的银行卡数据写入静态数据区中
//						App.mUserDetailInfo = mUserDetailInfo;
//					}
//				}
//			}
//
//			@Override
//			public void onErrorResponse(Exception error) {
//
//			}
//		});
//		jsonRequest.setTag(BcbRequestTag.UserBankMessageTag);
//		App.getInstance().getRequestQueue().add(jsonRequest);
//	}
}
