package com.bcb.data.bean.project;

import com.bcb.data.bean.AssetAuditContentBean;

import java.io.Serializable;
import java.util.List;

/**
 * 普通项目实体
 *
 * @author sun
 */
public class SimpleProjectDetail implements Serializable {
    /**
     *     "Amount": 5000,
     "ApplyBeginTime": "2016-08-03 17:11:05",
     "ApplyEndTime": "2016-08-10 17:11:05",
     "Balance": 3800,
     "CompanyName": "百财帮",
     "CompanyUrl": "http://cnt.flh001.com/053c0b4c-b488-44fa-ab24-a64c00bca8d4",
     "CouponType": 0,
     "Duration": 1080,
     "DurationExchangeType": 1,
     "HaploidAmount": 0,
     "InterestTakeDate": "2016-08-11 17:11:05",
     "InvestLeader": "",
     "Name": "WXMGRJK201608031000",
     "PackageId": "ee445bda-1554-437e-9f9f-a657011b3215",
     "PackageToken": "Q+DFxUQnIzvUcYIHe2f6/ej5HOkdK7KfxVFFwXDCBdPO+Nda2SVGAUTbwaryz2WjZsp4kqeOAh11om4W1RqZEQqyqO/Ys4htxAOL4U51Czn4niYvRMv2eQ==",
     "PageUrl": "http://ttwap.100cb.cn/package/detail?packageId=ee445bda-1554-437e-9f9f-a657011b3215",
     "PayEndDate": "2019-07-27 17:11:05",
     "PaymentType": "等额本金",
     "ProcessPercent": 24,
     "Rate": 7.5,
     "SingletonAmount": 100,
     "StartingAmount": 100,
     "Status": 20
     */
    public String PackageId;//项目Id
    public String Name;//项目名称
    public float Rate;//	年化利率

    public float Amount;//融资总金额······
    public float Balance;//剩余融资金额···········
    public int Duration;//融资期限
    public int DurationExchangeType;//天标（1）月标（2）········
    public String ApplyBeginTime;//投标开始时间
    public String ApplyEndTime;//	投标结束时间·············
    public String PayEndDate;//	项目到期时间
    public String InterestTakeDate;//	起息日
    public String PaymentType;//	还款方式············
    public float StartingAmount;//起投金额
    public float SingletonAmount;//	单笔限额
    public float ProcessPercent;//融资进度（百分比）
    public int Status;//	项目状态
    public int CouponType;//支持使用券类型
    public String PackageToken;//项目Token(投标时回传)
    public String CompanyName;//	来源公司··········
    public String CompanyUrl;//来源公司官网地址
    public String InvestLeader;//领投人
    public List<AssetAuditContentBean> AssetAuditContent;//	增信内容
    public String PageUrl;//H5详情页面地址

    public float RewardRate;
    public float PreInterest;//10000元预期收益
    public String RewardRateDescn;
    public String Code;
    public int Period;
    public int RateType;

    public String InterestTakeType;
    public String AuditDate;


    public String PackageTypeId;
    public boolean Closed;

    public String PreheatTime;

    public float HaploidAmount;
    public String UserId;
    public String CreateDate;
    public float BorrowerCreditPoint;
    public String BorrowerName;

    public String Department;
    public String LoanUsage;

}
