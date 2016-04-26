package com.bcb.data.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.text.TextUtils;

public class RegexManager {
	
	private static final String TAG = "RegexManager";

	// 使用正则表达式判断是否为二代身份证号码
	// 根据ISO7064:1983.MOD11-2校验公式，求 A[i] x W[i]的和，除以11得到的余数Mode[]数组的下标
	public static boolean isSecondGenerationIDCardNum(String Str) {
		if (Str.length() != 18 || Str.length() > 18)
			return false;
		// 加权因子
		int Prior[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		// 第18位的值，根据加权余数值判断
		char Mode[] = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
		int sum = 0;
		char[] idCardStr = Str.toCharArray();
		for (int i = 0; i < 17; i++) {
			int cardNum = idCardStr[i];
			if ((cardNum < 48) || (cardNum > 57))
				return false;
			sum += (cardNum - 48) * Prior[i];
		}
		if (idCardStr[17] == Mode[sum % 11])
			return true;
		return false;
	}

	// 判断是否为手机号码
	public static boolean isPhoneNum(String mobiles) {
		String telRegex = "[1][3578]\\d{9}";
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
            return mobiles.matches(telRegex);
	}

	// 判断密码是否规范的正则表达式，以数字、字母和下划线的组成的8到15位字符串
	public static boolean isPasswordNum(String pwd) {
		if (pwd.length() < 8 || pwd.length() > 15)
			return false;
		return isRightCode(pwd);
	}

	// 不允许输入汉字
	public static String stringFilter(String str) throws PatternSyntaxException {
		String regEx = "[\u4E00-\u9FA5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	// 判断是否是数字
	public static boolean isNum(String str) {
		return str.matches("^[0-9]*$");
	}

	// 判断是否是字母
	public static boolean isAZ(String str) {
		return str.matches("^[a-zA-Z]*$");
	}

	// 判断是否为数字、字母、字符等合法字符
	public static boolean isRightCode(String Str) {
		char[] StrArray = Str.toCharArray();
		for (int i = 0; i < Str.length(); i++) {
			if (StrArray[i] < 33 || StrArray[i] > 126)
				return false;
		}
		return true;
	}
	
	// 判断是否是除了数字、字母外的特殊合法字符
	public static boolean isSpecialRightCode(String str){
		
		char[] StrArray = str.toCharArray();
		for(int i = 0; i < str.length(); i++){
			//如果是特殊合法字符，继续下一次检查，否则返回false
			if((StrArray[i] >= 33 && StrArray[i] <= 47)
			   || (StrArray[i] >= 58 && StrArray[i] <= 64) 
			   || (StrArray[i] >= 91 && StrArray[i] <= 96)
			   || (StrArray[i] >= 123 && StrArray[i] <= 126))
				continue;
			else
				return false;
		}
		return true;
	}
	
	//判断是否为数字、字母和特殊合法字符的组合
	public static boolean isHybridRightCode(String str){
		char[] StrArray = str.toCharArray();
		
		boolean numStatus = false;
		boolean azStatus = false;
		boolean specialStatus = false;
		
		for (int i = 0; i < str.length(); i++){
			//包含数字，数字状态位为true
			if(StrArray[i] >= 48 && StrArray[i] <= 57)
				numStatus = true;
			
			//包含字母，字符状态为为true
			if((StrArray[i] >= 65 && StrArray[i] <= 90) || (StrArray[i] >= 97 && StrArray[i] <= 122))
				azStatus = true;
			
			//包含有特殊字符，特殊字符状态为true
			if((StrArray[i] >= 33 && StrArray[i] <= 47)
					   || (StrArray[i] >= 58 && StrArray[i] <= 64) 
					   || (StrArray[i] >= 91 && StrArray[i] <= 96)
					   || (StrArray[i] >= 123 && StrArray[i] <= 126))
				specialStatus = true;
		}
		
		//将三个状态位相与得到是否为三种类型的组合体
		return numStatus && azStatus && specialStatus;
	}
	
	// 判断是否为6位数字组成
	public static boolean isResizngCode(String str) {
		return str.matches("^[0-9]{6}$");
	}

	//判断email格式是否正确
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}
}
