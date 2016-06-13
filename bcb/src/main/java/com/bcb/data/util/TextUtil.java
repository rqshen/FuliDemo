package com.bcb.data.util;

import java.text.DecimalFormat;

public class TextUtil {

	public static String getDate(String datetime){
		if(null == datetime)return "";
		datetime = datetime.trim().replace(" ", "&");
		return datetime.substring(0, datetime.indexOf("&"));
	}
	
	public static String delBankNum(String data){
		if(null == data)return "";
		String newdata = data.substring(0, 4)+"***********"+data.substring(data.length() -4, data.length());
		return newdata;
	}
	
	public static String delFloat(float data){
		DecimalFormat fnum = new DecimalFormat("##0.00");
		String dd = fnum.format(data);
		return dd;
	}

//	/**
//	 * 小数点后两位
//	 * @param money
//	 * @return
//	 */
//	public static String setMoneyAndTwo(long money) {
//		DecimalFormat df = new DecimalFormat("#,###,###,###,##0.00");
//		df.setRoundingMode(RoundingMode.HALF_UP);
//		String mos;
//		if (money >= 100000000) {
//			mos = df.format(money * 0.00000001) + "亿";
//		} else if (money >= 10000) {
//			mos = df.format(money * 0.0001) + "万";
//		} else {
//			mos = df.format(money) + "元";
//		}
//		return mos;
//	}

}
