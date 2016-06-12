package com.bcb.data.bean;

/**
 * 用户银行卡信息
 * 
 * @author sun
 * 
 */

public class UserBankCard {

	public String RealName;
	public String BankCardId;
	public String BankCode;
	public String BankName;
	public String CardNumber;
	public String CardMobile;
	public String ProvinceCode;
	public String ProvinceName;
	public String CityCode;
	public String CityName;
	public String BranchBankName;
	public int BankChannel;

	public int getBankChannel() {
		return BankChannel;
	}
	public void setBankChannel(int bankChannel) {
		BankChannel = bankChannel;
	}

    public String getRealName() {
		return RealName;
	}

	public void setRealName(String realName) {
		RealName = realName;
	}

	public String getBankCardId() {
		return BankCardId;
	}

	public void setBankCardId(String bankCardId) {
		BankCardId = bankCardId;
	}

	public String getBankCode() {
		return BankCode;
	}

	public void setBankCode(String bankCode) {
		BankCode = bankCode;
	}

	public String getBankName() {
		return BankName;
	}

	public void setBankName(String bankName) {
		BankName = bankName;
	}

	public String getCardNumber() {
		return CardNumber;
	}

	public void setCardNumber(String cardNumber) {
		CardNumber = cardNumber;
	}

	public String getCardMobile() {
		return CardMobile;
	}

	public void setCardMobile(String cardMobile) {
		CardMobile = cardMobile;
	}

	public String getProvinceCode() {
		return ProvinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		ProvinceCode = provinceCode;
	}

	public String getProvinceName() {
		return ProvinceName;
	}

	public void setProvinceName(String provinceName) {
		ProvinceName = provinceName;
	}

	public String getCityCode() {
		return CityCode;
	}

	public void setCityCode(String cityCode) {
		CityCode = cityCode;
	}

	public String getCityName() {
		return CityName;
	}

	public void setCityName(String cityName) {
		CityName = cityName;
	}

	public String getBranchBankName() {
		return BranchBankName;
	}

	public void setBranchBankName(String branchBankName) {
		BranchBankName = branchBankName;
	}

}
