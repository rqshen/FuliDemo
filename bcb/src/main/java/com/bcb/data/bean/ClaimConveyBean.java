package com.bcb.data.bean;

import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/10/10 14:33
 */
public class ClaimConveyBean {

	/**
	 * status : 1
	 * message :
	 * result : [{"ClaimConveyDate":"2016-09-28 18:49:48.000","Amount":"1000.00"},{"ClaimConveyDate":"2016-09-28 18:49:56.000",
	 * "Amount":"1000.00"}]
	 */

	private int status;
	private String message;
	/**
	 * ClaimConveyDate : 2016-09-28 18:49:48.000
	 * Amount : 1000.00
	 */

	private List<ResultBean> result;

	public int getStatus() { return status;}

	public void setStatus(int status) { this.status = status;}

	public String getMessage() { return message;}

	public void setMessage(String message) { this.message = message;}

	public List<ResultBean> getResult() { return result;}

	public void setResult(List<ResultBean> result) { this.result = result;}

	public static class ResultBean {
		private String ClaimConveyDate;
		private String Amount;

		public String getClaimConveyDate() { return ClaimConveyDate;}

		public void setClaimConveyDate(String ClaimConveyDate) { this.ClaimConveyDate = ClaimConveyDate;}

		public String getAmount() { return Amount;}

		public void setAmount(String Amount) { this.Amount = Amount;}
	}
}
