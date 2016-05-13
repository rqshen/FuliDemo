package com.bcb.data.util;

import com.bcb.common.app.App;
import com.bcb.data.bean.WelfareBean;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ray on 2016/5/13.
 *
 * @desc 数据库工具
 */
public class DbUtil {


    /**
     * 保存每日福利数据到数据库
     * @param value 加息值
     */
    public static void saveWelfare(String value){
        WelfareBean bean;
        List<WelfareBean> beanList = DataSupport.where("username like ?", App.saveUserInfo.getLocalPhone() + "%").find(WelfareBean.class);
        if (null != beanList && beanList.size() > 0){
            bean =  beanList.get(0);
            bean.setValue(value);
            bean.updateAll();
        }else{
            bean = new WelfareBean();
            bean.setUsername(App.saveUserInfo.getLocalPhone());
            bean.setOpenDate(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
            bean.setValue(value);
            bean.save();
        }
    }

    /**
     * 获取当天当前用户每日福利数据
     * 注意：默认是当前手机
     * @return
     */
    public static WelfareBean getWelfare(){
        List<WelfareBean> beanList = DataSupport.where("username like ? and openDate like ?", App.saveUserInfo.getLocalPhone() + "%",
                new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()) + "%").find(WelfareBean.class);
        if (null != beanList && beanList.size() > 0){
            return beanList.get(0);
        }else{
            return null;
        }
    }
}
