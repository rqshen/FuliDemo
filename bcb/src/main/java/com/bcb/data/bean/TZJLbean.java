package com.bcb.data.bean;

import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2017/2/28 17:47
 */
public class TZJLbean {

    /**
     * OriginalInterest : 63
     * OriginalPrincipal : 53
     * PackInterest : 666
     * PackPrincipal : 695
     * InvetDetail : {"pageNow":1,"pageSize":10,"totalCount":3,"records":[{"OrderAmount":100,"OrderNo":"22965151021174824210","StatusTips":"将于xxxx日回款收益","PackageName":"投资测试","PayTime":"2015-10-21
     * 12:17:49","Rate":6.8,"TotalDays":6,"Duration":"天"}]}
     */

    public float OriginalInterest;//散标的应收利息
    public float OriginalPrincipal;//散标的在投本金
    public float PackInterest;//打包标的应收利息
    public float PackPrincipal;//打包标的在投本金
    public float ChickenInterest;//周盈宝应收利息
    public float ChickenPrincipal;//周盈宝在投本金
    public InvetDetailBean InvetDetail;//投资记录

    public static class InvetDetailBean {
        /**
         * pageNow : 1
         * pageSize : 10
         * totalCount : 3
         * records : [{"OrderAmount":100,"OrderNo":"22965151021174824210","StatusTips":"将于xxxx日回款收益","PackageName":"投资测试","PayTime":"2015-10-21 12:17:49","Rate":6.8,"TotalDays":6,"Duration":"天"}]
         */

        public int PageNow;
        public int PageSize;
        public int TotalCount;
        public List<RecordsBean> Records;

        public static class RecordsBean {
            /**
             * OrderAmount : 100
             * OrderNo : 22965151021174824210
             * StatusTips : 将于xxxx日回款收益
             * PackageName : 投资测试
             * PayTime : 2015-10-21 12:17:49
             * Rate : 6.8
             * TotalDays : 6
             * Duration : 天
             */

            public float OrderAmount;//投资金额
            public String OrderNo;//订单号
            public String StatusTips;//将于xxx回款
            public String PackageName;
            public String PayTime;
            public String CreateDate;
            public float Rate;
            public int TotalDays;
            public String Duration;//天或月

        }
    }
}
