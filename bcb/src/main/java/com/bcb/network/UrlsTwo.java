package com.bcb.network;

import com.bcb.constant.MyConstants;

public class UrlsTwo {

    public static final String host = MyConstants.getHost();
    public static final String hostStatic = MyConstants.getHostStatic();

    public static String AboutExpiredProjectIntroduction = "http://ttwap.100cb.cn/static/specialgoldinfo.html";

    // 获取会员信息
    public static String UserMessage = host + "/api/v2.0/a64300c7417c";

    //开启自动投标计划
    public static String OPENAUTOTENDERPLAN = host + "/api/v2.0/a6a400b598f2";

    //特权金列表
    public static String UserPrivilegeMoneyDto = host + "/api/v2.0/a64400ec1158";
    //激活特权金
    public static String UserPrivilegeMoneyActivated = host + "/api/v2.0/a64400f4893e";
    //托管用户开户
    public static String OpenAccount = host + "/api/v2.0/a63d00c46389";
    //借款首页
    public static String LOANKINDLIST = host + "/api/v2.0/337fca64f90b";
    //企业列表
    public static String ENTERPRISELIST = host + "/api/v2.0/8f1171e9a405 ";
    //托管用户登录
    public static String LoginAccount = host + "/api/v2.0/a63d00c4eca5";
    //托管用户信息修改
    public static String LoginAccountAlert = host + "/api/v2.0/a644012e52bb";
    // 提现
    public static String UrlTX_HF = host + "/api/v2.0/a63c0105dc1f";


    //支持的银行
    public static String UrlBanks = host + "/api/v2.0/a63e00b4e136";
    //充值
    public static String UrlCharge = host + "/api/v2.0/a63c00fbc64e";
    //绑定提现卡
    public static String UrlBandCard = host + "/api/v2.0/a63d00c46c0d";
    //用户银行卡
    public static String UrlUserBand = host + "/api/v2.0/a648009fe9f5";
    //保险
    public static String MYINSURANCE = host + "/api/v2.0/a6f300ade6f0";
    //生利宝，产品信息查询
    public static String UrlSlb = host + "/api/v2.0/a65c00f2ac02";
    //生利宝交易
    public static String UrlSlbJY = host + "/api/v2.0/a65c00f0eb76";
    //生利宝账户查询
    public static String UrlSlbZH = host + "/api/v2.0/a65c00f1d541";
    //生利宝收益列表
    public static String UrlSlbSY = host + "/api/v2.0/a65c00f5a8d0";
    //关于汇付
    public static String UrlAboutHF = hostStatic + "/static/HftxIntroduce.html";//http://ttwap.100cb.cn/

    //解绑
    public static String UrlUnBand = "https://c.chinapnr.com/p2puser/";
    //解绑说明
    public static String UrlUnBandExplain = hostStatic + "/static/UnbindExplain.html";
    //接入证明
    public static String UrlZM = hostStatic + "/static/hftgzm.html";


}
