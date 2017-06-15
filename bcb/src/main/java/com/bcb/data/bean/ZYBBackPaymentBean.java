package com.bcb.data.bean;

import java.util.List;

/**
 * Created by ruiqin.shen
 * 类说明：
 */

public class ZYBBackPaymentBean {

    /**
     * DonePrincialInterest : 1500.25
     * PePrincipalInteres : 3.5
     * RepaymentPlan : [{"CraeteDate":"2017-06-13 02:43:04","Descn":"复投不成功","Interest":0.25,"Principal":1500}]
     */

    private double DonePrincialInterest;
    private double PePrincipalInteres;
    private List<RepaymentPlanBean> RepaymentPlan;

    public double getDonePrincialInterest() {
        return DonePrincialInterest;
    }

    public void setDonePrincialInterest(double DonePrincialInterest) {
        this.DonePrincialInterest = DonePrincialInterest;
    }

    public double getPePrincipalInteres() {
        return PePrincipalInteres;
    }

    public void setPePrincipalInteres(double PePrincipalInteres) {
        this.PePrincipalInteres = PePrincipalInteres;
    }

    public List<RepaymentPlanBean> getRepaymentPlan() {
        return RepaymentPlan;
    }

    public void setRepaymentPlan(List<RepaymentPlanBean> RepaymentPlan) {
        this.RepaymentPlan = RepaymentPlan;
    }

    public static class RepaymentPlanBean {
        /**
         * CraeteDate : 2017-06-13 02:43:04
         * Descn : 复投不成功
         * Interest : 0.25
         * Principal : 1500
         */

        private String CraeteDate;
        private String Descn;
        private double Interest;
        private int Principal;

        public String getCraeteDate() {
            return CraeteDate.substring(0, CraeteDate.indexOf(" "));
        }

        public void setCraeteDate(String CraeteDate) {
            this.CraeteDate = CraeteDate;
        }

        public String getDescn() {
            return Descn;
        }

        public void setDescn(String Descn) {
            this.Descn = Descn;
        }

        public double getInterest() {
            return Interest;
        }

        public void setInterest(double Interest) {
            this.Interest = Interest;
        }

        public int getPrincipal() {
            return Principal;
        }

        public void setPrincipal(int Principal) {
            this.Principal = Principal;
        }
    }
}
