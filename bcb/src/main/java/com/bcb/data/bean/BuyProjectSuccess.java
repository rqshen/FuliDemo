package com.bcb.data.bean;

import java.io.Serializable;

public class BuyProjectSuccess implements Serializable {

	private static final long serialVersionUID = 1L;
	private float OrderAmount;
	private int Duration;
	private int DurationExchangeType;
	private float InterestAmount;		//预期收益
	private float RewardInterestAmount; //额外奖励
	private String RewardRateDescn;		//奖励描述

	public float getOrderAmount() {
		return OrderAmount;
	}

	public void setOrderAmount(float orderAmount) {
		OrderAmount = orderAmount;
	}

	public int getDuration() {
		return Duration;
	}

	public void setDuration(int duration) {
		Duration = duration;
	}

	public int getDurationExchangeType() {
		return DurationExchangeType;
	}

	public void setDurationExchangeType(int durationExchangeType) {
		DurationExchangeType = durationExchangeType;
	}

	public float getInterestAmount() {
		return InterestAmount;
	}

	public void setInterestAmount(float interestAmount) {
		InterestAmount = interestAmount;
	}

	public float getRewardInterestAmount() {
		return RewardInterestAmount;
	}

	public void setRewardInterestAmount(float rewardInterestAmount) {
		RewardInterestAmount = rewardInterestAmount;
	}

	public String getRewardRateDescn() {
		return RewardRateDescn;
	}

	public void setRewardRateDescn(String rewardRateDescn) {
		RewardRateDescn = rewardRateDescn;
	}

}
