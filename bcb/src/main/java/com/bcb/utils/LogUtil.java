package com.bcb.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashSet;

public final class LogUtil {

    private static boolean isDebug = true;// 为true时Locat会打印出相关日志，为false时不打印

    // Locat日志的颜色
    // v 黑色，verbose啰嗦，任何信息都会输出
    // d 蓝色，debug调试，仅输出d的信息
    // i 绿色，information消息，会输出i，w，e的信息
    // w 橙色，warning警告，会输出w，e的信息
    // e 红色，error错误，仅输出e的信息

    public static void printAllFields(Object obj) {
        if (isDebug) {
            if (obj == null) {
                android.util.Log.e("null", "obj =" + obj);
                return;
            }
            Class claz = obj.getClass();
            Field[] fileds = claz.getDeclaredFields();
            for (Field f : fileds) {
                if (!f.isAccessible())
                    f.setAccessible(true);
                try {
                    android.util.Log.i(claz.getSimpleName(), f.getName() + " = " + f.get(obj));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static HashSet<Object> set = new HashSet<Object>();

    public static boolean shutupLog(Object obj) {
        if (!set.contains(obj)) {
            return set.add(obj);
        }

        return false;
    }

    public static boolean openLog(Object obj) {
        return set.remove(obj);
    }

    // ==========================================================================

    public static void v(String tag, String msg) {
        if (isDebug) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void v(Object obj, String msg) {
        if (isDebug && !set.contains(obj)) {
            android.util.Log.v(obj.getClass().getSimpleName(), msg);
        }
    }

    public static void v(Object obj, String suffix, String prefix, Object... msgs) {
        if (isDebug && !set.contains(obj)) {
            StringBuilder sb = new StringBuilder();
            for (Object msg : msgs) {
                sb.append(prefix + msg + suffix + "\n");
            }
            android.util.Log.v(obj.getClass().getSimpleName(), sb.toString());
        }
    }

    public static void v(String tag, String msg, Throwable t) {
        if (isDebug) {
            android.util.Log.v(tag, msg, t);
        }
    }

    // ==========================================================================

    public static void d(String tag, String msg) {
        if (isDebug) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void d(Object obj, String msg) {
        if (isDebug && !set.contains(obj)) {
            Log.d(obj.getClass().getSimpleName(), msg);
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (isDebug) {
            android.util.Log.d(tag, msg, t);
        }
    }

    public static void d(Object obj, String prefix, String suffix, Object... msgs) {
        if (isDebug && !set.contains(obj)) {
            StringBuilder sb = new StringBuilder();
            for (Object msg : msgs) {
                sb.append(prefix + msg + suffix + "\n");
            }
            android.util.Log.d(obj.getClass().getSimpleName(), sb.toString());
        }
    }

    // ==========================================================================

    public static void i(Object obj, String msg) {
        if (isDebug && !set.contains(obj)) {
            android.util.Log.i(obj.getClass().getSimpleName(), msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void i(Object obj, String prefix, String suffix, Object... msgs) {
        if (isDebug && !set.contains(obj)) {
            StringBuilder sb = new StringBuilder();
            for (Object msg : msgs) {
                sb.append(prefix + msg + suffix + "\n");
            }
            android.util.Log.i(obj.getClass().getSimpleName(), sb.toString());
        }
    }

    public static void i(String tag, String msg, Throwable t) {
        if (isDebug) {
            android.util.Log.i(tag, msg, t);
        }
    }

    // ==========================================================================

    public static void w(String tag, String msg) {
        if (isDebug) {
            android.util.Log.w(tag, msg);
        }
    }

    public static void w(Object obj, String msg) {
        if (isDebug && !set.contains(obj)) {
            android.util.Log.w(obj.getClass().getSimpleName(), msg);
        }
    }

    public static void w(String tag, String msg, Throwable t) {
        if (isDebug) {
            android.util.Log.w(tag, msg, t);
        }
    }

    public static void w(Object obj, String prefix, String suffix, Object... msgs) {
        if (isDebug && !set.contains(obj)) {
            StringBuilder sb = new StringBuilder();
            for (Object msg : msgs) {
                sb.append(prefix + msg + suffix + "\n");
            }
            android.util.Log.w(obj.getClass().getSimpleName(), sb.toString());
        }
    }

    // ==========================================================================

    public static void e(String tag, String msg) {
        if (isDebug) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void e(Object obj, String msg) {
        if (isDebug && !set.contains(obj)) {
            android.util.Log.e(obj.getClass().getSimpleName(), msg);
        }

    }

    public static void e(String tag, String msg, Throwable t) {
        if (isDebug) {
            android.util.Log.e(tag, msg, t);
        }
    }

    public static void e(Object obj, String prefix, String suffix, Object... msgs) {
        if (isDebug && !set.contains(obj)) {
            StringBuilder sb = new StringBuilder();
            for (Object msg : msgs) {
                sb.append(prefix + msg + suffix + "\n");
            }
            android.util.Log.e(obj.getClass().getSimpleName(), sb.toString());
        }
    }

    // ==========================================================================

    public static boolean isDebug() {
        return isDebug;
    }

    public static void setDebug(boolean isDebug) {
        LogUtil.isDebug = isDebug;
    }

}
