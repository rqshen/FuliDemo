package com.bcb.utils;

import android.content.Context;
import android.content.DialogInterface;

import com.bcb.presentation.view.custom.AlertView.LoadingDialog;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/8/9 10:15
 */
public class LoadingUtils {
    public static LoadingDialog progressDialog;

    /**
     * 显示简单的带进度条对话框
     *
     * @param context    句柄
     * @param title      标题
     * @param message    内容
     * @param cancelable 是否可以取消
     */
    public static void showProgressDialog(Context context, String title, String message, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        dismissProgressDialog();
        progressDialog = new LoadingDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(cancelable);
        progressDialog.setOnCancelListener(cancelListener);
        progressDialog.show();
    }

    /**
     * 取消带进度条的对话框
     */
    public static void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (IllegalArgumentException e) {
            }
        }
        progressDialog = null;
    }
}
