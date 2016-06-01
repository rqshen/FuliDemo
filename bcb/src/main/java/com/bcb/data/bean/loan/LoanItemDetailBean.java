package com.bcb.data.bean.loan;

/**
 * Created by cain on 16/1/13.
 */
public class LoanItemDetailBean {
    public String UniqueId; //借款编号
    public String LoanType; //借款用途
    public float  Amount;   //借款金额
    public String Rate;     //利率
    public String Duration; //期限
    public int LoanPeriod;  //还款期数
    public float LatePenaltyRate; //逾期罚息
    public int Status;      //状态
    public String CreateDate;   //创建时间
    public String ExpectRepayDate;  //期望到款日期
    public String PaymentType;  //还款类型
    public float ServiceFee;    //平台服务费

    public boolean AllowUpload;//是否允许上传

    public String NextPayDate;  //下一个还款日期
    public float NextPayAmount; //下一次还款金额
}
