package com.bcb.data.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/11/28 14:44
 */
public class ProgressBarUtils {
	private  static ProgressDialog progressDialog;

	/**
	 * 转圈提示
	 */
	public static void show(Context context) {
		if (progressDialog != null) progressDialog.hide();
		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
		progressDialog.setMessage("正在验证借款信息...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

	/**
	 * 隐藏转圈
	 */
	public static void hide() {
		if (null != progressDialog && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}