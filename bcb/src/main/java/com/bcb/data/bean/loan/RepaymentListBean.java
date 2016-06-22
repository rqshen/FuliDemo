package com.bcb.data.bean.loan;

import java.util.List;

/**
 * Created by cain on 16/1/15.
 */
public class RepaymentListBean {
    public int PageNow;
    public int PageSize;
    public int TotalCount;
    public List<RepaymentRecordsBean> Records;

    @Override
    public String toString() {
        return "RepaymentListBean{" +
                "PageNow=" + PageNow +
                ", PageSize=" + PageSize +
                ", TotalCount=" + TotalCount +
                ", Records=" + Records +
                '}';
    }
}
