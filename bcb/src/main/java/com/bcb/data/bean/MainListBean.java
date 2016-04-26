package com.bcb.data.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cain on 16/2/17.
 */
public class MainListBean implements Serializable {
    public List<ExpiredRecordsBean> Tyb;  //体验标
    public List<ProductRecordsBean> Xszx;   //新手标
    public List<AnnounceRecordsBean> Xbyg; //新标预告
    public List<ProductRecordsBean> Jpxm; //精品项目
}
