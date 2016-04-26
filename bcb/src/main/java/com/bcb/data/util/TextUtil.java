package com.bcb.data.util;

import java.text.DecimalFormat;

public class TextUtil {

	public static String getDate(String datetime){
		if(null == datetime)return "";
		datetime = datetime.trim().replace(" ", "&");
		return 	datetime.substring(0, datetime.indexOf("&"));
	}
	
	public static String delBankNum(String data){
		if(null == data)return "";
		String newdata = data.substring(0, 4)+"***********"+data.substring(data.length() -4, data.length());
		return newdata;
	}
	
	public static String delFloat(float data){
		DecimalFormat   fnum  =   new  DecimalFormat("##0.00");    
		String   dd = fnum.format(data);
		return dd;
	}
	
}
