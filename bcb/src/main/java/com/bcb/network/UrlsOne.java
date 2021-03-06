package com.bcb.network;

import com.bcb.constant.MyConstants;

public class UrlsOne {

    public static final String host = MyConstants.getHost();
    public static final String host_v2 = MyConstants.getHost();//为了区分

    // 登录
    public static final String UserDoLogin = host_v2 + "/api/v2.0/a64200d31c1b";//2016-7-25更改

    //创建邮箱验证码
    public static final String CREATEVALIDATECODE = host_v2 + "/api/v2.0/6524764356c3";

    // 注销
    public static final String UserDoLogout = host + "/api/v1.0/5956571607a7";

    // 获取手机验证码
    public static final String UserGetRegiCode = host + "/api/v1.0/a51e0104b75b";
    // 编辑借款中个人邮件地址
    public static final String EDITMYBORROWEREMAIL = host + "/api/v2.0/88c99ef28230";

    // 用户注册
    public static final String UserDoRegister = host_v2 + "/api/v2.0/a64200d30c27";//2016-7-25更改

    // 首页广告轮播
    public static final String MainpageAdRotator = host + "/api/v2.5/a71400f186e3 ";

    // 首页驿站切换-热门驿站
    public static final String MainpageHotStation = host + "/api/v1.0/7469e8252732";

    // 交易明细
    public static final String MoneyDetail = host + "/api/v2.0/a64e011c4b73";

    //资金流水详情
    public static final String MoneyItemDetail = host + "/api/v2.0/a64e011e0590";

    // 投资详情
    public static final String ClaimConveyDate = host + "/api/v2.0/a69000af0cfe";//获取债权转让变更记录

    public static final String REQUESTZR = host + "/api/v2.0/a69000ac1b78";//申请债权转让
    public static final String UNREQUESTZR = host + "/api/v2.0/a69100a6d8a4";//取消债权转让

    //债权转让信息
    public static final String CLAIMCONVEYDETAIL = host + "/api/v2.0/a69a00bc7e0f";

    //债权转让详情
    public static final String GETORDERCLAIMCONVEYINFO = host + "/api/v2.0/a69b00b209d2";

    // 修改登录密码
    public static final String ModifyLoginPwd = host + "/api/v1.0/d2c93c3de51b";

    // 找回登录密码
    public static final String ForgetLoginPwd = host + "/api/v1.0/35cf86709999";

    // 设置交易密码
    public static final String SetPayPwd = host + "/api/v1.0/e4ce6a4366c6";

    // 修改交易密码
    public static final String ModifyPayPwd = host + "/api/v1.0/76391ecc641e";

    // 找回交易密码
    public static final String ForgetPayPwd = host + "/api/v1.0/8911a5522bec";

    // 获取会员信息
    public static final String GetUserInfo = UrlsTwo.UserMessage;//2.0

    // 获取省份
    public static final String GetProvinceList = host + "/api/v1.0/623eee48b868";

    // 获取城市
    public static final String GetCityList = host + "/api/v1.0/623eee48b868";

    // 支持的银行
    public static final String SupportBank = host + "/api/v1.0/ef7a079e7dff";

    // 文案配置
    public static final String WordDataConfig = host + "/api/v1.0/3a6d4bee5020";

    // 贝付充值验证码
    public static final String Beifu_Recharge_Code = host + "/api/v1.0/8139976c4f74";
    // 确认贝付充值
    public static final String Beifu_Recharge_Confirm = host + "/api/v1.0/8b5297927ac9";

    //融宝充值验证码
    public static final String ReapalVerification = host + "/api/v1.0/710cde7d0347";
    //确认融宝充值
    public static final String ReapalRecharge = host + "/api/v1.0/8b2dc841dddd";

    // 选择优惠劵
    public static final String Select_Coupon = host + "/api/v1.0/8c1c1c07acf2";

    //债权转让列表
    public static final String SEARCHCLAIMCONVEY = host + "/api/v2.0/a69a00bc546d";

    //稳赢宝列表，打包的标【月】
    public static final String Month_Finance_LIST = host + "/api/v2.5/a71b011df7f7";
    //周盈宝列表，打包的标【天】
    public static final String Day_Finance_LIST = host + "/api/v2.5/a78000f4e1d9";

    //购买稳盈宝
    public static String Month_Buying = host + "/api/v2.0/a6de0115f478";

    //购买周盈宝
    public static String Day_Buying = host + "/api/v2.0/a78000f6d15e";

    //稳赢宝详情，打包的标【月】
    public static final String Month_Buy_DETAIL = host + "/api/v2.5/a6de0105e5df";
    //周盈宝详情，打包的标【周】
    public static final String Day_Buy_DETAIL = host + "/api/v2.5/a78000f4e64e";

    //稳赢宝【月】，购买的标的列表
    public static final String Month_MyFinancial_List = host + "/api/v2.5/a71c00f5bd72";
    //周赢宝【周】，购买的标的列表
    public static final String Day_MyFinancial_List = host + "/api/v2.5/a78000f74fdf";

    //稳赢宝【月】，购买的标的列表详情
    public static final String Month_MyFinancial_XQ = host + "/api/v2.5/a71c00f5f9ff";
    // 周赢宝【周】，购买的标的列表详情
    public static final String Day_MyFinancial_XQ = host + "/api/v2.5/a78500f9885e";

    //稳盈宝【月】，回款记录列表
    public static final String Month_BackPayment_List = host + "/api/v2.5/a7b300f898b2";

    //周盈宝【日】回款记录列表
    public static final String Day_BackPayment_List = host + "/api/v2.5/a780010e681e";

    // 稳赢宝【月】，购买的标退出
    public static final String Month_MyFinancial_Exit = host + "/api/v2.5/a71d01130432";

    // 周赢宝【周】，购买的标退出
    public static final String Day_MyFinancial_Exit = host + "/api/v2.5/a78500f9a281";

    // 兑换优惠劵
    public static final String Convert_Coupon = host + "/api/v1.0/95aabaaae186";

    //认证
    public static final String Authentication = host + "/api/v1.0/312ee12de4a6";

    //加入公司
    public static final String JoinCompany = host + "/api/v1.3.4/372bd4be30ad";

    //借款验证信息
    public static final String LoanCertification = host + "/api/v1.3.4/6b6e9b7ce261";

    //提交借款申请信息
    public static final String PostRequestMessage = host + "/api/v1.3.4/a5ee0123619e";

    //获取个人借款信息
    public static final String GetLoanPersonalMessage = host + "/api/v1.3.4/a5ee0123e74c";

    //提交个人借款信息
    public static final String PostLoanPersonalMessage = host + "/api/v1.3.4/a5ee01284c3d";

    //我的借款列表信息
    public static final String MyLoanListMessage = host + "/api/v1.2/8be49b965b27";

    //借款详情
    public static final String MyLoanItemDetailMessage = host + "/api/v2.0/a64c00c6ce76";

    //上传借款补充材料
    public static final String Loan_Supplementary_Material = host + "/api/v1.3.4/a61000b6a80a";

    //还款列表
    public static final String MyLoanRepaymentMessage = host + "/api/v2.0/a64c00c6c856";

    //订单状态查询
    public static final String RechargeOrderStatus = host + "/api/v1.0/8b441a56e2e1";

    //首页标的数据
//    public static final String MainFragmentListData2 = host + "/api/v2.5/a7910127d4ba";
    // TODO: 2017/6/29  首页标 先用原始地址
    public static final String MainFragmentListData2 = host + "/api/v2.5/a71b0110b729";

    //预约新标预告
    public static final String RequestAnnounce = host + "/api/v1.3/075c12497971";

    //获取新标预告
    public static final String AnnounceStatus = host + "/api/v1.3/53f03d3e19d2";

    //可用提现券信息
    public static final String WithdrawCouponInfo = host + "/api/v1.3/d3625b30882b";

    public static final String UserWalletMessage = host_v2 + "/api/v2.0/a64701348128";//2016-7-25更改

    //是否升级
    public static final String VERSION = host_v2 + "/api/v1.0/a560012e859f";//获取版本号

    //最后登陆
    public static final String LAST_LOGIN = host_v2 + "/api/v2.0/a6d800aa41cc";//获取版本号

    //参加每日福利
    public static final String JoinDailyWelfare = host + "/api/v1.3.4/a60100aabccb";
    //每日福利统计的数据
    public static final String DailyWelfareData = host + "/api/v1.3.4/a6390100103a";
    //查询今日拆得利率
    public static final String SearchWelfareData = host + "/api/v1.3.4/a60600ef9560";

    //不支持银行邮件发送接口
    public static final String UnSupportBankEmail = host + "/api/v1.5.2/262ade95357b";

    //聚爱项目列表
    public static final String LoveProduct = host + "/api/v1.6/a6240133fbae";

    //活动首页
    public static final String Home_Activities = host + "/api/v2.5/a7910128ebae";

    //一分钟了解福利金融
    public static final String AboutFuliJingRong = "http://wap.fulijr.com/static/1minute/index.html";
    //如何获得补贴
    public static final String How2GetSubsidy = "http://wap.fulijr.com/Loan/CouponActivity/index";
    //起息第一时间通知
    public static final String WxBindIndex = "http://wap.fulijr.com/account/wxbindindex";
    //借款服务协议
    public static final String LoanProtocol = "http://wap.fulijr.com/static/loan-agreement.html";
    //关于福利金融
    public static final String AboutConpany = "http://cnt.flh001.com/2015/12/08/mabout/";
    //我能贷多少
    public static final String LoanCalculated = "http://wap.100cb.cn/static/LoanCalculated.html";
    //利息怎么算
    public static final String InterestCalculated = "http://wap.100cb.cn/static/InterestCalculated.html";
    //如何还款
    public static final String How2Repay = "http://cnt.flh001.com/2016/01/15/ruhehuankuan/";
    //准备材料
    public static final String LoanMaterial = "http://cnt.flh001.com/2016/01/21/zunbeicailiao/";
    //关于聚爱
    public static final String AboutLove = "http://cnt.flh001.com/juaiintro/";


}
