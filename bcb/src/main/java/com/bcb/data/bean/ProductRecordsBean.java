package com.bcb.data.bean;

import java.io.Serializable;

public class ProductRecordsBean implements Serializable {

    /**
     * PackageId : cf7ac7a9-2337-4b71-a97c-a64c00eba73a
     * Name : SMYGRJK201607231349
     * Rate : 12
     * Balance : 4600
     * Amount : 5000
     * Duration : 1
     * DurationExchangeType : 2
     * ProcessPercent : 8
     * Status : 20
     */

    public String PackageId;//	项目Id
    public String Name;//项目名称
    public float Rate;//年化利率
    public float Balance;//剩余融资金额
    public float Amount;//	融资总金额
    public int Duration;//融资期限
    public int DurationExchangeType;//天标（1）月标（2）
    public float ProcessPercent;//	融资进度（百分比）
    public int Status;//
    public String Type;//标类型：prj_package则为普通标 claim_convey则为债权转让标 mon_package为福鸡宝
}
