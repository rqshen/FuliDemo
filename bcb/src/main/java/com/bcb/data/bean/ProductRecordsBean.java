package com.bcb.data.bean;

import java.io.Serializable;

public class ProductRecordsBean implements Serializable {
	private String PackageId;
	private String Name;
	private float AmountTotal;
	private float AmountBalance;
	private String PreheatTime;
	private String ApplyBeginTime;
	private String ApplyEndTime;
	private float Rate;
	private String Department;
	private int Duration;
	private String LoanUsage;
	private String ObligorName;
	private float RewardRate;
	private String RewardRateDescn;
	private int Status;
	private String CompanyId;
	private String CompanyName;
	private String CompanyShortName;
	//标的类型
	private int PackageTypeId;
	private boolean IsCurrentCompany;
	private int DurationExchangeType;
	private int CouponType;
	//描述
	private String LoanSource;

	public String getLoanSource() {
		return LoanSource;
	}

	public void setLoanSource(String loanSource) {
		LoanSource = loanSource;
	}

	public String getPackageId() {
		return PackageId;
	}

	public void setPackageId(String packageId) {
		PackageId = packageId;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public float getAmountTotal() {
		return AmountTotal;
	}

	public void setAmountTotal(float amountTotal) {
		AmountTotal = amountTotal;
	}

	public float getAmountBalance() {
		return AmountBalance;
	}

	public void setAmountBalance(float amountBalance) {
		AmountBalance = amountBalance;
	}

	public String getPreheatTime() {
		return PreheatTime;
	}

	public void setPreheatTime(String preheatTime) {
		PreheatTime = preheatTime;
	}

	public String getApplyBeginTime() {
		return ApplyBeginTime;
	}

	public void setApplyBeginTime(String applyBeginTime) {
		ApplyBeginTime = applyBeginTime;
	}

	public String getApplyEndTime() {
		return ApplyEndTime;
	}

	public void setApplyEndTime(String applyEndTime) {
		ApplyEndTime = applyEndTime;
	}

	public float getRate() {
		return Rate;
	}

	public void setRate(float rate) {
		Rate = rate;
	}

	public String getDepartment() {
		return Department;
	}

	public void setDepartment(String department) {
		Department = department;
	}

	public int getDuration() {
		return Duration;
	}

	public void setDuration(int duration) {
		Duration = duration;
	}

	public String getLoanUsage() {
		return LoanUsage;
	}

	public void setLoanUsage(String loanUsage) {
		LoanUsage = loanUsage;
	}

	public String getObligorName() {
		return ObligorName;
	}

	public void setObligorName(String obligorName) {
		ObligorName = obligorName;
	}

	public float getRewardRate() {
		return RewardRate;
	}

	public void setRewardRate(float rewardRate) {
		RewardRate = rewardRate;
	}

	public String getRewardRateDescn() {
		return RewardRateDescn;
	}

	public void setRewardRateDescn(String rewardRateDescn) {
		RewardRateDescn = rewardRateDescn;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getCompanyId() {
		return CompanyId;
	}

	public void setCompanyId(String companyId) {
		CompanyId = companyId;
	}

	public String getCompanyName() {
		return CompanyName;
	}

	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}

	public String getCompanyShortName() {
		return CompanyShortName;
	}

	public void setCompanyShortName(String companyShortName) {
		CompanyShortName = companyShortName;
	}

	public int getPackageTypeId() {
		return PackageTypeId;
	}

	public void setPackageTypeId(int packageTypeId) {
		PackageTypeId = packageTypeId;
	}

	public boolean isIsCurrentCompany() {
		return IsCurrentCompany;
	}

	public void setIsCurrentCompany(boolean isCurrentCompany) {
		IsCurrentCompany = isCurrentCompany;
	}

	public int getDurationExchangeType() {
		return DurationExchangeType;
	}

	public void setDurationExchangeType(int durationExchangeType) {
		DurationExchangeType = durationExchangeType;
	}

	public int getCouponType() {
		return CouponType;
	}

	public void setCouponType(int couponType) {
		CouponType = couponType;
	}

}
