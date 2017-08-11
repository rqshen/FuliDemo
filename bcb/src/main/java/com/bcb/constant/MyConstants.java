package com.bcb.constant;

//分支
public class MyConstants {

    public static final String IDCARDTYPE = "01";

    public static final String HOST_LOCAL_DEVELOP = "http://192.168.20.12";//本地，内网
    public static final String HOST_LOCAL_TEMP = "http://192.168.20.232";//临时测试
    public static final String HOST_DEVELOP = "http://112.74.107.186:9003";//测试外网。线下"http://ttgateway.100cb.cn";
    public static final String HOST_RELEASE = "https://app.fulijr.com";//正式"http://app.fulijr.com";
    public static final String H5URL_HOST_DEVELOP = "http://ttwap.100cb.cn";//测试
    public static final String H5URL_HOST_RELEASE = "http://wap.fulijr.com";//正式

    public static int ENVIRONMENT = NetWorkEnvironment.LOCAL_DEVELOP;// LOCAL_DEVELOP本地,LOCAL_TEMP临时,DEVELOP线下,RELEASE正式

    public static String getHost() {
        switch (ENVIRONMENT) {
            case NetWorkEnvironment.LOCAL_DEVELOP:
                return HOST_LOCAL_DEVELOP;
            case NetWorkEnvironment.LOCAL_TEMP:
                return HOST_LOCAL_TEMP;
            case NetWorkEnvironment.DEVELOP:
                return HOST_DEVELOP;
            case NetWorkEnvironment.RELEASE:
                return HOST_RELEASE;
            default:
                return HOST_RELEASE;
        }
    }

    public static String getHostStatic() {
        switch (ENVIRONMENT) {
            case NetWorkEnvironment.LOCAL_DEVELOP:
            case NetWorkEnvironment.LOCAL_TEMP:
            case NetWorkEnvironment.DEVELOP:
                return H5URL_HOST_DEVELOP;
            default:
                return H5URL_HOST_RELEASE;
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
