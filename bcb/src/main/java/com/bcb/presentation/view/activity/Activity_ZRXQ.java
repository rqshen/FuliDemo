package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.ClaimConveyDetailBean;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 债权转让详情
 */
public class Activity_ZRXQ extends Activity_Base {
	private String Id;
	ListView lv;
	ViewHolder holder;
	List<Item> items;
	TradingAdapter adapter;
	ClaimConveyDetailBean bean;

	public static void launche(Context ctx, String Id) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_ZRXQ.class);
		intent.putExtra("Id", Id);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView(R.layout.activity_trading_cancle);

		setTitleValue("转让详情");
		setLeftTitleVisible(true);
		setRightTitleValue("转让协议", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity_Browser.launche(Activity_ZRXQ.this, "转让协议", "http://192.168.20.14/static/CreditAgreement.html");
			}
		});
		lv = (ListView) findViewById(R.id.lv);

		Intent intent = getIntent();
		if (intent != null) Id = intent.getStringExtra("Id");
		loadClaimConveyData();
	}

	/**
	 * 债权转让详情
	 */
	private void loadClaimConveyData() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("Id", Id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.CLAIMCONVEYDETAIL, obj, TokenUtil.getEncodeToken(this), new
				BcbRequest.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "债权转让详情" + response.toString());
				try {
					bean = App.mGson.fromJson(response.getJSONObject("result").toString(), ClaimConveyDetailBean.class);
					items = new ArrayList<Item>();
					items.add(new Item("债权订单", bean.OrderNo));
					items.add(new Item("订单状态", bean.Status));//Status	string	状态 ：申购中 归档 下架
					items.add(new Item("债权金额", String.format("%.2f", bean.Amount)));
					items.add(new Item("转让金额", String.format("%.2f", bean.Already)));
					items.add(new Item("剩余金额", String.format("%.2f", bean.Balance)));
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					items.add(new Item("申请时间", format.format(format.parse(bean.CreateTime))));
					adapter = new TradingAdapter();
					lv.setAdapter(adapter);
				} catch (Exception e) {
					LogUtil.d("bqt", "" + e.getMessage());
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.i("bqt", "【Activity_ZRXQ】【onErrorResponse】" + error.toString());
			}
		}

		);
		jsonRequest.setTag(UrlsOne.CLAIMCONVEYDETAIL);
		App.getInstance().getRequestQueue().add(jsonRequest);
	}

	//******************************************************************************************
	class TradingAdapter extends BaseAdapter {

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
			Item item = items.get(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(Activity_ZRXQ.this).inflate(R.layout.rading_cancle_item, null);
				holder = new ViewHolder();
				holder.tv_left = (TextView) convertView.findViewById(R.id.tv_left);
				holder.tv_right = (TextView) convertView.findViewById(R.id.tv_right);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_left.setText(item.title);
			holder.tv_right.setText(item.value);
			return convertView;
		}
	}

	class ViewHolder {
		public TextView tv_left;
		public TextView tv_right;
	}

	class Item {
		public Item(String title, String value) {
			this.title = title;
			this.value = value;
		}

		public String title;
		public String value;
	}
}