package com.bcb.presentation.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.AreaBean;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TokenUtil;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Activity_Province extends Activity_Base {

	private static final String TAG = "Activity_Province";
	
	private Receiver mReceiver;
	private ListView mListView;
	private List<AreaBean> list;

    private BcbRequestQueue requestQueue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Province.this);
		setBaseContentView(R.layout.activity_choose);
		setLeftTitleVisible(true);
		setTitleValue("选择省份");
        requestQueue = App.getInstance().getRequestQueue();
		mListView = (ListView) findViewById(R.id.list);
		mListView.setOnItemClickListener(mOnItemClickListener);
		loadData();
		// 实例化过滤器并设置要过滤的广播   
		IntentFilter intentFilter = new IntentFilter("com.bcb.bank.area.close"); 
		mReceiver = new Receiver();
		this.registerReceiver(mReceiver, intentFilter); 
	}
	
	class Receiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.bcb.bank.area.close")){		
				finish();
			}
		}
		
	}
	
	@Override
	protected void onDestroy() {	
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}


	private void loadData(){
		String url = UrlsOne.GetProvinceList;
		JSONObject obj = new org.json.JSONObject();
		try {		
			obj.put("ParentId", "1");
		} catch (JSONException e) {
			e.printStackTrace();
		}

        BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(PackageUtil.getRequestStatus(response, Activity_Province.this)){
                    try {
                        JSONArray result = response.getJSONArray("result");
                        //判断JSON对象是否为空
                        if (result != null) {
                            list = App.mGson.fromJson(result.toString(), new TypeToken<List<AreaBean>>(){}.getType());
                        }
                        if(null != list && list.size() > 0){
                            mListView.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        LogUtil.d(TAG, "" + e.getMessage());
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.GetProvinceListTag);
        requestQueue.add(jsonRequest);
	}

	BaseAdapter adapter = new BaseAdapter(){

		@Override
		public int getCount() {
			return list==null?0:list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list==null?null:list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			arg1 = View.inflate(Activity_Province.this, R.layout.item_addressvalue, null);
			((TextView)arg1.findViewById(R.id.value)).setText(list.get(arg0).Name);
			return arg1;
		}
	};
	
	
	OnItemClickListener mOnItemClickListener = new OnItemClickListener(){
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,long arg3) {			
			int pid = list.get(pos).Id;
			String pcode = list.get(pos).Code;
			String pname = list.get(pos).Name;		
			
			Intent intent = new Intent();
			intent.putExtra("pid", pid);
			intent.putExtra("pcode", pcode);
			intent.putExtra("pname", pname);
			intent.setClass(Activity_Province.this, Activity_City.class);
			startActivity(intent);
		}
		
	};

}

