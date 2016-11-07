package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.RadingRecordBean;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.TokenUtil;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 转让记录，变更记录
 */
public class Activity_Rading_Record extends Activity_Base {
	ListView lv;
	ViewHolder holder;
	List<RadingRecordBean> items;
	RadingAdapter adapter;
	private String OrderNo;
	private LinearLayout null_data_layout;

	public static void launche(Context ctx, String OrderNo) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Rading_Record.class);
		intent.putExtra("OrderNo", OrderNo);
		ctx.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView(R.layout.activity_rading_record);
		setLeftTitleVisible(true);
		setTitleValue("转让记录");
		lv = (ListView) findViewById(R.id.lv);
		null_data_layout = (LinearLayout) findViewById(R.id.null_data_layout);
		OrderNo = getIntent().getStringExtra("OrderNo");
		loadClaimConveyData();
	}

	/**
	 * 转让记录
	 */
	private void loadClaimConveyData() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("OrderNo", OrderNo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.ClaimConveyDate, obj, TokenUtil.getEncodeToken(this), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【Activity_Project_Investment_Details】【onResponse】债权转让信息" + response.toString());
				try {
					items = App.mGson.fromJson(response.getJSONArray("result").toString(), new TypeToken<List<RadingRecordBean>>() {}
							.getType());
					if (items != null && items.size() > 0) {
						null_data_layout.setVisibility(View.GONE);
						lv.setVisibility(View.VISIBLE);
						adapter = new RadingAdapter();
						lv.setAdapter(adapter);
					} else {
						null_data_layout.setVisibility(View.VISIBLE);
						lv.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					LogUtil.d("bqt", "" + e.getMessage());
				}
			}

			@Override
			public void onErrorResponse(Exception error) {

			}
		});
		jsonRequest.setTag(UrlsOne.ClaimConveyDate);
		App.getInstance().getRequestQueue().add(jsonRequest);
	}

	//******************************************************************************************
	class RadingAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return items.size();
		}
		
		@Override
		public Object getItem(int position) {
			return items.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RadingRecordBean bean = items.get(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(Activity_Rading_Record.this).inflate(R.layout.rading_record_item, null);
				holder = new ViewHolder();
				holder.tv_left = (TextView) convertView.findViewById(R.id.tv_left);
				holder.tv_right = (TextView) convertView.findViewById(R.id.tv_right);
				convertView.setTag(holder);
			} else holder = (ViewHolder) convertView.getTag();
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			try {
				holder.tv_left.setText(format.format(format.parse(bean.ClaimConveyDate)));
				holder.tv_right.setText(String.format("%.2f", bean.Amount));
			} catch (ParseException e) {
				e.printStackTrace();
				LogUtil.i("bqt", "【RadingAdapter】【getView】" + e.toString());
			}
			return convertView;
		}
	}
	
	class ViewHolder {
		public TextView tv_left;
		public TextView tv_right;
	}
}
