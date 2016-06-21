package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.HotStationListBean;
import com.bcb.data.bean.HotStationRecordsBean;
import com.bcb.data.util.AutoWrapLinearLayout;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Activity_Station_Change extends Activity_Base {

	private static final String TAG = "Activity_Station_Change";

	private AutoWrapLinearLayout tagContainer;
	private LinearLayout hotstation_layout;
	
	private TextView tagView;
	private ArrayList<String> list;
	
	private View hotView;
	
	private int PageNow = 1; 
	private int PageSize = 10;
	
	private HotStationListBean mHotStationList;
	private LinearLayout hot_station_list;
	
	private String stationName;

	//转圈提示
	private ProgressDialog progressDialog;

    private BcbRequestQueue requestQueue;

	public static void launche(Context ctx, String stationName) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Station_Change.class);
		intent.putExtra("stationName", stationName);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Station_Change.this);
		stationName = getIntent().getStringExtra("stationName");
		setBaseContentView(R.layout.activity_station_change);
		setLeftTitleVisible(true);
		setTitleValue("当前公司：" + stationName);
		requestQueue = App.getInstance().getRequestQueue();
        init();
	}

	private void init() {
		showProgressBar("正在获取公司信息....");

		tagContainer = (AutoWrapLinearLayout) findViewById(R.id.tag_container);
		hotstation_layout = (LinearLayout) findViewById(R.id.hotstation_layout);

        //获取四个常用驿站，如果没有的话，一开始就要设置为全部公司
        App.saveUserInfo.setAllCompany("" + "##" + "全部公司");
		list = new ArrayList<String>();
        //全部公司
        if (!TextUtils.isEmpty(App.saveUserInfo.getAllCompany())) {
			list.add(App.saveUserInfo.getAllCompany().split("##")[1]);
		}
        //第一个位置
		if (!TextUtils.isEmpty(App.saveUserInfo.getFirst())) {
			list.add(App.saveUserInfo.getFirst().split("##")[1]);
		}
        //第二个位置
		if (!TextUtils.isEmpty(App.saveUserInfo.getSecond())) {
			list.add(App.saveUserInfo.getSecond().split("##")[1]);
		}
        //第三个位置
		if (!TextUtils.isEmpty(App.saveUserInfo.getThird())) {
			list.add(App.saveUserInfo.getThird().split("##")[1]);
		}

		for (int i = 0; i < list.size(); i++) {
		    String tag=list.get(i);
		    tagView = (TextView) getLayoutInflater().inflate(R.layout.item_station_often, tagContainer, false);
		    tagView.setText(tag);
		    if (i == 0) {
		    	 tagView.setTag(App.saveUserInfo.getAllCompany());
			} else if (i == 1) {
		    	 tagView.setTag(App.saveUserInfo.getFirst());
			} else if (i == 2) {
		    	 tagView.setTag(App.saveUserInfo.getSecond());
			} else if (i == 3) {
		    	 tagView.setTag(App.saveUserInfo.getThird());
			}
		   
		    tagView.setOnClickListener(new OnClickListener() {
			
			    @Override
			    public void onClick(View v) {
			    	Intent intent = new Intent();        							
					intent.putExtra("CompanyId", v.getTag().toString().split("##")[0]);
					intent.putExtra("CompanyName", v.getTag().toString().split("##")[1]);
					intent.setAction("com.bcb.choose.company");
					LogUtil.d("CompanyId", v.getTag().toString().split("##")[0]);
					sendBroadcast(intent);  
			    	finish();
			    }
		    });
		    tagContainer.addView(tagView);
		}
		
		loadHotData(); 
	}

	private void loadHotData() {
		hotView = View.inflate(Activity_Station_Change.this, R.layout.layout_station_hot, null);  	
	    hot_station_list = (LinearLayout) hotView.findViewById(R.id.station_list);
	    
		JSONObject obj = new JSONObject();
		try {
			obj.put("PageNow", PageNow);
			obj.put("PageSize", PageSize);			
		} catch (JSONException e) {
			e.printStackTrace();
		}

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MainpageHotStation, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressBar();
                try {
                    boolean flag = PackageUtil.getRequestStatus(response, Activity_Station_Change.this);
                    if(true == flag){
                        JSONObject obj = PackageUtil.getResultObject(response);
                        //判断JSON对象是否为空
                        if (obj != null) {
                            mHotStationList = App.mGson.fromJson(obj.toString(), HotStationListBean.class);
                        }
                        if (null != mHotStationList && null != mHotStationList.Records && mHotStationList.Records.size() > 0) {
                            int size = mHotStationList.Records.size();
                            for (int i = 0; i < size; i++) {
                                final int temp = i;
                                View view = View.inflate(Activity_Station_Change.this, R.layout.item_station_hotlist, null);
                                mapStationInfo(view, i, mHotStationList.Records.get(i));

                                view.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String id = mHotStationList.Records.get(temp).getCompanyId();
                                        String name = mHotStationList.Records.get(temp).getShortName();

                                        Intent intent = new Intent();
                                        intent.putExtra("CompanyId", id);
                                        intent.putExtra("CompanyName", name);
                                        intent.setAction("com.bcb.choose.company");
                                        sendBroadcast(intent);
                                        String first = App.saveUserInfo.getFirst();
                                        String second = App.saveUserInfo.getSecond();
                                        String third = App.saveUserInfo.getThird();

                                        App.saveUserInfo.setAllCompany("" + "##" + "全部公司");

                                        if (first.indexOf(id) != -1) {
                                            App.saveUserInfo.setFirst(id + "##" + name);
                                        }
                                        else if (second.indexOf(id) != -1) {
                                            App.saveUserInfo.setFirst(id + "##" + name);
                                            App.saveUserInfo.setSecond(first);
                                        }
                                        else if (third.indexOf(id) != -1) {
                                            App.saveUserInfo.setFirst(id + "##" + name);
                                            App.saveUserInfo.setSecond(first);
                                            App.saveUserInfo.setThird(second);
                                        }
                                        else {
                                            App.saveUserInfo.setFirst(id + "##" + name);
                                            App.saveUserInfo.setSecond(first);
                                            App.saveUserInfo.setThird(second);
                                        }
                                        UmengUtil.eventById(Activity_Station_Change.this, R.string.list_select_company);
                                        finish();
                                    }
                                });

                                if(i == 0){
                                    // 第一个view不用设置间隔
                                    hot_station_list.addView(view);
                                } else {
                                    LinearLayout ll = new LinearLayout(Activity_Station_Change.this);
                                    ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 20));
                                    hot_station_list.addView(ll);
                                    hot_station_list.addView(view);
                                }

                            }

                            hotstation_layout.addView(hotView);

                        }

                    }
                } catch (Exception e) {
                    LogUtil.d(TAG, "" + e.getMessage());
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_Station_Change.this, "网络好像有问题，请稍后重试");
                hideProgressBar();
            }
        });
        jsonRequest.setTag(BcbRequestTag.MainHotStationTag);
        requestQueue.add(jsonRequest);
	}	

    public void mapStationInfo(View view, final int position, final HotStationRecordsBean bean){
		//查看公司介绍
		((LinearLayout) view.findViewById(R.id.layout_check_company)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
                UmengUtil.eventById(Activity_Station_Change.this, R.string.list_select_com_intro);
				Activity_WebView.launche(Activity_Station_Change.this, bean.getShortName(), bean.getPageUrl());
			}
		});
		//公司名称
		((TextView) view.findViewById(R.id.company)).setText(bean.getShortName().isEmpty() ? "" : bean.getShortName());
		//公司地址
		((TextView) view.findViewById(R.id.address)).setText(bean.getName().isEmpty() ? "" : bean.getName());
		//可投项目
		((TextView) view.findViewById(R.id.productNum)).setText(bean.getPackageCount() + "个");
		//所有项目
		((TextView) view.findViewById(R.id.history_num)).setText(bean.getHistoryPackageCount() + "");
    }

	/********************* 转圈提示 **************************/
	//显示转圈提示
	private void showProgressBar(String title) {
		if(null == progressDialog) progressDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
		progressDialog.setMessage(title);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}
	//隐藏转圈提示
	private void hideProgressBar() {
		if(null != progressDialog && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}
}