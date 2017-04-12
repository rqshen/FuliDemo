package com.bcb.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 对activity进行管理
 * Created by ruiqin.shen.
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<Activity>();

    /**
     * 添加Activity
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * 关闭所有的添加的Activity
     */
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }


}
