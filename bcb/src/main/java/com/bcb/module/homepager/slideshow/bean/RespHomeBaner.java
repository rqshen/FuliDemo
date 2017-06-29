package com.bcb.module.homepager.slideshow.bean;

/**
 * Created by ruiqin.shen
 * 类说明：
 */

public class RespHomeBaner {

    /**
     * Title :  有米活动
     * ImageUrl : http://192.168.1.111:7081/Uploads/imgs/2016/05/642016051719172965.png
     * PageUrl : http://192.168.20.14/activity/apoffline/three?from=singlemessage&isappinstalled=0
     */

    private String Title;
    private String Img;
    private String Url;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) {
        Img = img;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
