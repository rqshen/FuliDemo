package com.bcb.common.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.bcb.common.event.BroadcastEvent;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.UserWallet;
import com.bcb.data.bean.WelfareBean;
import com.bcb.data.util.DbUtil;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MapUtil;
import com.bcb.data.util.SaveConfigUtil;
import com.bcb.data.util.SaveUserInfoUtils;
import com.bcb.data.util.SystemUtil;
import com.bcb.data.util.TokenUtil;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
import org.litepal.LitePalApplication;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

public class App extends Application implements AMapLocationListener{
	
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

	//位置信息工具
	private MapUtil mapUtil;

	@Override
	public void onCreate() {
		super.onCreate();

		//数据库初始化
		LitePalApplication.initialize(this);
		//极光推送初始化
		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
		JPushInterface.init(this);     		// 初始化 JPush

		//关闭友盟bug收集
		MobclickAgent.setCatchUncaughtExceptions(false);
		//腾讯bugly
		CrashReport.initCrashReport(getApplicationContext(), "900035490", false);

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

		requestQueue = BcbNetworkManager.newRequestQueue(instance);

		mapUtil = new MapUtil(this,this);
		//获取定位数据
		doLocation();
		//获取福利数据
		if (null != saveUserInfo.getAccess_Token()){
			WelfareBean welfareBean = DbUtil.getWelfare();
			welfare = null == welfareBean ? "" : welfareBean.getValue();
		} else {
			welfare = "";
		}

		//设置极光推送别名
		setAlias();
	}

	public static App getInstance() {
	    return instance;
	} 

	public static Context getContext() {
	    return instance.getApplicationContext();
	}

	public String getWelfare() {
		return null == saveUserInfo.getAccess_Token() ? "" : welfare;
	}

	public void setWelfare(String welfare) {
		this.welfare = welfare;
	}

	/**
	 * 设置极光推送别名
	 */
	public void setAlias(){
		LogUtil.d(TAG, "CustomerId = " + mUserDetailInfo.getCustomerId());
		JPushInterface.setAliasAndTags(getApplicationContext(), mUserDetailInfo.getCustomerId(), null, mAliasCallback);
	}

	/**
	 * 设置极光推送标签
	 */
	public void setTag(){
		Set<String> tagSet = new LinkedHashSet<>();
		tagSet.add(mUserDetailInfo.getCustomerId());//设置别名为CustomerId
		LogUtil.d(TAG, "CustomerId = " + tagSet.toString());
		JPushInterface.setAliasAndTags(getApplicationContext(), null, tagSet, mTagsCallback);

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


	public void doLocation(){
		mapUtil.start();
	}

	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {
		//在保存地理位置的时候顺便保存一下其他额外信息(网络环境、手机型号、imei)
		String address = "";
		if (null != aMapLocation) {
//			LogUtil.d("位置信息", mapUtil.getLocationStr(aMapLocation));
			address = aMapLocation.getAddress();
		}
		String imei = SystemUtil.getImei(this);
		String model = android.os.Build.MODEL;
		String network = SystemUtil.getNetworkType(this);
		LogUtil.d("位置信息", "imei = " + imei + " model = " + model + " network = " + network + " address = " + address);
		DbUtil.saveUserExtra(imei,model,network,address);
	}

	//仅用于JPush测试
	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs ;
			switch (code) {
				case 0:
					logs = "Set tag and alias success";
					LogUtil.i(TAG, logs);
					break;

				case 6002:
					logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
					LogUtil.i(TAG, logs);
					break;

				default:
					logs = "Failed with errorCode = " + code;
					LogUtil.e(TAG, logs);
			}
		}

	};

	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs ;
			switch (code) {
				case 0:
					logs = "Set tag and alias success";
					LogUtil.i(TAG, logs);
					break;

				case 6002:
					logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
					LogUtil.i(TAG, logs);
					break;

				default:
					logs = "Failed with errorCode = " + code;
					LogUtil.e(TAG, logs);
			}
		}

	};

}
