package com.bcb.data.bean.loan;

import java.io.Serializable;

/**
 * Created by cain on 16/1/7.
 * 借款用途类型枚举
 */
public class LoanTypeListBean implements Serializable {
    public String Name;
    public int Value;

    public LoanTypeListBean() {
        Name = "";
        Value = 0;
    }

    public LoanTypeListBean(String name, int value) {
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
