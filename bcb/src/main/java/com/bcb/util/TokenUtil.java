package com.bcb.util;

import android.content.Context;
import android.text.TextUtils;

import com.bcb.MyApplication;

public class TokenUtil {

	public static String getEncodeToken(Context ctx){
		if (!TextUtils.isEmpty(MyApplication.saveUserInfo.getAccess_Token())) {
			return MyApplication.saveUserInfo.getAccess_Token();
		}
		return null;
	}
}
