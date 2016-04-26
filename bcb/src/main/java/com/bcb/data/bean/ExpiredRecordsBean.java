package com.bcb.data.bean;

import java.io.Serializable;

/**
 * Created by cain on 16/1/4.
 */
public class ExpiredRecordsBean implements Serializable {
    public String PackageId;        //项目Id
    public String Name;             //项目名称
    public int Duration;            //融资期限
    public int DurationExchangeType;//天标月标
    public int CouponType;          //优惠券类型
    public float Rate;              //年化利率
    public float RewardRate;        //奖励利率
    public String RewardRateDescn;  //奖励说明
    public float AmountTotal;       //融资总金额
    public float AmountBalance;     //剩余融资金额
    public String ApplyBeginTime;   //投标开始时间
    public String ApplyEndTime;     //投标结束时间
    public String PreheatTime;      //预热时间
    public int Status;              //项目状态

    public String getPackageId() {
        return PackageId;
    }

    public void setPackageId(String packageId) {
        PackageId = packageId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public int getDurationExchangeType() {
        return DurationExchangeType;
    }

    public void setDurationExchangeType(int durationExchangeType) {
        DurationExchangeType = durationExchangeType;
    }

    public int getCouponType() {
        return CouponType;
    }

    public void setCouponType(int couponType) {
        CouponType = couponType;
    }

    public float getRate() {
        return Rate;
    }

    public void setRate(float rate) {
        Rate = rate;
    }

    public float getRewardRate() {
        return RewardRate;
    }

    public void setRewardRate(float rewardRate) {
        RewardRate = rewardRate;
    }

    public String getRewardRateDescn() {
        return RewardRateDescn;
    }

    public void setRewardRateDescn(String rewardRateDescn) {
        RewardRateDescn = rewardRateDescn;
    }

    public float getAmountTotal() {
        return AmountTotal;
    }

    public void setAmountTotal(float amountTotal) {
        AmountTotal = amountTotal;
    }

    public float getAmountBalance() {
        return AmountBalance;
    }

    public void setAmountBalance(float amountBalance) {
        AmountBalance = amountBalance;
    }

    public String getApplyBeginTime() {
        return ApplyBeginTime;
    }

    public void setApplyBeginTime(String applyBeginTime) {
        ApplyBeginTime = applyBeginTime;
    }

    public String getApplyEndTime() {
        return ApplyEndTime;
    }

    public void setApplyEndTime(String applyEndTime) {
        ApplyEndTime = applyEndTime;
    }

    public String getPreheatTime() {
        return PreheatTime;
    }

    public void setPreheatTime(String preheatTime) {
        PreheatTime = preheatTime;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
