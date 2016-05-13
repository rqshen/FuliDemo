package com.bcb.common.app;

import android.app.Application;
import android.content.Context;

import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.UserWallet;
import com.google.gson.Gson;
import com.bcb.data.util.SaveConfigUtil;
import com.bcb.data.util.SaveUserInfoUtils;

import org.litepal.LitePalApplication;

public class App extends Application {
	
	public static final String TAG = "App";
	public static SaveUserInfoUtils saveUserInfo;
	public static SaveConfigUtil saveConfigUtil;
	public static Gson mGson;
	public static App instance;
    //存放银行卡信息
    public static UserDetailInfo mUserDetailInfo;
    //存放余额
    public static UserWallet mUserWallet;

	//存放是否隐藏加入公司的Banner
	public static Boolean viewJoinBanner = true;

	@Override
	public void onCreate() {
		super.onCreate();

		LitePalApplication.initialize(this);

		if (saveUserInfo == null) {
			saveUserInfo = new SaveUserInfoUtils(this);
		}
		if (saveConfigUtil == null) {
			saveConfigUtil = new SaveConfigUtil(this);
		}		
		if (mGson == null) {
			mGson = new Gson();
		}
        if (mUserWallet == null) {
            mUserWallet = new UserWallet();
        }
        if (mUserDetailInfo == null) {
            mUserDetailInfo = new UserDetailInfo();
        }
		instance = this;
	}
	
	public static App getInstance() {
	    return instance;
	} 

	public static Context getContext() {
	    return instance.getApplicationContext();
	}


}
