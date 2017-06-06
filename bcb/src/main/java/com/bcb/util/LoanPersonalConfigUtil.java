package com.bcb.util;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Map;

/**
 * Created by cain on 16/4/23.
 */
public class LoanPersonalConfigUtil {
    public static final String FILE_NAME = "Loan_Personal_Info";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Context context;

    public LoanPersonalConfigUtil(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 获取暂存的个人借款信息
     * @return
     */
    public String getLoanPersonalMessage() {
        return sp.getString("LoanPersonalMessage", "");
    }

    /**
     * 暂存个人借款信息
     * @param LoanPersonalMessage Json字符串
     */
    public void saveLoanPersonalMessage(String LoanPersonalMessage) {
        editor.putString("LoanPersonalMessage", LoanPersonalMessage);
        editor.commit();
    }

    /**
     * 移除某个key值已经对应的值
     *
     */
    public void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     *
     */
    public void clear() {
        //清空SharedPreference
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 查询某个key是否已经存在
     *
     */
    public boolean contains(String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     */
    public Map<String, ?> getAll() {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getAll();
    }
}
