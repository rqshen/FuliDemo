package com.bcb.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class HttpUtils {

    private static final String TAG = "HttpUtils";

    // 判断网络是否连接
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 将json格式的字符串解析成http中的传递的参数
     */
    public static String jsonToStr(String jString) throws JSONException {
        JSONObject jObject = new JSONObject(jString);
        // 将json字符串转换成jsonObject
        if (jObject != null && !jObject.equals("")) {
            Iterator<String> it = jObject.keys();
            StringBuilder strBuilder = new StringBuilder();
            // 遍历JSON数据，添加到Map对象
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                Object value = jObject.get(key);
                strBuilder.append(key + "=").append(value.toString()).append("&");
            }
            if (strBuilder.toString().endsWith("&")) {
                strBuilder.deleteCharAt(strBuilder.length() - 1);
            }
            return strBuilder.toString();
        } else {
            return "";
        }
    }
}