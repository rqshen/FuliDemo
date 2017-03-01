package com.bcb.data.bean;

import java.io.Serializable;
import java.util.List;

public class Project_Investment_Details_Bean implements Serializable {

	/**
	 * OrderAmount : 100
	 * Interest : 14.79
	 * EndDate : 2016-07-27
	 * TotalInterest : 20.0
	 * PayTime : 2016-07-27 13:57:31
	 * PackageName : 产品部卢先生的首付借款
	 * Period : 3
	 * PreInterest : 18.0
	 * InterestTakeDate : “2016-07-29”
	 * Rate : 12
	 * Status : “投资成功”
	 */
	public String PackageId;//项目Id
	public String Type;//项目类型：prj_package则为普通标 claim_convey则为债权转让标

	public float OrderAmount;//在投本金
	public float Interest;//已获收益
	public String EndDate;//到期时间
	public float TotalInterest;//满期收益
	public String PayTime;//项目名称
	public String PayDate;//项目名称
	public String StatusName;//
	public String PackageName;//加入时间
	public int Period;//封闭期
	public float PreInterest;//预期收益
	public String InterestTakeDate;//起息日期
	public float Rate;//年化利率
	public float ReceivedPrincipalAndInterest;
	public float WaitPrincipalAndInterest;
	public String Status;//状态
	public String Duration;
	public String StatusTips;
	public String PackageUrl;//项目url
	public String ReturnType;//退出方式
	public String ExpireDate;//年化利率
	public int StatusCode;// 0：不能申请转让 1：已完成 2：可以转让 3：转让中
	public int Phase;// 订单所处阶段 1：加入 5：加入后至开始计息前 10：开始计息 50：开始计息后至锁定到期前 100: 锁定到期
	public List<Plar> RepaymentPlan;
	
	public static class Plar implements Serializable {

		/**
		 * Inverest : 1
		 * PayDate : 2016-09-16 18:20:28
		 * Period : 1
		 * Principal : 20
		 */

		public float Interest;//利息，Inverest
		public String PayDate;
		public String Description;
		public int Period;//期数
		public float Principal;//本金
		public int Repayed;//1为已还，0为未还

	}

}
