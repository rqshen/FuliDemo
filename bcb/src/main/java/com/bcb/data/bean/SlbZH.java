package com.bcb.data.bean;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/8/12 15:32
 */
public class SlbZH {

    /**
     * TotalAsset : 0
     * TotalProfit : 0
     * YesterdayProfit : 0
     * TotalAsset	float	生利宝余额
     TotalProfit	float	历史累计收益
     YesterdayProfit	float	昨日收益
     */

    private float TotalAsset;
    private float TotalProfit;
    private float YesterdayProfit;

    public float getTotalAsset() {
        return TotalAsset;
    }

    public void setTotalAsset(float TotalAsset) {
        this.TotalAsset = TotalAsset;
    }

    public float getTotalProfit() {
        return TotalProfit;
    }

    public void setTotalProfit(float TotalProfit) {
        this.TotalProfit = TotalProfit;
    }

    public float getYesterdayProfit() {
        return YesterdayProfit;
    }

    public void setYesterdayProfit(float YesterdayProfit) {
        this.YesterdayProfit = YesterdayProfit;
    }
}
