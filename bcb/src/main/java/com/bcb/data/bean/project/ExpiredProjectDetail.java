package com.bcb.data.bean.project;

import java.io.Serializable;

/**
 * Created by cain on 16/1/4.
 * 废弃了
 */
public class ExpiredProjectDetail implements Serializable {
    public String PackageId; //项目编号
    public String Name;     //项目名称
    public String Code;      //项目编号
    public float Rate;      //年化利率
    public float RewardRate;    //奖励利率;
    public String RewardRateDescn;  //奖励说明
    public int Duration;        //融资期限
    public int DurationExchangeType;    //天标（1）月标（2）
    public int CouponType;      //体验券（1）现金券（2）
//    public int Period;          //还款期数
//    public String RateType;     //利率类型
    public String PaymentType;  //还款方式
//    public String InterestTakeType; //利息生效方式
//    public String AuditDate;    //资金审核时间
//    public String PayEndDate;   //还款结束时间
    public float AmountTotal;     //融资总金额
    public float AmountBalance; //剩余融资金额
//    public String PackageTypeId;    //项目类型
//    public String ApplyBeginTime;   //投标开始时间
    public String ApplyEndTime;     //投标结束时间
//    public String PreheatTime;      //预热时间
    public float StartingAmount;   //起投金额
//    public float SingletonAmount;   //单笔金额
//    public float HaploidAmount;     //单倍金额
    public int Status;          //项目状态
    public String PackageToken;          //项目状态
//    public List<AssetAuditContentBean> AssetAuditContent;
}
