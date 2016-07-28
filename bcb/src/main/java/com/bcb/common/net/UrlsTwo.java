package com.bcb.common.net;

import com.bcb.data.util.MyConstants;

public class UrlsTwo {

    public static final String host = MyConstants.getHost();

    //体验标介绍
    public static String ExpiredProjectIntroduction = host + "/api/v1.0/df76208580b6";

    // 普通项目介绍
    public static String NormalProjectIntroduction = host + "/api/v1.0/9f568d457714";

    // 获取银行卡信息
    public static String UserBankMessage = host + "/api/v1.0/1337fb626563";

    //特权金列表
    public static String UserPrivilegeMoneyDto = host + "/api/v2.0/a64400ec1158";
    //激活特权金
    public static String UserPrivilegeMoneyActivated = host + "/api/v2.0/a64400f4893e";
    //托管用户开户
    public static String OpenAccount = host + "/api/v2.0/a63d00c46389";
    // 提现
    public static String UrlWithdrawals = host + "/api/v1.0/a095329ba55b";

    // 投标
    public static String UrlBuyProject = host + "/api/v1.0/e7b238e2d3df";

}
