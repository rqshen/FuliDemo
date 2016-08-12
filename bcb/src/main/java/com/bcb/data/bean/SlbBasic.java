package com.bcb.data.bean;

import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/8/12 15:17
 */
public class SlbBasic {

    /**
     * PrdRate : 6.3631
     * AnnuRate : 36.343
     */

    private float PrdRate;
    private float AnnuRate;
    /**
     * PrdRate : 0
     * AnnuRate : 0
     * Date : 2016-08-03 00:00:00
     */

    private List<DailyRateListBean> DailyRateList;

    public float getPrdRate() {
        return PrdRate;
    }

    public void setPrdRate(float PrdRate) {
        this.PrdRate = PrdRate;
    }

    public float getAnnuRate() {
        return AnnuRate;
    }

    public void setAnnuRate(float AnnuRate) {
        this.AnnuRate = AnnuRate;
    }

    public List<DailyRateListBean> getDailyRateList() {
        return DailyRateList;
    }

    public void setDailyRateList(List<DailyRateListBean> DailyRateList) {
        this.DailyRateList = DailyRateList;
    }

    public static class DailyRateListBean {
        private float PrdRate;
        private float AnnuRate;
        private String Date;

        public float getPrdRate() {
            return PrdRate;
        }

        public void setPrdRate(float PrdRate) {
            this.PrdRate = PrdRate;
        }

        public float getAnnuRate() {
            return AnnuRate;
        }

        public void setAnnuRate(float AnnuRate) {
            this.AnnuRate = AnnuRate;
        }

        public String getDate() {
            return Date;
        }

        public void setDate(String Date) {
            this.Date = Date;
        }
    }
}
