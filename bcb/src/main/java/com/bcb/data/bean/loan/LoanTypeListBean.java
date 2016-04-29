package com.bcb.data.bean.loan;

import com.bcb.data.bean.NameVaule;

import java.io.Serializable;

/**
 * Created by cain on 16/1/7.
 * 借款用途类型枚举
 */
public class LoanTypeListBean extends NameVaule implements Serializable {

    public LoanTypeListBean() {
    }

    public LoanTypeListBean(String name, int value) {
        this.Name = name;
        this.Value = value;
    }

}
