package com.bcb.data.bean.transaction;

import java.io.Serializable;

/**
 * Created by cain on 16/4/6.
 */
public class MoneyWithdrawDetail implements Serializable {

    public float ProcedureFee;//提现手续费
    public String BankSerialNo;//支付流水
    public String CouponId; //提现券ID，如果存在则表示使用了提现券


//    public int Status;  //提现状态
//    public String BankName; //银行名称
//    public String IdCard;   //身份证号码
//    public float ProcedureFeeAmount;    //提现手续费
//
//
//    public int getStatus() {
//        return Status;
//    }
//
//    public void setStatus(int status) {
//        Status = status;
//    }
//
//    public String getBankName() {
//        return BankName;
//    }
//
//    public void setBankName(String bankName) {
//        BankName = bankName;
//    }
//
//    public String getIdCard() {
//        return IdCard;
//    }
//
//    public void setIdCard(String idCard) {
//        IdCard = idCard;
//    }
//
//    public float getProcedureFeeAmount() {
//        return ProcedureFeeAmount;
//    }
//
//    public void setProcedureFeeAmount(float procedureFeeAmount) {
//        ProcedureFeeAmount = procedureFeeAmount;
//    }
//
//    public String getCouponId() {
//        return CouponId;
//    }
}
