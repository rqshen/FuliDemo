package com.bcb.data.bean.loan;

import java.io.Serializable;

/**
 * Created by cain on 16/1/7.
 * 借款利率表
 */
public class RateTableBean implements Serializable {
    public int Period;
    public float Rate;
    public int Duration;

    public RateTableBean() {
        Period = 0;
        Rate = 0;
        Duration = 0;
    }

    public RateTableBean(int period, float rate, int duration) {
        Period = period;
        Rate = rate;
        Duration = duration;
    }

    public int getPeriod() {
        return Period;
    }

    public float getRate() {
        return Rate;
    }

    public int getDuration() {
        return Duration;
    }
}
