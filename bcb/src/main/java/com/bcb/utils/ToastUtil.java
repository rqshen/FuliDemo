package com.bcb.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

public class ToastUtil {

    private static Toast mToast;

    public static void alert(Context ctx, String content) {
        if (mToast == null) {
            mToast = Toast.makeText(ctx, content, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(content);
        }
        mToast.show();
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
