package com.bcb.data.bean;

public class HotStationRecordsBean {
	private String CompanyId;
	private String Name;
	private String ShortName;

	private String ProvinceName;
	private String CityName;
	private String DistrictName;

	private int PackageCount;
	private float MaxRate;
	private float MinRate;
	private float AmountBalance;
	private int SafetyLevel;
	private String PageUrl;
	private int HistoryPackageCount;

	public String getCompanyId() {
		return CompanyId;
	}

	public void setCompanyId(String companyId) {
		CompanyId = companyId;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getShortName() {
		return ShortName;
	}

	public void setShortName(String shortName) {
		ShortName = shortName;
	}

	public String getProvinceName() {
		return ProvinceName;
	}

	public void setProvinceName(String provinceName) {
		ProvinceName = provinceName;
	}

	public String getCityName() {
		return CityName;
	}

	public void setCityName(String cityName) {
		CityName = cityName;
	}

	public String getDistrictName() {
		return DistrictName;
	}

	public void setDistrictName(String districtName) {
		DistrictName = districtName;
	}

	public int getPackageCount() {
		return PackageCount;
	}

	public void setPackageCount(int packageCount) {
		PackageCount = packageCount;
	}

	public float getMaxRate() {
		return MaxRate;
	}

	public void setMaxRate(float maxRate) {
		MaxRate = maxRate;
	}

	public float getMinRate() {
		return MinRate;
	}

	public void setMinRate(float minRate) {
		MinRate = minRate;
	}

	public float getAmountBalance() {
		return AmountBalance;
	}

	public void setAmountBalance(float amountBalance) {
		AmountBalance = amountBalance;
	}

	public int getSafetyLevel() {
		return SafetyLevel;
	}

	public void setSafetyLevel(int safetyLevel) {
		SafetyLevel = safetyLevel;
	}

	public String getPageUrl() {
		return PageUrl;
	}

	public void setPageUrl(String pageUrl) {
		PageUrl = pageUrl;
	}

	public int getHistoryPackageCount() {
		return HistoryPackageCount;
	}

	public void setHistoryPackageCount(int historyPackageCount) {
		HistoryPackageCount = historyPackageCount;
	}
}
