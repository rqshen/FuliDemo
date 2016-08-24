package com.bcb.data.bean.love;

import java.io.Serializable;

/**
 * Created by Ray on 2016/6/7.
 *
 * @desc 聚爱项目详情
 */
public class LoveBean implements Serializable{
    private String AggregateId;//项目编号
    private String Title;//标题
    private String ThumbnailImg;//图片路径
    private String CompanyName;//公司
    private String Description;//说明
    private String APPJumplink;//跳转链接
    private int Supports;//支持次数
    private int Status;//筹款中（1）筹款完成（2）
    private float Amounts;//已筹资金额
    private String CreateDate;

    public String getAggregateId() {
        return AggregateId;
    }

    public void setAggregateId(String aggregateId) {
        AggregateId = aggregateId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getThumbnailImg() {
        return ThumbnailImg;
    }

    public void setThumbnailImg(String thumbnailImg) {
        ThumbnailImg = thumbnailImg;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getJumplink() {
        return APPJumplink;
    }

    public void setJumplink(String jumplink) {
        APPJumplink = jumplink;
    }

    public int getSupports() {
        return Supports;
    }

    public void setSupports(int supports) {
        Supports = supports;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public float getAmounts() {
        return Amounts;
    }

    public void setAmounts(float amounts) {
        Amounts = amounts;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }
}
