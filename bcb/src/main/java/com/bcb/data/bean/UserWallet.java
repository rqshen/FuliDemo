package com.bcb.data.bean;

public class UserWallet {
/*
参数	类型	说明
TotalAsset	float	总资产
BalanceAmount	float	可用余额
FreezeAmount	float	冻结金额
LeftPrincipal	float	待收本金
LeftInterest	float	待收利息
 */

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
//    public double TotalInterest;

    public double 	InvestAmount;//累计投资金额
    public double 	InvestIncome ;//累计投资收益
    public double 	RechargeAmount;//累计充值金额
    public double 	WithdrawAmount;//累计提现金额
    public double 	InvestingAmount ;//在投本金
    public double 	SecurityAmount;//保证金


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
}
