package com.bcb.data.bean;

import java.util.List;

public class TradingRecordListBean {

	private float TotalAmount;//累计交易金额
	private float TotalInterestAmount;
	public float TotalInvestorFuturePrincipalAmount;

	private InvetDetailBean InvetDetail;

	public float getTotalAmount() { return TotalAmount;}

	public void setTotalAmount(float TotalAmount) { this.TotalAmount = TotalAmount;}

	public float getTotalInterestAmount() { return TotalInterestAmount;}

	public void setTotalInterestAmount(float TotalInterestAmount) { this.TotalInterestAmount = TotalInterestAmount;}

	public float getTotalInvestorFuturePrincipalAmount() { return TotalInvestorFuturePrincipalAmount;}

	public void setTotalInvestorFuturePrincipalAmount(float TotalInvestorFuturePrincipalAmount) { this.TotalInvestorFuturePrincipalAmount = TotalInvestorFuturePrincipalAmount;}

	public InvetDetailBean getInvetDetail() { return InvetDetail;}

	public void setInvetDetail(InvetDetailBean InvetDetail) { this.InvetDetail = InvetDetail;}

	public static class InvetDetailBean {
		private int PageNow;
		private int PageSize;
		private int TotalCount;
		/**
		 * Duration : 2
		 * OrderAmount : 100
		 * OrderNo : 22965151021174824210
		 * OrderStatus : 收益中
		 * PackageName : 投资测试
		 * PayTime : 2015-10-21 12:17:49
		 * Rate : 6.8
		 * InterestAmount : 0.01863013698630137
		 */

		private List<RecordsBean> Records;

		public int getPageNow() { return PageNow;}

		public void setPageNow(int pageNow) { this.PageNow = pageNow;}

		public int getPageSize() { return PageSize;}

		public void setPageSize(int pageSize) { this.PageSize = pageSize;}

		public int getTotalCount() { return TotalCount;}

		public void setTotalCount(int totalCount) { this.TotalCount = totalCount;}

		public List<RecordsBean> getRecords() { return Records;}

		public void setRecords(List<RecordsBean> records) { this.Records = records;}

		public static class RecordsBean {
			private String Duration;
			private float OrderAmount;
			private String OrderNo;
			private String OrderStatus;
			private String PackageName;
			private String PayTime;
			private float Rate;
//			private float InterestAmount;

			public String getDuration() { return Duration;}

			public void setDuration(String Duration) { this.Duration = Duration;}

			public float getOrderAmount() { return OrderAmount;}

			public void setOrderAmount(float OrderAmount) { this.OrderAmount = OrderAmount;}

			public String getOrderNo() { return OrderNo;}

			public void setOrderNo(String OrderNo) { this.OrderNo = OrderNo;}

			public String getOrderStatus() { return OrderStatus;}

			public void setOrderStatus(String OrderStatus) { this.OrderStatus = OrderStatus;}

			public String getPackageName() { return PackageName;}

			public void setPackageName(String PackageName) { this.PackageName = PackageName;}

			public String getPayTime() { return PayTime;}

			public void setPayTime(String PayTime) { this.PayTime = PayTime;}

			public float getRate() { return Rate;}

			public void setRate(float Rate) { this.Rate = Rate;}

		}
	}
}
