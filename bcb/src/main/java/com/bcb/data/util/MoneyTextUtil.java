package com.bcb.data.util;

public class MoneyTextUtil {

	public static String ConversionThousandUnit(String str1) {
		str1 = new StringBuilder(str1).reverse().toString();
		String str2 = "";
		for (int i = 0; i < str1.length(); i++) {
			if (i * 3 + 3 > str1.length()) {
				str2 += str1.substring(i * 3, str1.length());
				break;
			}
			str2 += str1.substring(i * 3, i * 3 + 3) + ",";
		}
		if (str2.endsWith(",")) {
			str2 = str2.substring(0, str2.length() - 1);
		}
		return new StringBuilder(str2).reverse().toString();
	}
	
}
