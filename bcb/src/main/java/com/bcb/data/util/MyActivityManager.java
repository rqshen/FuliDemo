package com.bcb.data.util;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by cain on 16/3/3.
 * 主要是用来手动管理Activity的栈，用于登陆界面点击返回的时候，销毁栈中的所有非首页的Activity
 * 其中SettingGesturePatternLock在点击忘记密码的时候还需要使用，另外手动销毁
 */
public class MyActivityManager {
    private static MyActivityManager instance;
    private Stack<Activity> activityStack;  //用于管理activityStack的栈
    private MyActivityManager() {
    }
    //单例模式
    public static MyActivityManager getInstance() {
        if (instance == null) {
            instance = new MyActivityManager();
        }
        return instance;
    }
    //入栈
    public void pushOneActivity(Activity actvity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(actvity);
    }

    //获取栈顶的activity，先进后出原则
    public Activity getLastActivity() {
        return activityStack.lastElement();
    }
    //移除一个activity
    public void popOneActivity(Activity activity) {
        if (activityStack != null && activityStack.size() > 0) {
            if (activity != null) {
                activity.finish();
                activityStack.remove(activity);
                activity = null;
            }
        }
    }
    //退出所有activity
    public void finishAllActivity() {
        if (activityStack != null) {
            while (activityStack.size() > 0) {
                Activity activity = getLastActivity();
                if (activity == null) break;
                popOneActivity(activity);
            }
        }
    }
}
