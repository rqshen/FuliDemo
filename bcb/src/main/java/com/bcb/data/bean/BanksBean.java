package com.bcb.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/8/4 11:49
 */
public class BanksBean implements Parcelable {

    /**
     * BankName : 招商银行
     * BankCode : CMB
     * MaxSingle : 50000
     * MaxDay : 50000
     */

    private String BankName;
    private String BankCode;
    private int MaxSingle;
    private int MaxDay;

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String BankName) {
        this.BankName = BankName;
    }

    public String getBankCode() {
        return BankCode;
    }

    public void setBankCode(String BankCode) {
        this.BankCode = BankCode;
    }

    public int getMaxSingle() {
        return MaxSingle;
    }

    public void setMaxSingle(int MaxSingle) {
        this.MaxSingle = MaxSingle;
    }

    public int getMaxDay() {
        return MaxDay;
    }

    public void setMaxDay(int MaxDay) {
        this.MaxDay = MaxDay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.BankName);
        dest.writeString(this.BankCode);
        dest.writeInt(this.MaxSingle);
        dest.writeInt(this.MaxDay);
    }

    public BanksBean() {
    }

    protected BanksBean(Parcel in) {
        this.BankName = in.readString();
        this.BankCode = in.readString();
        this.MaxSingle = in.readInt();
        this.MaxDay = in.readInt();
    }

    public static final Parcelable.Creator<BanksBean> CREATOR = new Parcelable.Creator<BanksBean>() {
        @Override
        public BanksBean createFromParcel(Parcel source) {
            return new BanksBean(source);
        }

        @Override
        public BanksBean[] newArray(int size) {
            return new BanksBean[size];
        }
    };
}
