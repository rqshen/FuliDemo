package com.bcb.presentation.view.activity_interface;

import android.app.Activity;

import com.bcb.utils.LogUtil;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/7/29 16:18
 */
public class WebAppinterface {
    private Activity mActivity;

    public WebAppinterface(Activity context) {
        this.mActivity = context;
    }

    public void open_result(String state, String message) {
        LogUtil.i("bqt", "【WebAppinterface】【open_result】后台回调" + state + "-" + message);
        if (state.equals("000")) {

            //mActivity.startActivity(new Intent(mActivity, Activity.class));
        }
    }
}
