package com.bcb.data.bean;

import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/10/14 15:31
 */
public class ZQBGbean {
	
	/**
	 * pageNow : 1
	 * pageSize : 10
	 * totalCount : 30
	 * records : [{"PackageName":"萧幼仪的置付首业借款","CreateDate":"2016-11-11","Id":"5fa1548a-9e38-427d-b351-a69a00bc7e0f","Amount":100,
	 * "Duration":"3月"}]
	 */
	
	public int PageNow;
	public int PageSize;
	public int TotalCount;
	/**
	 * PackageName : 萧幼仪的置付首业借款
	 * CreateDate : 2016-11-11
	 * Id : 5fa1548a-9e38-427d-b351-a69a00bc7e0f
	 * Amount : 100.0
	 * Duration : 3月
	 */
	
	public List<RecordsBean> Records;

	public static class RecordsBean {
		public String PackageName;
		public String CreateDate;
		public String Id;
		public float Amount;
		public String Duration;
		public String Status;
	}
}
