package com.bcb.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/11/4 10:29
 */
public class DownloadUtils {

	public static final String FILE_PATH = "/fljr/app";//相对路径

	/**
	 * 下载文件
	 */
	public static void downLoadFile(Context mContext, String url, String fileName) {
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))//路径
				.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)//默认，下载过程中显示，下载完成后自动消失
				.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)//
				.setDestinationInExternalPublicDir(FILE_PATH, fileName);//要使用相对路径
		((DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
	}
}
