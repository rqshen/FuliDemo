package com.bcb.common.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.bcb.R;
import com.bcb.common.event.BroadcastEvent;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.UserWallet;
import com.bcb.data.bean.WelfareBean;
import com.bcb.data.util.DbUtil;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.activity.Activity_Daily_Welfare_Result;
import com.bcb.presentation.view.activity.Activity_Daily_Welfare_Tip;
import com.google.gson.Gson;
import com.bcb.data.util.SaveConfigUtil;
import com.bcb.data.util.SaveUserInfoUtils;

import org.json.JSONObject;
import org.litepal.LitePalApplication;

import de.greenrobot.event.EventBus;

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

	//每日福利加息数据
	private String welfare;
	private BcbRequestQueue requestQueue;

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
		welfare = "";
		requestQueue = BcbNetworkManager.newRequestQueue(instance);
	}
	
	public static App getInstance() {
	    return instance;
	} 

	public static Context getContext() {
	    return instance.getApplicationContext();
	}

	public String getWelfare() {
		return welfare;
	}

	public void setWelfare(String welfare) {
		this.welfare = welfare;
	}

	/**
	 * 查询今日拆得利率
	 */
	public void requestWelfare(){
		JSONObject obj = new JSONObject();
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.SearchWelfareData, obj, TokenUtil.getEncodeToken(instance), true, new BcbRequest.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.getInt("status") == 1) {
						//设置对应位置的数据
						String value = response.getJSONObject("result").getString("Rate");
						LogUtil.d("福袋数据", value);
						if (!TextUtils.isEmpty(value) && Float.valueOf(value) > 0){
							//保存到数据库
							DbUtil.saveWelfare(value);
							setWelfare(value);
							//通知刷新
							EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.REFRESH));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onErrorResponse(Exception error) {

			}
		});
		requestQueue.add(jsonRequest);
	}

}
