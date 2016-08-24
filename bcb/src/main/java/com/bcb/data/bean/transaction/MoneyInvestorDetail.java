package com.bcb.data.bean.transaction;

import java.io.Serializable;

/**
 * Created by cain on 16/4/6.
 * 回款信息对象
 */
public class MoneyInvestorDetail implements Serializable {
    public float Principal; //回款本金
    public float ServiceFee; //信息服务费
    public String PackageName;  //项目名称
    public float Interest;  //利息金额
    public String PayDate;  //回款时间
    public int Period;  //回款期数

    public float getPrincipal() {
        return Principal;
    }

    public void setPrincipal(float principal) {
        Principal = principal;
    }

    public float getServiceFeeAmount() {
        return ServiceFee;
    }

    public void setServiceFeeAmount(float serviceFeeAmount) {
        ServiceFee = serviceFeeAmount;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public float getInterest() {
        return Interest;
    }

    public void setInterest(float interest) {
        Interest = interest;
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
}
