package com.bcb.data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/11/30 10:32
 */
public class JEnterprise implements Serializable {

	/**
	 * Version : 2
	 * EnterpriseList : [{"Name":"企业1","EmailSuffix":"test1.com"},{"Name":"企业2","EmailSuffix":"test2.com"},{"Name":"企业3",
	 * "EmailSuffix":"test3.com"}]
	 */

	public int Version;
	/**
	 * Name : 企业1
	 * EmailSuffix : test1.com
	 */

	public List<EnterpriseListBean> EnterpriseList;

	public static class EnterpriseListBean implements Serializable {
		public String Name;
		public String EmailSuffix;
	}
}
