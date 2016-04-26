package com.bcb.data.bean.loan;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cain on 16/1/7.
 */
public class LoanRequestInfoBean implements Serializable {
    public String AggregateId;  //可编辑ID
    public int Amount;  //借款金额
    public String CouponId; //借款使用的赠送券ID
    public int CouponAmount;    //赠送券金额
    public String CouponDescn;  //借款所使用的赠券使用条件描述
    public int Period;  //还款期数
    public boolean UseSubsidy;    //是否申请福利补贴
    public boolean UseCoupon;   // 是否使用了利息抵扣券
    public int LoanType;    //借款用途
    public int LoanTimeType;    //借款时间
    public List<LoanTypeListBean> LoanTypeTable;//借款用途
    public List<RateTableBean> RateTable;//借款利率表
}
