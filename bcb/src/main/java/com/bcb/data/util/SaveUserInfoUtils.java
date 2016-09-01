package com.bcb.data.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Map;

public class SaveUserInfoUtils {
	
	public static final String FILE_NAME = "User_Info";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private Context context;

	public SaveUserInfoUtils(Context context) {
		this.context = context;
		sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public String getAccess_Token() {
		if (!TextUtils.isEmpty(sp.getString("Access_Token", ""))) {
			return sp.getString("Access_Token", "");
		} else {
			return null;
		}
	}

	public void setAccess_Token(String access_Token) {
		//使用sharedPreference存储
		editor.putString("Access_Token", access_Token);
		editor.commit();
	}

	// 登录注销配置相关信息
	public boolean isFirstLogin() {
		return sp.getBoolean("isFirstLogin", false);
	}

	public void setFirstLogin(boolean firstLogin) {
		editor.putBoolean("isFirstLogin", firstLogin);
		editor.commit();
	}

	//注册的手机号
	public String getLocalPhone() {
		return sp.getString("localPhone", "");
	}

	public void setLocalPhone(String localPhone) {
		//使用SharedPreference存储
		editor.putString("localPhone", localPhone);
		editor.commit();
	}

	//是否已经预约新标预告
	public void setPreviewInvest(String packageId) {
		editor.putBoolean(packageId, true);
		editor.commit();
	}

	public boolean isPreviewInvest(String packageId) {
		return sp.getBoolean(packageId, false);
	}

	//获取手势密码
	public String getGesturePassword() {
		return sp.getString("GesturePassword", "");
	}

	//储存手势密码
	public void setGesturePassword(String gesturePassword) {
		editor.putString("GesturePassword", gesturePassword);
		editor.commit();
	}

	// 浏览公司相关信息配置
	public String getCurrentCompanyId() {
		return sp.getString("currentCompanyId", "");
	}

	public void setCurrentCompanyId(String currentCompanyId) {
		editor.putString("currentCompanyId", currentCompanyId);
		editor.commit();
	}
	
	public String getCurrentCompanyName() {
		return sp.getString("currentCompanyName", "福利金融");
	}

	public void setCurrentCompanyName(String currentCompanyName) {
		editor.putString("currentCompanyName", currentCompanyName);
		editor.commit();
	}
	
	public String getJXPackageAdWord() {
		return sp.getString("JXPackageAdWord", "");
	}

	public void setJXPackageAdWord(String JXPackageAdWord) {
		editor.putString("JXPackageAdWord", JXPackageAdWord);
		editor.commit();
	}
	
	public String getXFBPackageAdWord() {
		return sp.getString("XFBPackageAdWord", "");
	}

	public void setXFBPackageAdWord(String XFBPackageAdWord) {
		editor.putString("XFBPackageAdWord", XFBPackageAdWord);
		editor.commit();
	}
	
	public String getInvestButtonAdWord() {
		return sp.getString("InvestButtonAdWord", "");
	}

	public void setInvestButtonAdWord(String InvestButtonAdWord) {
		editor.putString("InvestButtonAdWord", InvestButtonAdWord);
		editor.commit();
	}
	
	public String getUpgradeWord() {
		return sp.getString("UpgradeWord", "");
	}

	public void setUpgradeWord(String UpgradeWord) {
		editor.putString("UpgradeWord", UpgradeWord);
		editor.commit();
	}

	// 常用的四个驿站
	//全部公司
	public String getAllCompany() {
		return sp.getString("AllCompany", "");
	}

	public void setAllCompany(String One) {
		editor.putString("AllCompany", One);
		editor.commit();
	}

	//第一个位置
	public String getFirst() {
		return sp.getString("First", "");
	}

	public void setFirst(String First) {
		editor.putString("First", First);
		editor.commit();
	}

	//第二个位置
	public String getSecond() {
		return sp.getString("Second", "");
	}

	public void setSecond(String Second) {
		editor.putString("Second", Second);
		editor.commit();
	}

	//第三个位置
	public String getThird() {
		return sp.getString("Third", "");
	}

	public void setThird(String Third) {
		editor.putString("Third", Third);
		editor.commit();
	}
	
	/**
	 * 移除某个key值已经对应的值
	 */
	public void remove(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}

	/**
	 * 清除所有数据
	 */
	public void clear() {

		//清空SharedPreference
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		String gesturePassword = sp.getString("GesturePassword", "");
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.putString("GesturePassword", gesturePassword);
		editor.commit();
		LogUtil.i("bqt", "【清除所有数据，除了手势密码】" + sp.getString("GesturePassword", ""));
	}

	/**
	 * 查询某个key是否已经存在
	 */
	public boolean contains(String key) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		return sp.contains(key);
	}

	/**
	 * 返回所有的键值对
	 */
	public Map<String, ?> getAll() {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		return sp.getAll();
	}

}
