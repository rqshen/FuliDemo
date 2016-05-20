package com.bcb.data.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import cn.jpush.android.api.JPushInterface;

public class SystemUtil {

	private static final String TAG = "SystemUtil";
	
	public static String getLocalHostIp() {
				
		String ipaddress = "";
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			// 遍历所用的网络接口
			while (en.hasMoreElements()) {
				NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
				Enumeration<InetAddress> inet = nif.getInetAddresses();
				// 遍历每一个接口绑定的所有ip
				while (inet.hasMoreElements()) {
					InetAddress ip = inet.nextElement();
					if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
						return ipaddress = ip.getHostAddress();
					}
				}

			}
		} catch (SocketException e) {
			LogUtil.d(TAG, "获取本地ip地址失败");
			e.printStackTrace();
		}
		return ipaddress;
	}

	/**
	 * 获取设备imei
	 * @param context
	 * @param imei
     * @return
     */
	public static String getImei(Context context, String imei) {
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId();
		} catch (Exception e) {
			LogUtil.e(SystemUtil.class.getSimpleName(), e.getMessage());
		}
		return imei;
	}

	/**
	 * 获取极光推送的设备id
	 * @param context
	 * @return
     */
	public static String getDeviceId(Context context) {
		String deviceId = JPushInterface.getUdid(context);
		return deviceId;
	}



}
