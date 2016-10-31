package com.bcb.data.bean;

public class TradingRecordRecordsBean {
	//http://192.168.1.108:8082/doku.php?id=api:doc2.0:investrecord

	private String OrderNo; // 订单号

	private float OrderAmount; // 投资金额

	private String OrderStatus; // 订单状态

	private String PackageName; // 项目名称

	private String PayTime; // 支付时间

	private float Rate; // 年化收益率

	public	float InterestAmount;//预期收益
	public int Duration;//f封闭期

	public String getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public float getOrderAmount() {
		return OrderAmount;
	}

	public void setOrderAmount(float orderAmount) {
		OrderAmount = orderAmount;
	}

	public String getOrderStatus() {
		return OrderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		OrderStatus = orderStatus;
	}

	public String getPackageName() {
		return PackageName;
	}

	public void setPackageName(String packageName) {
		PackageName = packageName;
	}

	public String getPayTime() {
		return PayTime;
	}

	public void setPayTime(String payTime) {
		PayTime = payTime;
	}

	public float getRate() {
		return Rate;
	}

	public void setRate(float rate) {
		Rate = rate;
	}

}
