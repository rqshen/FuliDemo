package com.bcb.data.bean;

/**
 * Created by Ray on 2016/7/8.
 *
 * @desc 特权本金
 */
public class PrivilegeMoneyDto {


    public String GoldNo;//特权金编号
    public float Amount;//金额
    public float Income;//预期收益
    public float Rate;//利率
    public int Days;//天数
    public String ExpireDate;//失效日期
    public int Status;//状态（未使用 = 0,收益中 = 1,已使用 = 2,已过期 = 3）
}
