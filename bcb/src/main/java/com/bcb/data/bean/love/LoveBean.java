package com.bcb.data.bean.love;

import java.io.Serializable;

/**
 * Created by Ray on 2016/6/7.
 *
 * @desc 聚爱项目详情
 */
public class LoveBean implements Serializable{
    private String PackageId;//项目编号
    private String Name;//标题
    private String ImagePath;//图片路径
    private String Company;//公司
    private String Desc;//说明
    private int Progress;//进度
    private int Support;//支持次数
    private float Money;//已筹资金额

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

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public int getProgress() {
        return Progress;
    }

    public void setProgress(int progress) {
        Progress = progress;
    }

    public int getSupport() {
        return Support;
    }

    public void setSupport(int support) {
        Support = support;
    }

    public float getMoney() {
        return Money;
    }

    public void setMoney(float money) {
        Money = money;
    }
}
