package com.bcb.data.bean;

/**
 * Created by Ray on 2016/7/8.
 *
 * @desc 特权本金
 */
public class PrivilegeMoneyDto {


    private String GoldNo;
    private float Amount;
    private float Income;
    private float Rate;
    private int Days;
    private String ExpireDate;
    private int Status;

    public String getGoldNo() {
        return GoldNo;
    }

    public void setGoldNo(String GoldNo) {
        this.GoldNo = GoldNo;
    }

    public float getAmount() {
        return Amount;
    }

    public void setAmount(float Amount) {
        this.Amount = Amount;
    }

    public float getIncome() {
        return Income;
    }

    public void setIncome(float Income) {
        this.Income = Income;
    }

    public float getRate() {
        return Rate;
    }

    public void setRate(float Rate) {
        this.Rate = Rate;
    }

    public int getDays() {
        return Days;
    }

    public void setDays(int Days) {
        this.Days = Days;
    }

    public String getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(String ExpireDate) {
        this.ExpireDate = ExpireDate;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }
}
