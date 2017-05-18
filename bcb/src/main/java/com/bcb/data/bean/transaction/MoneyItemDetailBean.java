package com.bcb.data.bean.transaction;

import java.io.Serializable;

/**
 * Created by cain on 16/4/6.
 */
public class MoneyItemDetailBean implements Serializable {
    public String TransNo;
    public String Title;   //标题
    public String Status;  //交易状态

    public String StatusDescn;  //交易详情状态描述
    public String CreateDate;    //交易时间

    public MoneyLoanDetail DebtExt;    //借款信息
    public MoneyInvestorDetail InvestorRepay;  //回款信息
    public MoneyRepaymentDetail LoanerRepayExt;    //还款信息
    public MoneyWithdrawDetail WithdrawExt;    //提现信息
    public MoneyRechargeDetail RechargeExt;//充值信息

    public String BillId;//订单号
    public float Amount;   //交易金额
    public int TopCategoryId;//交易类型
    public int Type;//收入支付类型（1收入0支出）
    public String Time;//操作时间

    //充值
    public class MoneyRechargeDetail {
        public String BankSerialNo;//支流水
        public String ReceivedDate;   //到账时间
        public float ProcedureFee;  //充值手续费
    }


    //提现
    public class MoneyWithdrawDetail implements Serializable {

        public float ProcedureFee;//提现手续费
        public String BankSerialNo;//支付流水
        public String CouponId; //提现券ID，如果存在则表示使用了提现券

    }

    //还款信息
    public class MoneyRepaymentDetail implements Serializable {
        public float Principal; //回款本金
        public String AssetName;//资产名字
        public float SubsidyAmount;   //	赠券补贴
        public float PenaltyAmount;   //	违约金
        public float Interest;   //利息金额
        public String PayDate;  //回款时间
        public int Period;  //回款期数
//    public float ServiceFeeAmount; //信息服务费

        public float getPrincipal() {
            return Principal;
        }

        public void setPrincipal(float principal) {
            Principal = principal;
        }

        public String getAssetName() {
            return AssetName;
        }

        public void setAssetName(String assetName) {
            AssetName = assetName;
        }

        public float getInterest() {
            return Interest;
        }

        public void setInterest(float interest) {
            Interest = interest;
        }

        public String getPayDate() {
            return PayDate;
        }

        public void setPayDate(String payDate) {
            PayDate = payDate;
        }

        public int getPeriod() {
            return Period;
        }

        public void setPeriod(int period) {
            Period = period;
        }

//    public float getServiceFeeAmount() {
//        return ServiceFeeAmount;
//    }
//
//    public void setServiceFeeAmount(float serviceFeeAmount) {
//        ServiceFeeAmount = serviceFeeAmount;
//    }
    }

    //回款信息
    public class MoneyInvestorDetail implements Serializable {
        public float Principal; //回款本金
        public float ServiceFee; //信息服务费
        public String PackageName;  //项目名称
        public float Interest;  //利息金额
        public String PayDate;  //回款时间
        public int Period;  //回款期数

        public float getPrincipal() {
            return Principal;
        }

        public void setPrincipal(float principal) {
            Principal = principal;
        }

        public float getServiceFeeAmount() {
            return ServiceFee;
        }

        public void setServiceFeeAmount(float serviceFeeAmount) {
            ServiceFee = serviceFeeAmount;
        }

        public String getPackageName() {
            return PackageName;
        }

        public void setPackageName(String packageName) {
            PackageName = packageName;
        }

        public float getInterest() {
            return Interest;
        }

        public void setInterest(float interest) {
            Interest = interest;
        }

        public String getPayDate() {
            return PayDate;
        }

        public void setPayDate(String payDate) {
            PayDate = payDate;
        }

        public int getPeriod() {
            return Period;
        }

        public void setPeriod(int period) {
            Period = period;
        }
    }


    //借款信息
    public class MoneyLoanDetail implements Serializable {
        public float LoanAmount;   //放款金额
        public String PayDate;     //放款时间
        public int Period; //总期数
        public float SecurityDepositFee; //保证金金额
        public float ServiceFee;     //信息服务费

        public float getLoanAmount() {
            return LoanAmount;
        }

        public void setLoanAmount(float loanAmount) {
            LoanAmount = loanAmount;
        }

        public String getPayDate() {
            return PayDate;
        }

        public void setPayDate(String payDate) {
            PayDate = payDate;
        }

        public int getPeriod() {
            return Period;
        }

        public void setPeriod(int period) {
            Period = period;
        }

        public float getSecurityDepositFeeAmount() {
            return SecurityDepositFee;
        }

        public void setSecurityDepositFeeAmount(float securityDepositFeeAmount) {
            SecurityDepositFee = securityDepositFeeAmount;
        }

        public float getServiceFeeAmount() {
            return ServiceFee;
        }

        public void setServiceFeeAmount(float serviceFeeAmount) {
            ServiceFee = serviceFeeAmount;
        }
    }
}
