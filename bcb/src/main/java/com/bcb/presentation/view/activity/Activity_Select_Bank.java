package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.BankItem;
import com.bcb.data.util.LoadingLayoutMgr;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.adapter.BankListAdapter;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Activity_Select_Bank  extends Activity_Base implements OnItemClickListener {

	private static final String TAG = "Activity_Select_Bank";

	private ListView listview_bank;
	private List<BankItem> mBanklist;
	private BankListAdapter bankListAdapter;
	
	private RelativeLayout loading_layout;
	private LoadingLayoutMgr ld;

    private BcbRequestQueue requestQueue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Select_Bank.this);
		setBaseContentView(R.layout.activity_select_bank);
		setLeftTitleVisible(true);
		setTitleValue("选择银行");
        requestQueue = App.getInstance().getRequestQueue();
		loading_layout = (RelativeLayout) findViewById(R.id.load_layout);
		ld = new LoadingLayoutMgr(Activity_Select_Bank.this, false, "正在加载...");
		loading_layout.addView(ld.getLayout());
		init();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			
			case 0x100:
				loading_layout.removeAllViews();
				ToastUtil.alert(Activity_Select_Bank.this, "网络异常，请稍后重试");
				break;
			
			case 0:
				loading_layout.removeAllViews();
				bankListAdapter = new BankListAdapter(Activity_Select_Bank.this, mBanklist);
				listview_bank.setAdapter(bankListAdapter);
				break;
				
			default:
				break;
			}
		}
	};	
	
	private void init() {
		listview_bank = (ListView) findViewById(R.id.listview_bank);
		mBanklist = new ArrayList<BankItem>();	
		listview_bank.setOnItemClickListener(this);
		
		loadData();	
	}
	
    private void loadData() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.SupportBank, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(PackageUtil.getRequestStatus(response, Activity_Select_Bank.this)){
                    try {
                        JSONArray result = response.getJSONArray("result");
                        if (result != null) {
                            mBanklist = App.mGson.fromJson(result.toString(), new TypeToken<List<BankItem>>(){}.getType());
                        }
                        if(null != mBanklist && mBanklist.size() > 0){
                            handler.sendEmptyMessage(0);
                        }
                    } catch (Exception e) {
                        LogUtil.d(TAG, "" + e.getMessage());
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                handler.sendEmptyMessage(0x100);
            }
        });
        jsonRequest.setTag(BcbRequestTag.SupportBankTag);
        requestQueue.add(jsonRequest);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mBanklist.size() > 0) {
			BankItem bank = (BankItem) listview_bank.getItemAtPosition(position);
			Intent intent = new Intent();
			intent.putExtra("bankCode", bank.getBankCode());
			intent.putExtra("bankLogo", bank.getLogo());	
			intent.putExtra("bankName", bank.getBankName());	
			intent.putExtra("maxSingle", bank.getMaxSingle());	
			intent.putExtra("maxDay", bank.getMaxDay());		
			setResult(0, intent);
			Activity_Select_Bank.this.finish();
		}
	}
	
}
