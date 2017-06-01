package com.bcb.presentation.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.constant.H5UrlConstant;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.UrlsOne;
import com.bcb.data.bean.ClaimConveyBeanBQT;
import com.bcb.utils.LogUtil;
import com.bcb.utils.TokenUtil;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.projectdetail.ProjectDetailActivity;
import com.bcb.presentation.view.custom.AlertView.AlertView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 申请转让、取消转让
 */
public class Activity_Trading_Cancle extends Activity_Base {
	private String OrderNo;
	private int StatusCode;// 0：不能申请转让 1：已完成 2：可以转让 3：转让中
	CheckBox cbCheck;
	ListView lv;
	TextView tv, tv_text;
	ViewHolder holder;
	List<Item> items;
	TradingAdapter adapter;
	ClaimConveyBeanBQT bean;

	public static void launche(Activity ctx, String OrderNo, int StatusCode) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Trading_Cancle.class);
		intent.putExtra("OrderNo", OrderNo);
		intent.putExtra("StatusCode", StatusCode);
		ctx.startActivityForResult(intent, 100);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView(R.layout.activity_trading_cancle);

		cbCheck = (CheckBox) findViewById(R.id.cbCheck);
		lv = (ListView) findViewById(R.id.lv);
		tv = (TextView) findViewById(R.id.tv);
		tv_text = (TextView) findViewById(R.id.tv_text);
		tv_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ProjectDetailActivity.launche(Activity_Trading_Cancle.this, "转让协议", H5UrlConstant.ZRXY);
			}
		});
		setLeftTitleVisible(true);

		Intent intent = getIntent();
		if (intent != null) {
			OrderNo = intent.getStringExtra("OrderNo");
			StatusCode = intent.getIntExtra("StatusCode", 0);
		}
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
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.GETORDERCLAIMCONVEYINFO, obj, TokenUtil.getEncodeToken(this), new
				BcbRequest.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【FinancialDetailActivity】【onResponse】债权转让信息" + response.toString());
				try {
					bean = MyApplication.mGson.fromJson(response.getJSONObject("result").toString(), ClaimConveyBeanBQT.class);
					tv.setVisibility(View.VISIBLE);
					items = new ArrayList<Item>();
					items.add(new Item("债权订单", bean.PackageId));
					items.add(new Item("转让金额", String.format("%.2f", bean.Amount)));
					items.add(new Item("剩余期限", bean.LeftDuration + "天"));
					items.add(new Item("转让费率", String.format("%.2f", bean.Rate) + "%"));
					items.add(new Item("转让费用", String.format("%.2f", bean.Cost)));
					items.add(new Item("待收金额", String.format("%.2f", bean.WaitAmount)));
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					items.add(new Item("完成时间", format.format(format.parse(bean.EndTime))));
					adapter = new TradingAdapter();
					lv.setAdapter(adapter);

					switch (StatusCode) {
						case 2:
							setTitleValue("申请转让");
							tv.setText("申请转让");
							cbCheck.setVisibility(View.VISIBLE);
							tv_text.setVisibility(View.VISIBLE);
							setRightTitleValue("转让说明", new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									AlertView.Builder ibuilder = new AlertView.Builder(Activity_Trading_Cancle.this);
									ibuilder.setTitle("转让说明");
									ibuilder.setGravity(Gravity.LEFT);
									ibuilder.setMessage(R.string.zqzr);
									ibuilder.setNegativeButton("我知道了", null);
									AlertView view = ibuilder.create();
									view.show();
								}
							});
							tv.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									if (!cbCheck.isChecked()) {
										Toast.makeText(Activity_Trading_Cancle.this, "请阅读并同意转让协议", Toast.LENGTH_SHORT).show();
										return;
									}
									requestZR(UrlsOne.REQUESTZR);
								}
							});
							break;
						case 3:
							setTitleValue("取消转让");
							tv.setText("取消转让");
							cbCheck.setVisibility(View.GONE);
							tv_text.setVisibility(View.GONE);
							tv.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									requestZR(UrlsOne.UNREQUESTZR);
								}
							});
							break;
						default:
							break;
					}
				} catch (Exception e) {
					LogUtil.d("bqt", "" + e.getMessage());
				}
			}

			@Override
			public void onErrorResponse(Exception error) {

			}
		});
		jsonRequest.setTag(UrlsOne.GETORDERCLAIMCONVEYINFO);
		MyApplication.getInstance().getRequestQueue().add(jsonRequest);
	}
	//******************************************************************************************

	/**
	 * 申请或取消转让
	 */
	private void requestZR(String url) {
		LogUtil.i("bqt", "【Activity_Trading_Cancle】【onResponse】路径" + url);
		JSONObject obj = new JSONObject();
		try {
			obj.put("OrderNo", OrderNo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(this), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【Activity_Trading_Cancle】【onResponse】申请债权转让" + response.toString());
				if (response.optBoolean("result", false)) {
					if (StatusCode==2) {//申请转让
						Activity_Tips_FaileOrSuccess.launche(Activity_Trading_Cancle.this, Activity_Tips_FaileOrSuccess.ZR_SUCCESS,
								"您已成功申请债权转让");
					}else
						Activity_Tips_FaileOrSuccess.launche(Activity_Trading_Cancle.this, Activity_Tips_FaileOrSuccess.ZR_SUCCESS,
								"成功取消债权转让");
					Intent intent=new Intent();
					intent.putExtra("rufush",true);
					setResult(200,intent);
				} else {
					Activity_Tips_FaileOrSuccess.launche(Activity_Trading_Cancle.this, Activity_Tips_FaileOrSuccess.ZR_FAILED,
							response.optString("message"));
				}
				finish();
			}

			@Override
			public void onErrorResponse(Exception error) {

			}
		});
		jsonRequest.setTag(UrlsOne.REQUESTZR);
		MyApplication.getInstance().getRequestQueue().add(jsonRequest);
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
				convertView = LayoutInflater.from(Activity_Trading_Cancle.this).inflate(R.layout.rading_cancle_item, null);
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