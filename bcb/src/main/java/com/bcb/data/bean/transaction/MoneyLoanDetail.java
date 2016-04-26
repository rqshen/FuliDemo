package com.bcb.data.bean.transaction;

import java.io.Serializable;

/**
 * Created by cain on 16/4/6.
 */
public class MoneyLoanDetail implements Serializable {
    public float LoanAmount;   //放款金额
    public String PayDate;     //放款时间
    public int Period; //总期数
    public float SecurityDepositFeeAmount; //保证金金额
    public float ServiceFeeAmount;     //信息服务费

    public float getLoanAmount() {
        return LoanAmount;
    }

    public void setLoanAmount(float loanAmount) {
        LoanAmount = loanAmount;
    }

    public String getPayDate() {
        return PayDate;
    }

    public void setPayDate(String payDate) {
        PayDate = payDate;
    }

    public int getPeriod() {
        return Period;
    }

    public void setPeriod(int period) {
        Period = period;
    }

    public float getSecurityDepositFeeAmount() {
        return SecurityDepositFeeAmount;
    }

    public void setSecurityDepositFeeAmount(float securityDepositFeeAmount) {
        SecurityDepositFeeAmount = securityDepositFeeAmount;
    }

    public float getServiceFeeAmount() {
        return ServiceFeeAmount;
    }

    public void setServiceFeeAmount(float serviceFeeAmount) {
        ServiceFeeAmount = serviceFeeAmount;
    }
}
