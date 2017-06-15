package com.bcb.presentation.view.activity;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.bcb.base.old.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.data.bean.AreaBean;
import com.bcb.util.MyActivityManager;
import com.bcb.util.TokenUtil;
import com.google.gson.reflect.TypeToken;
import com.bcb.network.UrlsOne;
import com.bcb.util.LogUtil;
import com.bcb.util.PackageUtil;
import com.bcb.util.ToastUtil;

public class Activity_City extends Activity_Base {

	private static final String TAG = "Activity_City";
	
	private int pid;
	private String pcode;
	private String pname;
	
	private Receiver mReceiver;
	
	private ListView mListView;
	private List<AreaBean> list;

    private BcbRequestQueue requestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_City.this);
		setBaseContentView(R.layout.activity_city);
		setLeftTitleVisible(true);
		setTitleValue("选择城市");
		requestQueue = MyApplication.getInstance().getRequestQueue();
        pid = getIntent().getIntExtra("pid", 0);
		pcode = getIntent().getStringExtra("pcode");
		pname = getIntent().getStringExtra("pname");
		((TextView)findViewById(R.id.txt_value)).setText(pname);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setOnItemClickListener(mOnItemClickListener);
		loadData();
		//注册广播   
		IntentFilter intentFilter = new IntentFilter("com.bcb.bank.area.close"); 
		mReceiver = new Receiver();
		registerReceiver(mReceiver, intentFilter); 
		
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

	private void loadData() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("ParentId", pid);

		} catch (JSONException e) {
			e.printStackTrace();
		}
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.GetCityList, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(PackageUtil.getRequestStatus(response, Activity_City.this)){
                    try {
                        JSONArray result = response.getJSONArray("result");
                        //判断JSON对象是否为空
                        if (result != null) {
                            list = MyApplication.mGson.fromJson(result.toString(), new TypeToken<List<AreaBean>>(){}.getType());
                        }
                        if(null != list && list.size()>0){
                            mListView.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        LogUtil.d(TAG, "" + e.getMessage());
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_City.this, "网络好像有问题，请稍后重试");
            }
        });
        jsonRequest.setTag(BcbRequestTag.GetCityListTag);
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
			arg1 = View.inflate(Activity_City.this, R.layout.item_addressvalue, null);
			((TextView)arg1.findViewById(R.id.value)).setText(list.get(arg0).Name);
			return arg1;
		}
	};
	
	
	public static final String PARAM_pcode = "pcode";
	public static final String PARAM_pname = "pname";
	public static final String PARAM_cityObject = "cityObject";
	
	OnItemClickListener mOnItemClickListener = new OnItemClickListener(){
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,long arg3) {
			
			String ccode = list.get(pos).Code;
			String ccname = list.get(pos).Name;
								
            Intent intent1 = new Intent();
            intent1.putExtra(PARAM_pcode, pcode);
            intent1.putExtra(PARAM_pname, pname);
            intent1.putExtra(PARAM_cityObject, list.get(pos));
			intent1.setAction("com.bcb.bank.area.complete");
			sendBroadcast(intent1);
			
			Intent intent2 = new Intent();		
			intent2.setAction("com.bcb.bank.area.close");
			sendBroadcast(intent2);
		
		}
		
	};
	
}
