package com.bcb.util;

import android.content.Context;
import android.content.Intent;

import com.bcb.MyApplication;
import com.bcb.module.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class PackageUtil {

    /**
     * 获取请求成功与否的结果
     *
     * @param data
     * @param ctx
     * @return
     */
    public static boolean getRequestStatus(String data, Context ctx) {
        try {
            if (null == data) {
                ToastUtil.alert(ctx, "服务器返回数据为空");
                return false;
            }
            JSONObject json = new JSONObject(data);
            int status = json.getInt("status");
            if (status != 1) {
                if (status == -5) {
                    // token过期或者用户已经被踢出，要删除本地数据库，并跳转到登录界面
                    onUserKickOut(ctx);
                }
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取请求成功与否的结果
     *
     * @param data 回调的JSONObject对象
     * @param ctx  上下文
     * @return 成功或失败
     */
    public static boolean getRequestStatus(JSONObject data, Context ctx) {
        try {
            if (null == data) {
                ToastUtil.alert(ctx, "服务器返回数据为空");
                return false;
            }
            if (data.getInt("status") != 1) {
                if (data.getInt("status") == -5) {
                    onUserKickOut(ctx);
                }
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getBasicStatus(String data) {
        if (null == data) return 0;
        try {
            JSONObject json = new JSONObject(data);
            int status = json.getInt("status");
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public static void onUserKickOut(Context ctx) {
        MyApplication.saveUserInfo.removeGesturePassword();
        MyApplication.saveUserInfo.clear();
        Intent intent = new Intent();
        intent.setClass(ctx, LoginActivity.class);
        ctx.startActivity(intent);
    }

    public static JSONObject getResultObject(String data) {
        try {
            if (null == data) {
                return null;
            }
            JSONObject json = new JSONObject((String) data);
            JSONObject result = json.getJSONObject("result");
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONObject getResultObject(JSONObject data) {
        try {
            if (null == data) {
                return null;
            }
            return data.getJSONObject("result");
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONObject pageParameter(int PackageTypeId, int PageNow, int PageSize) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("PackageTypeId", PackageTypeId);
            obj.put("PageNow", PageNow);
            obj.put("PageSize", PageSize);
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject pkgMyPageList(int PageNow, int PageSize) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("PageNow", PageNow);
            obj.put("PageSize", PageSize);
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
