package com.bcb.data.util;

import java.text.DecimalFormat;

public class MyTextUtil {

	public static String delBankNum(String data){
		if (null == data)return "";
		if (data.length() < 5)return data;
		return data.substring(0, 4)+"***********"+data.substring(data.length() -4, data.length());
	}
	
	public static String delFloat(float data){
		DecimalFormat fnum = new DecimalFormat("##0.00");
		return fnum.format(data);
	}

}
