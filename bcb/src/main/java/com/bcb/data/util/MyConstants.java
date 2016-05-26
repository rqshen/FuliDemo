package com.bcb.data.util;

public class MyConstants {

	public static final String IDCARDTYPE = "01";

	public static final String HOST_DEV = "http://192.168.1.111:7071";
	public static final String HOST_RELEASE = "http://app.100cb.cn";

	public static boolean TESTMODE = false;// true为测试环境，false为发布环境

	public static final String HOST = getHost();

	public static String getHost() {
		if (TESTMODE) {
			return HOST_DEV;
		} else {
			return HOST_RELEASE;
		}
	}

	// 没有token时的默认key
	public static String KEY ="1e469f986f5d4991a83b95d4";

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
