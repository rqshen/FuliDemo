package com.bcb.data.bean.transaction;

import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/11/4 09:59
 */
public class VersionBean {

	/**
	 * version : 2.0.3
	 * force : true
	 * Url : http
	 * confirmbtntext : 确定
	 * cancelbtntext : 好的
	 * tips : ["债权转让功能","优化用户界面","夏天夏天悄悄过去","留下小咪咪"]
	 */

	public String Version;
	public int Increment;
	public boolean Force;
	public String Url;
	public String Confirmbtntext;
	public String Cancelbtntext;
	public List<String> Tips;
}