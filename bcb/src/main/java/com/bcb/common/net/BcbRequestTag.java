package com.bcb.common.net;

/**
 * Created by cain on 16/4/15.
 * 网络接口的请求标志
 */
public interface BcbRequestTag {
    //登陆
    String BCB_LOGIN_REQUEST = "BcbLoginRequest";
    //注销
    String BCB_LOGOUT_REQUEST = "BcbLogoutRequest";
    //获取手机验证码
    String BCB_VERIFICATION_REQUEST = "BcbVerificationRequest";
    //用户注册
    String BCB_REGISTER_REQUEST = "BcbRegisterRequest";
    //首页广告轮播
    String MainAdRotatorTag = "BcbMainAdRotatorRequest";
    //产品列表
    String MainProductTag = "BcbMainProductRequest";
    //聚爱列表
    String LoveProductTag = "BcbLoveProductRequest";
    //热门驿站
    String MainHotStationTag = "BcbMainHotStationRequest";
    //安全保障
    String MainSecureTag = "BcbMainSecureRequest";
    //投资记录
    String TradeRecordTag = "BcbTradeRecordRequest";
    //投资记录详情
    String TradeRecordDetailTag = "BcbTradeRecordDetailRequest";
    //交易明细(资金流水)
    String TransactionTag = "BcbTransactionRequest";
    //资金流水详情
    String TranscationDetailTag = "BcbTransactionDetailRequest";
    //修改密码
    String ModifyPasswordTag = "BcbModifyPasswordRequest";
    //设置交易密码
    String SetPayPasswordTag = "BcbSetPayPasswordRequest";
    //找回交易密码
    String FogetPasswordTag = "BcbFogetPayPasswordRequest";
    //获取会员信息
    String BCB_GET_USER_INFORMATION_REQUEST = "BcbGetUserInformationRequest";
    //获取省份
    String GetProvinceListTag = "BcbGetProvinceListRequest";
    //获取城市
    String GetCityListTag = "BcbGetCityListRequest";
    //支持的银行
    String SupportBankTag = "BcbSupportBankRequest";
    //文案配置
    String WordDataConfigTag = "BcbWordDataConfigRequest";
    //贝付充值验证码
    String BeifuRechargeCodeTag = "BcbBeifuRechargeCodeRequest";
    //确认贝付充值
    String BeifuRechargeConfirmTag = "BcbBeifuRechargeConfirmRequest";
    //融宝充值验证码
    String ReapalVerificationTag = "BCbReapalVerificationRequest";
    //融宝充值
    String ReapalRechargeTag = "BcbReapalRechargeRequest";
    //选择优惠券
    String BCB_SELECT_COUPON_REQUEST = "BcbSelectCouponRequest";
    //兑换优惠券
    String ConvertCouponTag = "BcbConvertCouponRequest";
    //认证
    String BCB_AUTHENTICATION_REQUEST ="BcbAuthenticationRequest";
    //加入公司
    String BCB_JOIN_COMPANY_REQUEST = "BcbJoinCompanyRequest";
    //借款验证
    String BCB_LOAN_CERTIFICATION_REQUEST = "BcbLoanCertificationRequest";
    //获取借款的个人信息
    String BCB_GET_LOAN_PERSONAL_MESSAGE_REQUEST = "BcbGetLoanPersonalMessageRequest";
    //提交借款的个人信息
    String BCB_POST_LOAN_PERSONAL_MESSAGE_REQUEST = "BcbPostLoanPersonalMessageRequest";
    //提交借款申请信息
    String BCB_CREATE_LOAN_REQUEST_MESSAGE_REQUEST = "BcbCreateLoanRequestMessageRequest";
    //我的借款列表
    String MyLoanListMessageTag = "BcbMyLoanMessageRequest";
    //借款详情
    String MyLoanItemDetailMessageTag = "BcbMyLoanItemDetailMessageRequest";
    //还款列表
    String MyLoanRepaymentMessageTag = "BcbMyLoanRepaymentMessageRequest";
    //订单状态查询
    String RechargeOrderStatusTag = "BcbRechargeOrderStatusRequest";
    //首页标的数据(V1.3.4)
    String MainFragmentListDataTag = "BcbMainFragmentListDataRequest";
    //新标预告预约状态
    String AnnounceStatusTag = "BcbAnnounceStatusRequest";
    //预约新标预告
    String RequestAnnounceTag = "BcbAnnounceRequest";
    //可用提现券信息
    String WithdrawCouponInfoTag = "BcbWithdrawCouponInfoRequest";
    //体验标介绍
    String ExpiredProjectIntroductionTag = "BcbExpiredProjectIntroductionRequest";
    //普通标介绍
    String NormalProjectIntroductionTag = "BcbNormalProjectIntroductionRequest";
    //用户钱包信息
    String UserWalletMessageTag = "BcbUserWalletMessageRequest";
    //银行卡信息
    String UserBankMessageTag = "BcbUserBankMessageRequest";
    //提现
    String UrlWithdrawalTag = "BcbUrlWithdrawalRequest";
    //投标
    String UrlBuyProjectTag = "BcbUrlBuyProjectRequest";
    //参加每日福利
    String UrlJoinWelfareTag = "JoinWelfareRequest";
    //特权金列表
    String UserPrivilegeMoneyDtoTag = "UserPrivilegeMoneyRequest";
    //激活特权金
   String UserPrivilegeMoneyActivatedTag ="UserPrivilegeMoneyActivatedRequest";
}
