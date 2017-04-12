package com.bcb.utils;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveConfigUtil {
    public static final String FILE_NAME = "System_Config";  
    private SharedPreferences sp;  
    private SharedPreferences.Editor editor;  
    private Context context;  
    
    public SaveConfigUtil(Context context) {  
    	this.context = context;
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);  
        editor = sp.edit();  
    }

    // 软件配置相关信息
	public boolean isNotFirstRun() {
		 return sp.getBoolean("isNotFirstRun", false);
	}
	public void setNotFirstRun(boolean firstRun) {
		 editor.putBoolean("isNotFirstRun", firstRun);
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