package com.bcb.data.bean;


public class CouponRecordsBean {
	private String Name;
	private float Amount;
	private String ExpireDate;
	private float MinAmount;
	private int Status;
	private int CouponType;
	private String CouponId;
	private String ConditionDescn;


	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public float getAmount() {
		return Amount;
	}

	public void setAmount(float amount) {
		Amount = amount;
	}

	public String getExpireDate() {
		return ExpireDate;
	}

	public void setExpireDate(String expireDate) {
		ExpireDate = expireDate;
	}

	public float getMinAmount() {
		return MinAmount;
	}

	public void setMinAmount(float minAmount) {
		MinAmount = minAmount;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public int getCouponType() {
		return CouponType;
	}

	public void setCouponType(int couponType) {
		CouponType = couponType;
	}

	public String getCouponId() {
		return CouponId;
	}

	public void setCouponId(String couponId) {
		CouponId = couponId;
	}

	public String getConditionDescn() {
		return ConditionDescn;
	}

	public void setConditionDescn(String conditionDescn) {
		ConditionDescn = conditionDescn;
	}

}
