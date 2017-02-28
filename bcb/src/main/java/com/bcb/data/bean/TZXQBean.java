package com.bcb.data.bean;

import java.io.Serializable;
import java.util.List;

public class TZXQBean implements Serializable {
	
	/**
	 * Duration : 3天
	 * InterestTakeDate : 2016-07-29
	 * OrderAmount : 100
	 * PackageUrl : http://www.baidu.com
	 * Rate : 12
	 * RepaymentPlan : [{"Description":"2016-08-10(第1期)","Interest":2.05,"PayDate":"2016-8-10","Period":1,"Principal":100,"Repayed":1}]
	 * StatusCode : 1
	 * StatusName :
	 * StatusTips :
	 * TotalInterest : 20.0
	 */
	
	public String Duration;
	public String InterestTakeDate; //起息日期
	public float OrderAmount;
	public String PackageUrl;
	public float Rate;
	public int StatusCode;//状态码 0：不能申请转让 1：已完成 2：可以转让 3：转让中
	public String StatusName;//收益完成，或待起息
	public String StatusTips;//将于2017-02-03退出并回收本息
	public float TotalInterest;//满期收益
	public List<RepaymentPlanBean> RepaymentPlan;

	public static class RepaymentPlanBean {
		/**
		 * Description : 2016-08-10(第1期)
		 * Interest : 2.05
		 * PayDate : 2016-8-10
		 * Period : 1
		 * Principal : 100
		 * Repayed : 1
		 */
		public String Description;
		public float Interest;
		public String PayDate;
		public int Period;
		public int Principal;
		public int Repayed;

	}
}
