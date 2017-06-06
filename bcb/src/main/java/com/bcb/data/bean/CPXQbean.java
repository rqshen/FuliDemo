package com.bcb.data.bean;

import java.io.Serializable;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2017/2/28 09:29
 */
public class CPXQbean implements Serializable {

    /**
     * PackageId : 96060092-1813-4021-8861-a51800f32f7d
     * Name : 这是借款盒子a
     * Rate : 7.5
     * Balance : 10000
     * Amount : 601
     * Duration : 1
     * DurationExchangeType : 1
     * ApplyBeginTime : 2015-09-22 11:58:03
     * ApplyEndTime : 2015-09-24 11:58:03
     * InterestTakeDate : 2016-07-27
     * HoldingDate : 2016-09-27
     * PaymentType : 一次性还本付息
     * StartingAmount : 100
     * HaploidAmount : 100
     * SingletonAmount : 100
     * CouponType : 0
     * ProcessPercent : 100
     * Status : 40
     * PageUrl : http://192.168.20.14/package/detail?packageId=96060092-1813-4021-8861-a51800f32f7d
     * PackageToken : e69tea
     * MinPreInterest : 12.5
     * MaxPreInterest : 12.5
     * MixDuration : 3
     * MaxDuration : 36
     */

    public String PackageId;
    public String Name;
    public float Rate;
    public float Balance;
    public float Amount;
    public int Duration;
    public int DurationExchangeType;
    public String ApplyBeginTime;
    public String ApplyEndTime;
    public String InterestTakeDate;
    public String HoldingDate;
    public String PaymentType;
    public float StartingAmount;
    public float HaploidAmount;//*************
    public float SingletonAmount;
    public int CouponType;
    public float ProcessPercent;
    public int Status;
    public String PageUrl;
    public String PackageToken;
    public float MinPreInterest;
    public float MaxPreInterest;
    public int MixDuration;
    public int MaxDuration;
}
