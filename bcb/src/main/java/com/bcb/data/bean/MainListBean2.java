package com.bcb.data.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cain on 16/2/17.
 */
public class MainListBean2 implements Serializable {

	public List<JpxmBean> Jpxm;
	public List<XbygBean> Xbyg;

	public static class JpxmBean {
		/**
		 * Amount : 3750.02
		 * Balance : 2000      剩余融资金额
		 * Duration : 1
		 * DurationExchangeType : 2
		 * Name : 涨薪宝201705220149
		 * Old : false
		 * PackageId : bdf2e9a2-3b23-4f51-8d16-a71a0106a1ea
		 * ProcessPercent : 46.67
		 * Rate : 8
		 * Status : 20
		 * Type : mon_package
		 */

		public String Amount;
		public float Balance;
		public int Duration;
		public int DurationExchangeType;
		public String Name;
		public boolean Old;
		public String PackageId;
		public float ProcessPercent;
		public float Rate;
		public int Status;
		public String Type;
	}

	public static class XbygBean {
		/**
		 * Amount : 3750.02
		 * Duration : 1     融资期限
		 * DurationExchangeType : 2   天标（1）月标（2）
		 * Name : 涨薪宝201705220149
		 * PackageId : bdf2e9a2-3b23-4f51-8d16-a71a0106a1ea
		 * PredictCount : 20      预约人数
		 * Rate : 8
		 * Source : 来源
		 */

		public float Amount;
		public int Duration;
		public int DurationExchangeType;
		public String Name;
		public String PackageId;
		public int PredictCount;
		public float Rate;
		public String Source;
		public int PackageStatus;
	}
}
