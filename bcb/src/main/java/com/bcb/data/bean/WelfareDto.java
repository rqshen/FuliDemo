package com.bcb.data.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Ray on 2016/7/4.
 *
 * @desc 每日日福利DTO
 */
public class WelfareDto implements Serializable {
    private List<Map<String,String>> JoinList;//滚动文字
    private float TotalInterest;//累计收益
    private int TotalPopulation;//参与人数
    private float Rate;//加息利率

    public WelfareDto() {
    }

    public List<Map<String, String>> getJoinList() {
        return JoinList;
    }

    public void setJoinList(List<Map<String, String>> joinList) {
        JoinList = joinList;
    }

    public float getTotalInterest() {
        return TotalInterest;
    }

    public void setTotalInterest(float totalInterest) {
        TotalInterest = totalInterest;
    }

    public int getTotalPopulation() {
        return TotalPopulation;
    }

    public void setTotalPopulation(int totalPopulation) {
        TotalPopulation = totalPopulation;
    }

    public float getRate() {
        return Rate;
    }

    public void setRate(float rate) {
        Rate = rate;
    }

    @Override
    public String toString() {
        return "WelfareDto{" +
                "JoinList=" + JoinList +
                ", TotalInterest=" + TotalInterest +
                ", TotalPopulation=" + TotalPopulation +
                ", Rate=" + Rate +
                '}';
    }
}
