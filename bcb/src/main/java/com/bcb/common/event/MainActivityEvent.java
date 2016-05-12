package com.bcb.common.event;

/**
 * Created by Ray on 2016/5/12.
 *
 * @desc MainActivity事件
 */
public class MainActivityEvent{
    public static final String HOME = "home";
    public static final String PRODUCT = "product";
    public static final String USER = "user";

    private String flag;

    public MainActivityEvent(String flag){
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }
}
