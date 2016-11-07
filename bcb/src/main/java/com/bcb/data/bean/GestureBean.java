package com.bcb.data.bean;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/9/2 21:12
 */
public class GestureBean {
	String password;
	String account;

	public GestureBean(String password, String account) {
		this.password = password;
		this.account = account;
	}

	public String toString() {
		return password + "--" + account;
	}
}
