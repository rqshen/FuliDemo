package com.bcb.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/12/13 10:35
 */
public class IpUtils {
	public static String getIpAddressString() {
		try {
			for (Enumeration<NetworkInterface> enNetI = NetworkInterface.getNetworkInterfaces() ; enNetI.hasMoreElements() ; ) {
				NetworkInterface netI = enNetI.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = netI.getInetAddresses() ; enumIpAddr.hasMoreElements() ; ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return "";
	}
}
