package com.bcb.common.event;

/**
 * Created by Ray on 2016/5/12.
 *
 * @desc 产品列表事件
 */
public class ProductFragEvent {
    public static final String REFRESH = "refresh";

    private String flag;

    public ProductFragEvent(String flag){
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }
}
