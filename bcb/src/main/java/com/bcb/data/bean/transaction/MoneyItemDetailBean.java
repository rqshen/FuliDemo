package com.bcb.data.bean.transaction;

import java.io.Serializable;

/**
 * Created by cain on 16/4/6.
 */
public class MoneyItemDetailBean implements Serializable {
    public String TransNo; //订单号
    public String Title;   //标题
    public String Status;  //交易状态
    public float Amount;   //交易金额
    public String StatusDescn;  //交易详情状态描述
    public String CreateDate;    //交易时间
    public MoneyLoanDetail DebtExt;    //借款信息
    public MoneyInvestorDetail InvestorRepay;  //回款信息
    public MoneyRepaymentDetail LoanerRepayExt;    //还款信息
    public MoneyWithdrawDetail WithdrawExt;    //提现信息

    public String getTransNo() {
        return TransNo;
    }

    public void setTransNo(String transNo) {
        TransNo = transNo;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public float getAmount() {
        return Amount;
    }

    public void setAmount(float amount) {
        Amount = amount;
    }

    public String getStatusDescn() {
        return StatusDescn;
    }

    public void setStatusDescn(String statusDescn) {
        StatusDescn = statusDescn;
    }


    public String getTime() {
        return CreateDate;
    }

    public void setTime(String time) {
        CreateDate = time;
    }
}
