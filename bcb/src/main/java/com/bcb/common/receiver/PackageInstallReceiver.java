package com.bcb.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.bcb.common.app.App;
import com.bcb.data.util.LoanPersonalConfigUtil;
import com.bcb.data.util.LogUtil;

public class PackageInstallReceiver extends BroadcastReceiver {

	private static final String TAG = "PackageInstallReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getDataString().substring(8).equals("com.bcb")) {
			
			if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
				String packageName = intent.getDataString().substring(8);
				LogUtil.d(TAG, "安装:" + packageName + "包名的程序");
			}
			// 接收卸载广播
			if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
				String packageName = intent.getDataString().substring(8);
				LogUtil.d(TAG, "卸载:" + packageName + "包名的程序");
                Intent newIntent = new Intent();
				newIntent.setClassName(packageName, "com.bcb.presentation.view.activity.Activity_Start_Up");
				newIntent.setAction("android.intent.action.MAIN");
				newIntent.addCategory("android.intent.category.LAUNCHER");
				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(newIntent);
			}
		}
	}

}