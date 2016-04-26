package com.bcb.data.bean;

public class BankItem {
	private String BankCode;
	private String Logo;
	private String BankName;
	private float MaxSingle;
	private float MaxDay;

	public BankItem(String bankCode, String logo, String bankName, float maxSingle, float maxDay) {
		super();
		BankCode = bankCode;
		Logo = logo;
		BankName = bankName;
		MaxSingle = maxSingle;
		MaxDay = maxDay;
	}

	public String getBankCode() {
		return BankCode;
	}

	public void setBankCode(String bankCode) {
		BankCode = bankCode;
	}

	public String getLogo() {
		return Logo;
	}

	public void setLogo(String logo) {
		Logo = logo;
	}

	public String getBankName() {
		return BankName;
	}

	public void setBankName(String bankName) {
		BankName = bankName;
	}

	public float getMaxSingle() {
		return MaxSingle;
	}

	public void setMaxSingle(float maxSingle) {
		MaxSingle = maxSingle;
	}

	public float getMaxDay() {
		return MaxDay;
	}

	public void setMaxDay(float maxDay) {
		MaxDay = maxDay;
	}

}
