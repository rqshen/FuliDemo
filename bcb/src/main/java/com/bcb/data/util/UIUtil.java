package com.bcb.data.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by Ray on 2016/5/10.
 *
 * @desc UI工具
 */
public class UIUtil {

    /**
     * 初始化系统状态栏背景色
     * @param activity
     * @param resId
     */
    public static void initSystemBar(Activity activity, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源
        tintManager.setStatusBarTintResource(resId);
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    private static ProgressDialog progressDialog;

    //显示转圈提示
    public static void showProgressBar(Activity context) {
        if (!context.isFinishing()){
            if(null == progressDialog) progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
            progressDialog.setMessage("正在加载数据....");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
    }
    //隐藏转圈提示
    public static void hideProgressBar() {
        if(null != progressDialog && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
