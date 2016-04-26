package com.bcb.data.bean.loan;

/**
 * Created by cain on 16/1/7.
 */
public class LoanPeriodWithRateBean {
    public int Period; //借款期限
    public float Rate; //利率

    public LoanPeriodWithRateBean() {
        Period = 0;
        Rate = 0;
    }

    public LoanPeriodWithRateBean(int period, float rate) {
        Period = period;
        Rate = rate;
    }

    @Override
    public String toString() {
        return Period + "期";
    }
}
