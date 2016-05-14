package com.bcb.common.event;

/**
 * Created by Ray on 2016/5/14.
 *
 * @desc 广播事件
 */
public class BroadcastEvent {

    /*****************事件标志类型*******************/
    //MainActivity中fragment切换事件
    public static final String HOME = "home";
    public static final String PRODUCT = "product";
    public static final String USER = "user";

    /**
     * 产品列表刷新事件
     */
    public static final String REFRESH = "refresh";

    /**
     * 退出登录
     */
    public static final String LOGOUT = "logout";

    /**
     * 登录
     */
    public static final String LOGIN = "login";


    /**
     * 事件标志
     */
    private String flag;

    public BroadcastEvent(String flag) {
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }
}
