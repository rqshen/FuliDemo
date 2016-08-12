package com.bcb.data.bean;

import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/8/12 18:12
 */
public class SlbList {
    public int PageNow;
    public int PageSize;
    public int TotalCount;
    public List<SlbSyBean> Records;

    /**
     * 描述：
     * 作者：baicaibang
     * 时间：2016/8/12 18:57
     */

    public static class SlbSyBean {
        public float Profit;
        public String ProfitDate;
    }
}
