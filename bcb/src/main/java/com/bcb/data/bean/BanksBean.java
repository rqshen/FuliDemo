package com.bcb.data.bean;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/8/4 11:49
 */
public class BanksBean {

    /**
     * BankName : 招商银行
     * BankCode : CMB
     * MaxSingle : 50000
     * MaxDay : 50000
     */

    private String BankName;
    private String BankCode;
    private int MaxSingle;
    private int MaxDay;

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String BankName) {
        this.BankName = BankName;
    }

    public String getBankCode() {
        return BankCode;
    }

    public void setBankCode(String BankCode) {
        this.BankCode = BankCode;
    }

    public int getMaxSingle() {
        return MaxSingle;
    }

    public void setMaxSingle(int MaxSingle) {
        this.MaxSingle = MaxSingle;
    }

    public int getMaxDay() {
        return MaxDay;
    }

    public void setMaxDay(int MaxDay) {
        this.MaxDay = MaxDay;
    }
}
