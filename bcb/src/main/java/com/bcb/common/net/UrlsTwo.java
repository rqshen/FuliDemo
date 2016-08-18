package com.bcb.common.net;

import com.bcb.data.util.MyConstants;

public class UrlsTwo {

    public static final String host = MyConstants.getHost();
    public static final String hostStatic = MyConstants.getHostStatic();

    //体验标介绍
    public static String ExpiredProjectIntroduction = host + "/api/v1.0/df76208580b6";
    public static String AboutExpiredProjectIntroduction = "http://ttwap.100cb.cn/static/specialgoldinfo.html";

    // 普通项目介绍
//    public static String NormalProjectIntroduction = host + "/api/v1.0/9f568d457714";
    public static String NormalProjectIntroduction = host + "/api/v2.0/a64b0102ddbd";

    // 获取会员信息
    public static String UserMessage = host + "/api/v2.0/a64300c7417c";

    //特权金列表
    public static String UserPrivilegeMoneyDto = host + "/api/v2.0/a64400ec1158";
    //激活特权金
    public static String UserPrivilegeMoneyActivated = host + "/api/v2.0/a64400f4893e";
    //托管用户开户
    public static String OpenAccount = host + "/api/v2.0/a63d00c46389";
    //托管用户登录
    public static String LoginAccount = host + "/api/v2.0/a63d00c4eca5";
    //托管用户信息修改
    public static String LoginAccountAlert = host + "/api/v2.0/a644012e52bb";
    //转发给汇付开户
    public static String OpenAccountHF = host + "/api/v2.0/a63d00c46389";
    // 提现
//    public static String UrlWithdrawals = host + "/api/v1.0/a095329ba55b";
    // 提现
    public static String UrlTX_HF = host + "/api/v2.0/a63c0105dc1f";

    // 投标
//    public static String UrlBuyProject = host + "/api/v1.0/e7b238e2d3df";
    public static String UrlBuyProject = host + "/api/v2.0/a63c0109b86a";

    //支持的银行
    public static String UrlBanks = host + "/api/v2.0/a63e00b4e136";
    //充值
    public static String UrlCharge = host + "/api/v2.0/a63c00fbc64e";
    //绑定提现卡
    public static String UrlBandCard = host + "/api/v2.0/a63d00c46c0d";
    //用户银行卡
    public static String UrlUserBand = host + "/api/v2.0/a648009fe9f5";
    //生利宝，产品信息查询
    public static String UrlSlb = host + "/api/v2.0/a65c00f2ac02";
    //生利宝交易
    public static String UrlSlbJY = host + "/api/v2.0/a65c00f0eb76";
    //生利宝账户查询
    public static String UrlSlbZH = host + "/api/v2.0/a65c00f1d541";
    //生利宝收益列表
    public static String UrlSlbSY = host + "/api/v2.0/a65c00f5a8d0";
    //关于汇付
    public static String UrlAboutHF= hostStatic + "/static/HftxIntroduce.html";//http://ttwap.100cb.cn/


}
