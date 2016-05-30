package com.bcb.data.bean.project;

import com.bcb.data.bean.AssetAuditContentBean;

import java.io.Serializable;
import java.util.List;

/**
 * 普通项目实体
 * @author sun
 * 
 */
public class SimpleProjectDetail implements Serializable {

	public String PackageId;
	public String Name;
	public String Code;
	public float Rate;
	public float RewardRate;
	public String RewardRateDescn;
	public int Duration;
	public int Period;
	public int RateType;
	public String PaymentType;
	public String InterestTakeType;
	public String AuditDate;
	public String PayEndDate;
	public float AmountTotal;
	public float AmountBalance;
	public int Status;
	public String PackageTypeId;
	public boolean Closed;
	public String ApplyBeginTime;
	public String ApplyEndTime;
	public String PreheatTime;
	public float StartingAmount;
	public float SingletonAmount;
	public float HaploidAmount;
	public String UserId;
	public String CreateDate;
	public float BorrowerCreditPoint;
	public String BorrowerName;
	public String PageUrl;
	public String Department;
	public String LoanUsage;	
	public int DurationExchangeType;
	public int CouponType;
    public String CompanyName;
    public String CompanyUrl;
	public String InvestLeader;
	public List<AssetAuditContentBean> AssetAuditContent;
}
