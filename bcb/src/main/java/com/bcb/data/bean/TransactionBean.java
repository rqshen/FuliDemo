package com.bcb.data.bean;

/**
 * Created by cain on 15/12/28.
 */
public class TransactionBean {
    private float Amount;
    private String Descn;
    private String Order;
    private String Time;
    private String CategoryName;
    private int Type;

    public float getAmount() {
        return Amount;
    }

    public void setAmount(float amount) {
        Amount = amount;
    }

    public String getDescn() {
        return Descn;
    }

    public void setDescn(String descn) {
        Descn = descn;
    }

    public String getOrder() {
        return Order;
    }

    public void setOrder(String order) {
        Order = order;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }
}
