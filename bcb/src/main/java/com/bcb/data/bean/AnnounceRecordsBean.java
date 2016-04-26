package com.bcb.data.bean;

import java.io.Serializable;

/**
 * Created by cain on 16/2/17.
 */
public class AnnounceRecordsBean implements Serializable {
    public boolean loadStatus = false;
    public String PackageId;        //项目Id
    public String Name;             //项目名称
    public int Duration;            //融资期限
    public int DurationExchangeType;//天标月标
    public float Rate;  //年化利率
    public float Amount;    //总金额
    public String Source;   //来源
    public int PredictCount;    //预约人数

    public String getPackageId() {
        return PackageId;
    }

    public void setPackageId(String packageId) {
        PackageId = packageId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public int getDurationExchangeType() {
        return DurationExchangeType;
    }

    public void setDurationExchangeType(int durationExchangeType) {
        DurationExchangeType = durationExchangeType;
    }

    public float getRate() {
        return Rate;
    }

    public void setRate(float rate) {
        Rate = rate;
    }

    public float getAmount() {
        return Amount;
    }

    public void setAmount(float amount) {
        Amount = amount;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public int getPredictCount() {
        return PredictCount;
    }

    public void setPredictCount(int predictCount) {
        PredictCount = predictCount;
    }
}
