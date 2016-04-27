package com.bcb.data.bean;

/**
 * 用户银行卡信息
 * @author sun
 * 
 */
public class UserDetailInfo {
	public String RealName;
	public String IDCard;
	//是否已经认证
	public boolean HasCert;
	//是否已经设置了交易密码
	public boolean HasTradePassword;
	//是否已经绑定了银行卡
	public boolean HasBindCard;
	//银行卡信息
	public UserBankCard BankCard;
	//我加入的公司
	public MyCompanyBean MyCompany;
    //判断是否投过标
    public boolean HasInvest;

    //美洽ID
    public String CustomerId;


    public String getRealName() {
		return RealName;
	}

	public void setRealName(String realName) {
		RealName = realName;
	}

	public String getIDCard() {
		return IDCard;
	}

	public void setIDCard(String IDCard) {
		this.IDCard = IDCard;
	}

	public boolean isHasCert() {
		return HasCert;
	}

	public void setHasCert(boolean hasCert) {
		HasCert = hasCert;
	}

	public boolean isHasTradePassword() {
		return HasTradePassword;
	}

	public void setHasTradePassword(boolean hasTradePassword) {
		HasTradePassword = hasTradePassword;
	}

	public boolean isHasBindCard() {
		return HasBindCard;
	}

	public void setHasBindCard(boolean hasBindCard) {
		HasBindCard = hasBindCard;
	}

	public UserBankCard getBankCard() {
		return BankCard;
	}

	public void setBankCard(UserBankCard bankCard) {
		BankCard = bankCard;
	}

	public MyCompanyBean getMyCompany() {
		return MyCompany;
	}

	public void setMyCompany(MyCompanyBean myCompany) {
		MyCompany = myCompany;
	}

    public boolean isHasInvest() {
        return HasInvest;
    }

    public void setHasInvest(boolean hasInvest) {
        HasInvest = hasInvest;
    }

    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String customerId) {
        CustomerId = customerId;
    }
}