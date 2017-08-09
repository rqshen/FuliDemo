package com.bcb.data.bean;

import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2017/2/28 17:47
 */
public class TZJLbean {

    /**
     * ChickenInterest : 0
     * ChickenPrincipal : 0
     * InvetDetail : {"PageNow":1,"PageSize":10,"Records":[{"CreateDate":"2017-02-28","Duration":"天","OrderAmount":10000,"OrderNo":"a0bce6261f6e0b3c0000030c","PackageName":"福鸡宝201704036148","Rate":7,"StatusTips":"将于2017-04-03开始计息","TotalDays":0}],"TotalCount":1}
     * MonkeyInterest : 0
     * MonkeyPrincipal : 10000
     * OriginalInterest : 0
     * OriginalPrincipal : 0
     * PackInterest : 0
     * PackPrincipal : 10000
     */

    public float ChickenInterest;
    public float ChickenPrincipal;
    public InvetDetailBean InvetDetail;
    public float PackInterest;
    public float PackPrincipal;

    public static class InvetDetailBean {
        /**
         * PageNow : 1
         * PageSize : 10
         * Records : [{"CreateDate":"2017-02-28","Duration":"天","OrderAmount":10000,"OrderNo":"a0bce6261f6e0b3c0000030c","PackageName":"福鸡宝201704036148","Rate":7,"StatusTips":"将于2017-04-03开始计息","TotalDays":0}]
         * TotalCount : 1
         */

        public int PageNow;
        public int PageSize;
        public int TotalCount;
        public List<RecordsBean> Records;

        public static class RecordsBean {
            /**
             * CreateDate : 2017-02-28
             * Duration : 天
             * OrderAmount : 10000
             * OrderNo : a0bce6261f6e0b3c0000030c
             * PackageName : 福鸡宝201704036148
             * Rate : 7
             * StatusTips : 将于2017-04-03开始计息
             * TotalDays : 0
             */

            public String CreateDate;
            public String Duration;
            public float OrderAmount;
            public String OrderNo;
            public String PackageName;
            public String StatusTips;
            public int TotalDays;
            public float AuditingAmount;

        }
    }
}
