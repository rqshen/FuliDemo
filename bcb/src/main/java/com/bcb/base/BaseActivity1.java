package com.bcb.base;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.MyApplication;
import com.bcb.module.myinfo.setting.gesturelock.GestureLockActivity;
import com.bcb.presentation.view.custom.AlertView.AlertView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by cain on 16/3/2.
 */
public class BaseActivity1 extends FragmentActivity {

    private static final String FILE_NAME = "App_Enter_Background_Time";
    private static final String ENTER_BACKGROUND_KEY = "AppOnBackGround";

    private AlertView alertView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_base);
        //创建的时候判断是否要设置手势密码
        if (isLongerThanOneMinute() && MyApplication.saveUserInfo.getAccess_Token() != null && MyApplication.saveUserInfo.isFirstLogin()) {
            showSettingGestureAlertView();
        }
    }

    @SuppressLint("NewApi")
    public void setBaseContentView(int layoutResId) {
        LinearLayout llContent = (LinearLayout) findViewById(R.id.content);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(layoutResId, null);
        llContent.addView(v);
    }

    @SuppressLint("NewApi")
    public void setLeftTitleVisible(boolean visible) {
        if (visible) {
            ((ImageView) findViewById(R.id.back_img)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.back_img)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            ((ImageView) findViewById(R.id.back_img)).setVisibility(View.GONE);
        }
    }

    @SuppressLint("NewApi")
    public void setLeftTitleVisible(View.OnClickListener onClickListener) {
        ((ImageView) findViewById(R.id.back_img)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.back_img)).setOnClickListener(onClickListener);
    }

    @SuppressLint("NewApi")
    public void setLeftTitleValue(String leftTitle, boolean dropdownVisible, View.OnClickListener onClickListener) {
        if (leftTitle.isEmpty()) {
            setLeftTitleVisible(true);
            ((ImageView) findViewById(R.id.dropdown)).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.left_text)).setText(leftTitle);
            ((TextView) findViewById(R.id.left_text)).setOnClickListener(onClickListener);
            if (dropdownVisible) {
                ((ImageView) findViewById(R.id.dropdown)).setVisibility(View.VISIBLE);
            } else {
                ((ImageView) findViewById(R.id.dropdown)).setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("NewApi")
    public void setTitleValue(String title) {
        ((TextView) findViewById(R.id.title_text)).setText(title);
    }

    @SuppressLint("NewApi")
    public void setRightTitleValue(String rightTitle, View.OnClickListener onClickListener) {
        ((TextView) findViewById(R.id.right_text)).setText(rightTitle);
        ((TextView) findViewById(R.id.right_text)).setOnClickListener(onClickListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //判断是否进入后台
        if (!isAppOnForeground()) {
            //清空上一次进入后台的时间
            clearEnterTime();
            //获取当前时间
            SimpleDateFormat formatter = new SimpleDateFormat ("yyyy/MM/dd hh:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            //存储退到后台的时间
            setAppOnBackgroundTime(formatter.format(curDate));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断APP返回前台，存在手势密码，并且退到后台时间超过一分钟，则提示
        if (isAppOnForeground() && isLongerThanOneMinute()) {
            //存在手势密码
            if (hasGesturePassword()) {
                GestureLockActivity.launche(BaseActivity1.this, false, true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    private boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否存在手势密码
     */
    private boolean hasGesturePassword() {
        return !MyApplication.saveUserInfo.getGesturePassword().isEmpty();
    }

    /**
     * 判断程序退到后台是否超过一分钟
     */
    private boolean isLongerThanOneMinute() {
        //判断时间是否已经过了一分钟
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy/MM/dd hh:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        Date appEnterBackgroundTime = null;
        try {
            if (!getAppOnBackgroundTIme().isEmpty()) {
                //需要将当前时间转成相应的格式，否则很可能会出现因格式不对等导致的时间计算不正确的情况
                String  currentString = formatter.format(curDate);
                curDate = formatter.parse(currentString);
                appEnterBackgroundTime = formatter.parse(getAppOnBackgroundTIme());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (appEnterBackgroundTime != null && (curDate.getTime()/1000 - appEnterBackgroundTime.getTime()/1000) > 60) {
            return true;
        }
        return false;
    }

    /**
     * 记录进入后台时间
     * @param time
     */
    public void setAppOnBackgroundTime(String time) {
        SharedPreferences sp = this.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //缓存进入后台的时间
        editor.putString("AppOnBackGround", time);
        editor.commit();
    }

    /**
     * 获取上一次进入后台的时间
     * @return 时间
     */
    public String getAppOnBackgroundTIme() {
        SharedPreferences sp = this.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        return  sp.getString(ENTER_BACKGROUND_KEY, "");
    }

    /**
     * 清除所有数据
     *
     */
    public void clearEnterTime() {
        //清空SharedPreference
        SharedPreferences sp = this.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    //提示手势密码
    private void showSettingGestureAlertView() {
        //提示设置手势密码
        if(MyApplication.saveUserInfo.getAccess_Token() != null && MyApplication.saveUserInfo.isFirstLogin()&&!hasGesturePassword()) {
            MyApplication.saveUserInfo.setFirstLogin(false);
            AlertView.Builder ibuilder = new AlertView.Builder(BaseActivity1.this);
            ibuilder.setTitle("是否设置手势密码?");
            ibuilder.setMessage("设置手势密码可以提高账户安全");
            ibuilder.setPositiveButton("立即设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    GestureLockActivity.launche(BaseActivity1.this, true, true);
                    startActivity(new Intent(BaseActivity1.this, GestureLockActivity.class));
                    alertView.dismiss();
                    alertView = null;
                }
            });
            ibuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertView.dismiss();
                    alertView = null;
                }
            });
            alertView = ibuilder.create();
            alertView.show();
        }
    }
}