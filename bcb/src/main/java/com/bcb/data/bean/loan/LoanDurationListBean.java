package com.bcb.data.bean.loan;

/**
 * Created by cain on 16/1/7.
 * 借款期限枚举
 */
public class LoanDurationListBean {
    public String Name;
    public int Value;


    public LoanDurationListBean() {
        Name = "";
        Value = 0;
    }

    public LoanDurationListBean(String name, int value) {
        Name = name;
        Value = value;
    }

    @Override
    public String toString() {
        return Name;
    }

    public int getValue() {
        return Value;
    }

    public String getName() {
        return Name;
    }
}
