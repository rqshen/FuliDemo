package com.bcb.module.homepager.slideshow.bean;

/**
 * Created by ruiqin.shen
 * 类说明：
 */

public class ImageBanner {
    private String Title;
    private String ImageUrl;
    private String PageUrl;

    public ImageBanner(String imageUrl, String pageUrl) {
        ImageUrl = imageUrl;
        PageUrl = pageUrl;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getPageUrl() {
        return PageUrl;
    }

    public void setPageUrl(String pageUrl) {
        PageUrl = pageUrl;
    }
}
