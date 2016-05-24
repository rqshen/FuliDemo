package com.bcb.data.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by Ray on 2016/5/23.
 *
 * @desc 用户额外信息
 */
public class UserExtraInfo extends DataSupport {

    @Column(unique = true, defaultValue = "unknown")
    private String username;//用户名
    private String model;//机型
    private String imei;//IMEI
    private String network;//网络
    private String location;//地理位置

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
