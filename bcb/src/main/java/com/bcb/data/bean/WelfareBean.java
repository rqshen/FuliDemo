package com.bcb.data.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by Ray on 2016/5/13.
 *
 * @desc 每日福利实体
 */
public class WelfareBean extends DataSupport {
    @Column(unique = true, defaultValue = "unknown")
    private String username;//用户名
    private String openDate;//领取时间
    private String value;//加息值

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
