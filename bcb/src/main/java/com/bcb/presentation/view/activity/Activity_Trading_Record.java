package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.data.bean.TradingRecordListBean;
import com.bcb.utils.HttpUtils;
import com.bcb.utils.LogUtil;
import com.bcb.utils.MyActivityManager;
import com.bcb.utils.MyListView;
import com.bcb.utils.PackageUtil;
import com.bcb.utils.ToastUtil;
import com.bcb.utils.TokenUtil;
import com.bcb.module.myinfo.financial.financialdetail.FinancialDetailActivity;
import com.bcb.presentation.adapter.TradingRecordAdapter;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.bcb.R.id.back_img;

/**
 * 1、投资记录OK
 */
public class Activity_Trading_Record extends Activity_Base {

	private TextView top_amount, left, right;

	private MyListView mTradingRecordListView;
	private int PageNow = 1;
	private int PageSize = 10;

	private List<TradingRecordListBean.InvetDetailBean.RecordsBean> recordsBeans;
	private TradingRecordAdapter mTradingRecordAdapter;

	private LinearLayout null_data_layout;
	private boolean canLoadmore = true;
	private PullToRefreshLayout refreshLayout;
	private RelativeLayout loadmore_view;

	public static void launche(Context ctx) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Trading_Record.class);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
		MyActivityManager myActivityManager = MyActivityManager.getInstance();
		myActivityManager.pushOneActivity(Activity_Trading_Record.this);
		setBaseContentView(R.layout.activity_trading_record);
		setLeftTitleVisible(true);
		setTitleValue("投资记录");
		layout_title.setBackgroundColor(getResources().getColor(R.color.red));
		title_text.setTextColor(getResources().getColor(R.color.white));
		dropdown.setImageResource(R.drawable.return_delault);
		dropdown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		(findViewById(back_img)).setVisibility(View.GONE);
		init();
	}

	private void init() {
		top_amount = (TextView) findViewById(R.id.top_amount);
		left = (TextView) findViewById(R.id.left);
		right = (TextView) findViewById(R.id.right);
		null_data_layout = (LinearLayout) findViewById(R.id.null_data_layout);
		recordsBeans = new ArrayList<>();
		mTradingRecordAdapter = new TradingRecordAdapter(Activity_Trading_Record.this, recordsBeans);
		mTradingRecordListView = (MyListView) findViewById(R.id.listview_data_layout);
		mTradingRecordListView.setOnItemClickListener(new onClickViewTradingRecord());
		mTradingRecordListView.setAdapter(mTradingRecordAdapter);

		//刷新
		loadmore_view = (RelativeLayout) findViewById(R.id.loadmore_view);
		refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
		refreshLayout.setRefreshResultView(false);
		refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				if (HttpUtils.isNetworkConnected(Activity_Trading_Record.this)) {
					PageNow = 1;
					recordsBeans.clear();
					loadTradingRecordData();
					loadmore_view.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.alert(Activity_Trading_Record.this, "网络异常，请稍后再试");
					refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				}
			}

			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
				if (HttpUtils.isNetworkConnected(Activity_Trading_Record.this)) {
					if (canLoadmore) {
						loadTradingRecordData();
					} else {
						loadmore_view.setVisibility(View.GONE);
						refreshLayout.loadmoreFinish(PullToRefreshLayout.NOMORE);
					}
				} else {
					ToastUtil.alert(Activity_Trading_Record.this, "网络异常，请稍后再试");
					refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
				}
			}
		});
		refreshLayout.autoRefresh();
	}

	private void loadTradingRecordData() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("PageNow", PageNow);
			obj.put("PageSize", PageSize);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.TradingRecord, obj, TokenUtil.getEncodeToken(this), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【Activity_Trading_Record】【onResponse】投资列表" + response.toString());

				try {
					if (PackageUtil.getRequestStatus(response, Activity_Trading_Record.this)) {
						TradingRecordListBean mTradingRecordList = MyApplication.mGson.fromJson(response.getJSONObject("result").toString(),
								TradingRecordListBean.class);
						if (mTradingRecordList == null) return;

						//累计交易金额
						top_amount.setText(String.format("%.2f", mTradingRecordList.getTotalAmount()));
						left.setText(String.format("%.2f", mTradingRecordList.TotalInvestorFuturePrincipalAmount));
						right.setText(String.format("%.2f", mTradingRecordList.getTotalInterestAmount()));

						TradingRecordListBean.InvetDetailBean detailBean = mTradingRecordList.getInvetDetail();

						//判断是否存在交易记录
						if (null != detailBean && null != detailBean.getRecords() && detailBean.getRecords().size() > 0) {
							canLoadmore = true;
							PageNow++;
							setupListViewVisible(true);
							synchronized (this) {
								recordsBeans.addAll(detailBean.getRecords());
							}
							//如果适配器存在，则刷新
							if (null != mTradingRecordAdapter) {
								mTradingRecordAdapter.notifyDataSetChanged();
							}
						} else {
							canLoadmore = false;
							if (null != mTradingRecordAdapter) {
								mTradingRecordAdapter.notifyDataSetChanged();
							}
							if (PageNow <= 1) {
								setupListViewVisible(false);
							}
						}
					} else {
						canLoadmore = false;
						if (recordsBeans == null || recordsBeans.size() <= 0) {
							setupListViewVisible(false);
						} else {
							setupListViewVisible(true);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.i("bqt", "【Activity_Trading_Record】【onResponse】" + e.toString());
					
				} finally {
					refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
					refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.i("bqt", "【Activity_Trading_Record】【onErrorResponse】" + error.toString());
				
				refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
				if (recordsBeans == null || recordsBeans.size() <= 0) {
					setupListViewVisible(false);
				} else {
					setupListViewVisible(true);
				}
			}
		});
		jsonRequest.setTag(BcbRequestTag.TradeRecordTag);
		MyApplication.getInstance().getRequestQueue().add(jsonRequest);
	}

	//设置是否显示ListView
	private void setupListViewVisible(boolean visible) {
		if (visible) {
			null_data_layout.setVisibility(View.GONE);
			mTradingRecordListView.setVisibility(View.VISIBLE);
		} else {
			null_data_layout.setVisibility(View.VISIBLE);
			mTradingRecordListView.setVisibility(View.GONE);
		}
	}
	class onClickViewTradingRecord implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			FinancialDetailActivity.launche(Activity_Trading_Record.this, recordsBeans.get(position).getOrderNo() + "");
		}
	}
}