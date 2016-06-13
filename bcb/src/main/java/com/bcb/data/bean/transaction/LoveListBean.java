package com.bcb.data.bean.transaction;

import com.bcb.data.bean.love.LoveBean;

import java.util.List;

/**
 * Created by Ray on 2016/6/13.
 *
 * @desc
 */
public class LoveListBean {
    public int PageNow;
    public int PageSize;
    public int TotalCount;
    public List<LoveBean> Records;
}
