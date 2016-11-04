package com.bcb.data.bean.transaction;

import java.io.Serializable;

/**
 * Created by cain on 16/4/6.
 */
public class MoneyWithdrawDetail implements Serializable {

    public float ProcedureFee;//提现手续费
    public String BankSerialNo;//支付流水
    public String CouponId; //提现券ID，如果存在则表示使用了提现券

}
