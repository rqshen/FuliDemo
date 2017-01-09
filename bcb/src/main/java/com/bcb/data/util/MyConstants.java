package com.bcb.data.util;

//分支
public class MyConstants {

	public static final String IDCARDTYPE = "01";

	//192.168.1.108
	public static final String HOST_DEV_LOCAL = "http://192.168.1.111:5071";//本地，内网。"http://192.168.1.111:7071"
	public static final String HOST_DEV = "http://ttgateway.100cb.cn";//测试外网。线下"http://ttgateway.100cb.cn";
	public static final String HOST_RELEASE = "https://app.fulijr.com";//正式"http://app.fulijr.com";

	public static final String HOST_DEV_Static = "http://ttwap.100cb.cn";//测试
	public static final String HOST_RELEASE_Static = "http://wap.fulijr.com";//正式

	public static int TESTMODE = 1;// 0为本地，1为线下，2为正式

	public static String getHost() {
		switch (TESTMODE) {
			case 0:
				return HOST_DEV_LOCAL;
			case 1:
				return HOST_DEV;
			default:
				return HOST_RELEASE;
		}
	}

	public static String getHostStatic() {
		switch (TESTMODE) {
			case 0:
			case 1:
				return HOST_DEV_Static;
			default:
				return HOST_RELEASE_Static;
		}
	}

	// 没有token时的默认key
	public static String KEY = "1e469f986f5d4991a83b95d4";

	// 微信分享需要的注册APPID
	public static final String APP_ID = "wx2a098a76630fc98f";

	/////////////////////我的优惠券类型///////////////////////
	//体验券
	public static final int EXPERIENCE = 1;
	//现金券
	public static final int CASH = 2;
	//提现券
	public static final int WITHDRAW = 8;
	//借款补贴券
	public static final int LOAN_SUBSIDIES = 16;
	//免息券
	public static final int FREE = 1048576;

}
