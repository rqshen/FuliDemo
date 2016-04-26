package com.bcb.data.bean;

public class UserWallet {

    //总资产
    public double TotalAsset;

    //冻结金额
    public double FreezeAmount;

	/**
	 * 账户余额
	 */
	public double BalanceAmount;
	/**
	 * 待回款金额
	 */
	public double IncomingMoney;
	/**
	 * 代收本金
	 */
	public double LeftPrincipal;
	/**
	 * 代收利息
	 */
	public double LeftInterest;
    /**
     * 今日收益
     */
    public double TodayInterest;
	/**
	 * 累计收益
	 */
	public double TotalInterest;

    public double getTotalAsset() {
        return TotalAsset;
    }

    public void setTotalAsset(double totalAsset) {
        TotalAsset = totalAsset;
    }

    public double getFreezeAmount() {
        return FreezeAmount;
    }

    public void setFreezeAmount(double freezeAmount) {
        FreezeAmount = freezeAmount;
    }

    public double getBalanceAmount() {
        return BalanceAmount;
    }

    public void setBalanceAmount(double balanceAmount) {
        BalanceAmount = balanceAmount;
    }

    //待回款金额 = 代收本金 + 代收利息
    public double getIncomingMoney() {
        return LeftPrincipal + LeftInterest;
    }

    public void setIncomingMoney(double incomingMoney) {
        IncomingMoney = incomingMoney;
    }

    public double getTodayInterest() {
        return TodayInterest;
    }

    public void setTodayInterest(double todayInterest) {
        TodayInterest = todayInterest;
    }

    public double getLeftPrincipal() {
        return LeftPrincipal;
    }

    public void setLeftPrincipal(double leftPrincipal) {
        LeftPrincipal = leftPrincipal;
    }

    public double getLeftInterest() {
        return LeftInterest;
    }

    public void setLeftInterest(double leftInterest) {
        LeftInterest = leftInterest;
    }

    public double getTotalInterest() {
        return TotalInterest;
    }

    public void setTotalInterest(double totalInterest) {
        TotalInterest = totalInterest;
    }
}
