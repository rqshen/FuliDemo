package com.bcb.data.bean.loan;

import com.bcb.data.bean.NameVaule;

import java.io.Serializable;

/**
 * Created by cain on 16/1/7.
 * 借款用途类型枚举
 */
public class LoanTypeListBean  implements Serializable {
	public String name;
	public int value;

	public LoanTypeListBean(String name, int value) {
		this.name = name;
		this.value = value;
	}

}
