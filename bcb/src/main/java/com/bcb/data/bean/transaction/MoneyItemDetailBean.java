package com.bcb.data.bean.transaction;

import java.io.Serializable;

/**
 * Created by cain on 16/4/6.
 */
public class MoneyItemDetailBean implements Serializable {
	public String TransNo;
	public String Title;   //标题
	public String Status;  //交易状态

	public String StatusDescn;  //交易详情状态描述
	public String CreateDate;    //交易时间

	public MoneyLoanDetail DebtExt;    //借款信息
	public MoneyInvestorDetail InvestorRepay;  //回款信息
	public MoneyRepaymentDetail LoanerRepayExt;    //还款信息
	public MoneyWithdrawDetail WithdrawExt;    //提现信息
	public MoneyRechargeDetail RechargeExt;//充值信息

	public String BillId;//订单号
	public float Amount;   //交易金额
	public int TopCategoryId;//交易类型
	public int Type;//收入支付类型（1收入0支出）
	public String Time;//操作时间
}
