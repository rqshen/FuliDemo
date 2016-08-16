package com.bcb.data.bean;

import java.io.Serializable;
import java.util.List;

public class Project_Investment_Details_Bean implements Serializable{

    /**
     * OrderAmount : 100
     * Interest : 14.79
     * EndDate : 2016-07-27
     * TotalInterest : 20.0
     * PayTime : 2016-07-27 13:57:31
     * PackageName : 产品部卢先生的首付借款
     * Period : 3
     * PreInterest : 18.0
     * InterestTakeDate : “2016-07-29”
     * Rate : 12
     * Status : “投资成功”
     */

    private float OrderAmount;//在投本金
    private float Interest;//已获收益
    private String EndDate;//到期时间
    private float TotalInterest;//满期收益
    private String PayTime;//项目名称
    private String PackageName;//加入时间
    private int Period;//封闭期
    private float PreInterest;//预期收益
    private String InterestTakeDate;//起息日期
    private float Rate;//年化利率
    private String Status;//状态
    public int Duration;//年化利率
    public List<Plar> RepaymentPlan;

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public List<Plar> getRepaymentPlan() {
        return RepaymentPlan;
    }

    public void setRepaymentPlan(List<Plar> repaymentPlan) {
        RepaymentPlan = repaymentPlan;
    }

    public static class Plar implements Serializable{

        /**
         * Inverest : 1
         * PayDate : 2016-09-16 18:20:28
         * Period : 1
         * Principal : 20
         */

        public float Inverest;//利息
        public String PayDate;
        public int Period;//期数
        public float Principal;//本金
    }

    public float getOrderAmount() {
        return OrderAmount;
    }

    public void setOrderAmount(float orderAmount) {
        OrderAmount = orderAmount;
    }

    public float getInterest() {
        return Interest;
    }

    public void setInterest(float interest) {
        Interest = interest;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public float getTotalInterest() {
        return TotalInterest;
    }

    public void setTotalInterest(float totalInterest) {
        TotalInterest = totalInterest;
    }

    public String getPayTime() {
        return PayTime;
    }

    public void setPayTime(String payTime) {
        PayTime = payTime;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public int getPeriod() {
        return Period;
    }

    public void setPeriod(int period) {
        Period = period;
    }

    public float getPreInterest() {
        return PreInterest;
    }

    public void setPreInterest(float preInterest) {
        PreInterest = preInterest;
    }

    public String getInterestTakeDate() {
        return InterestTakeDate;
    }

    public void setInterestTakeDate(String interestTakeDate) {
        InterestTakeDate = interestTakeDate;
    }

    public float getRate() {
        return Rate;
    }

    public void setRate(float rate) {
        Rate = rate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
