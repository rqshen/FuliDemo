package com.bcb.data.bean;

public class HousesRecordsBean {
	private String HouseId;
	private String Name;
	private String Title;
	private String Developer;
	private String Address;
	private float Discount;
	private float DiscountAmount;
	private int DiscountNumber;
	private int HasDiscountNumber;
	private String LogoUrl;

	public String getHouseId() {
		return HouseId;
	}

	public void setHouseId(String houseId) {
		HouseId = houseId;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDeveloper() {
		return Developer;
	}

	public void setDeveloper(String developer) {
		Developer = developer;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public float getDiscount() {
		return Discount;
	}

	public void setDiscount(float discount) {
		Discount = discount;
	}

	public float getDiscountAmount() {
		return DiscountAmount;
	}

	public void setDiscountAmount(float discountAmount) {
		DiscountAmount = discountAmount;
	}

	public int getDiscountNumber() {
		return DiscountNumber;
	}

	public void setDiscountNumber(int discountNumber) {
		DiscountNumber = discountNumber;
	}

	public int getHasDiscountNumber() {
		return HasDiscountNumber;
	}

	public void setHasDiscountNumber(int hasDiscountNumber) {
		HasDiscountNumber = hasDiscountNumber;
	}

	public String getLogoUrl() {
		return LogoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		LogoUrl = logoUrl;
	}

}