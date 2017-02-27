package com.bcb.data.bean;

import java.util.List;

public class WYBbean {

	/**
	 * PageNow : 1
	 * PageSize : 3
	 * TotalCount : 58
	 * Records : [{"PackageId":"1cb29670-f320-407d-ad13-3cee881e1bb9","Name":"胡昌俊的借款项目","Rate":6.5,"Balance":49900,"Amount":500000,"Duration":5,"DurationExchangeType":1,"ProcessPercent":0,
	 * "Status":25,"Type":"claim_convey","Old":"1"},{"PackageId":"cf343b4a-c7c7-463c-bf00-a59500f03b54","Name":"YMFGRJK201601222504","Rate":5.7,"Balance":0,"Amount":500000,"Duration":24,
	 * "DurationExchangeType":2,"ProcessPercent":0,"Status":25,"Type":"prj_package"},{"PackageId":"524c5dd4-3caa-43a7-92e2-a62e0156e32b","Name":"YDSGRJK201606234150","Rate":12,"Balance":3150,
	 * "Amount":25000,"Duration":1,"DurationExchangeType":2,"ProcessPercent":0,"Status":25,"Type":"prj_package"}]
	 */

	public int PageNow;
	public int PageSize;
	public int TotalCount;
	public List<MainListBean2.JpxmBean> Records;
}
