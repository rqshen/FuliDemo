package com.bcb.data.bean.transaction;

import java.io.Serializable;

/**
 * Created by cain on 16/4/6.
 * 还款信息
 */
public class MoneyRepaymentDetail implements Serializable {
    public float Principal; //回款本金
    public String AssetName;//资产名字
    public float  Interest;   //利息金额
    public String PayDate;  //回款时间
    public int Period;  //回款期数
    public float ServiceFeeAmount; //信息服务费

    public float getPrincipal() {
        return Principal;
    }

    public void setPrincipal(float principal) {
        Principal = principal;
    }

    public String getAssetName() {
        return AssetName;
    }

    public void setAssetName(String assetName) {
        AssetName = assetName;
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

    public float getServiceFeeAmount() {
        return ServiceFeeAmount;
    }

    public void setServiceFeeAmount(float serviceFeeAmount) {
        ServiceFeeAmount = serviceFeeAmount;
    }
}
