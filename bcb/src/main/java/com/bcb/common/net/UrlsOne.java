package com.bcb.common.net;

import com.bcb.data.util.MyConstants;

public class UrlsOne {

	public static final String host = MyConstants.getHost();

	// 登录
	public static final String UserDoLogin = host + "/api/v1.0/866ed3ee0260";

	// 注销
	public static final String UserDoLogout = host + "/api/v1.0/5956571607a7";
	
	// 获取手机验证码
	public static final String UserGetRegiCode = host + "/api/v1.0/a51e0104b75b";

	// 用户注册
	public static final String UserDoRegister = host + "/api/v1.0/e81bf6e10a18";
	
	// 首页广告轮播
	public static final String MainpageAdRotator = host + "/api/v1.5.2/01170ac7916f";

	//首页新手体验标
	public static final String MainpageExpired = host + "/api/v1.0/bd5f57bdbc8b";

	//首页精品项目
	public static final String MainpageBoutique = host + "/api/v1.0/a59800f24a15";

	//产品项目列表
	public static final String MainpageProduct = host + "/api/v1.0/60bb55e23716";
	
	// 项目汇总
	public static final String CompanyProduct = host + "/api/v1.0/ae72adb06802";
	
	// 首页新房宝列表
	public static final String MainpageHouses = host + "/api/v1.0/c98980c8d6f6";
	
	// 首页驿站切换-热门驿站
	public static final String MainpageHotStation = host + "/api/v1.0/7469e8252732";
		
	// 首页驿站切换-城市驿站
	public static final String MainpageCityStation = host + "/api/v1.0/7469e8252732";
	
	// 资金流水
	public static final String MoneyFlowingWater = host + "/api/v1.0/a5330105d6c3";

	// 投资记录
	public static final String TradingRecord = host + "/api/v1.0/cc738a2de840";
	
	// 交易明细
	public static final String MoneyDetail = host + "/api/v1.0/8152ae00741b";

    //资金流水详情
    public static final String MoneyItemDetail = host + "/api/v1.3.4/a5da0116755c";

	// 投资详情
	public static final String TradingRecordDetail = host + "/api/v1.0/e2c1df573f9f";

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
	public static final String GetUserInfo = host + "/api/v1.0/1337fb626563";
	
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
	
	// 兑换优惠劵
	public static final String Convert_Coupon = host + "/api/v1.0/95aabaaae186";

    //认证
    public static final String Authentication = host + "/api/v1.0/312ee12de4a6";

	//公司列表
	public static final String CompanyList = host + "/api/v1.0/04c177d3d36d";

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
	public static final String MyLoanItemDetailMessage = host + "/api/v1.2/db3a4dd4fe70";

	//上传借款补充材料
	public static final String Loan_Supplementary_Material = host + "/api/v1.3.4/a61000b6a80a";

	//还款列表
	public static final String MyLoanRepaymentMessage = host + "/api/v1.2/82dcb6a645aa";

	//订单状态查询
	public static final String RechargeOrderStatus = host + "/api/v1.0/8b441a56e2e1";

//	//首页标的数据
//	public static final String MainFragmentListData = host + "/api/v1.3/af3af1b08768";

    //首页标的数据(v1.3.4)
    public static final String MainFragmentListData = host + "/api/v1.3.4/af3af1b08768";

	//预约新标预告
	public static final String RequestAnnounce = host + "/api/v1.3/075c12497971";

	//获取新标预告
	public static final String AnnounceStatus = host + "/api/v1.3/53f03d3e19d2";

	//可用提现券信息
	public static final String WithdrawCouponInfo = host + "/api/v1.3/d3625b30882b";

    // 用户钱包信息
    public static String UserWalletMessage = host + "/api/v1.0/e7401af956b5";

    //参加每日福利
    public static final String JoinDailyWelfare = host + "/api/v1.3.4/a60100aabccb";
	//每日福利统计的数据
    public static final String DailyWelfareData = host + "/api/v1.3.4/a60100b02646";
	//查询今日拆得利率
	public static final String SearchWelfareData = host + "/api/v1.3.4/a60600ef9560";

	// 安全保障WebView
	public static final String SecureWebView = "http://wap.100cb.cn/static/security.html";
	// 理财学院WebView
	public static final String CollegeWebView = "http://fulijinrong.kuaizhan.com/58/36/p32451810370302";
	//一分钟了解福利金融
	public static final String AboutFuliJingRong = "http://wap.flh001.com/static/1minute/index.html";
	//如何获得补贴
	public static final String How2GetSubsidy = "http://wap.flh001.com/Loan/CouponActivity/index";
	//起息第一时间通知
	public static final String WxBindIndex = "http://wap.flh001.com/account/wxbindindex";

}
