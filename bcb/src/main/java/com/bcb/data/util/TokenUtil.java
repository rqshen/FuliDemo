package com.bcb.data.util;

import android.content.Context;
import android.text.TextUtils;

import com.bcb.common.app.App;

public class TokenUtil {

	public static String getEncodeToken(Context ctx){
		if (!TextUtils.isEmpty(App.saveUserInfo.getAccess_Token())) {
			return App.saveUserInfo.getAccess_Token();
		}
		return null;
	}
}
