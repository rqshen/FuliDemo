package com.bcb.data.bean;


import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CouponRecordsBean implements Comparable<CouponRecordsBean>{
	private String Name;
	private float Amount;
	private String ExpireDate;
	private float MinAmount;
	private int Status;
	private int CouponType;
	private String CouponId;
	private String ConditionDescn;
	private SimpleDateFormat dateFormater;

	public CouponRecordsBean() {
		dateFormater = new SimpleDateFormat("yyyy-MM-dd");//将dateString格式化成 XXX-XX-XX 的形式
	}

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

	@Override
	public int compareTo(CouponRecordsBean another) {//按照过期日期进行排序
		int result = 0;
		try {
			if (dateFormater.parse(ExpireDate).after(dateFormater.parse(another.getExpireDate()))){
				result = 1;
			}else {
				result = -1;
			}
		}catch (ParseException e){
			e.printStackTrace();
		}
		return result;
	}
}
