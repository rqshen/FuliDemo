package com.bcb.data.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

public class ToastUtil {

	public static void alert(Context ctx, String content) {
		Toast.makeText(ctx, content, Toast.LENGTH_SHORT).show();
	}

	public static boolean checkInputParam(Context ctx, EditText input, String toast) {
		String inputStr = input.getText().toString();
		if (TextUtils.isEmpty(inputStr)) {
			ToastUtil.alert(ctx, toast);
			return false;
		}
		return true;
	}

}
