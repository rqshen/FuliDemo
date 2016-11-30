package com.bcb.data.bean.loan;

import java.io.Serializable;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/11/29 21:07
 */
public class LoanKindBean implements Serializable{

	/**
	 * LoanKindId : 1
	 * BannerUrl : test/test1.png
	 * Title : 名企金领贷
	 * SummaryDesc : 适用于国家公务员，500强企业员工，低利息高额度。
	 * DetailDesc : 产品介绍：
	 * 1、适用于适用于国家公务员，500强企业高管等，最高可贷30万。
	 * 2、年利率低至8.5%，每月归还本息。
	 */

	public int LoanKindId;
	public String BannerUrl;
	public String Title;
	public String SummaryDesc;
	public String DetailDesc;
}
