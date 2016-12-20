package com.bcb.presentation.view.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.event.BroadcastEvent;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.ProductListBean;
import com.bcb.data.bean.ProductRecordsBean;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyListView;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.adapter.ProductAdapter;
import com.bcb.presentation.view.activity.Activity_NormalProject_Introduction;
import com.bcb.presentation.view.activity.Activity_Station_Change;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class Frag_Product extends Frag_Base implements OnClickListener {
	
	private static final String TAG = "Frag_Product";

	private MyListView mListView;
	private List<ProductRecordsBean> recordsBeans;
	private ProductAdapter mProductAdapter;

	private boolean isFirstLoad = true;

	private boolean canLoadMore = false;
	private int PageNow;
	private int PageSize = 10;
	//标题
	private TextView title_text, left_text;
	private ImageView dropdown;

	private String CompanyId, CompanyName = "";
	private LinearLayout null_data_layout;
	private TextView null_data_tip;

	private Context ctx;

	private Receiver receiver;

	//刷新
	private PullToRefreshLayout refreshLayout;
	//加载更多
	private RelativeLayout loadmore_view;

	private BcbRequestQueue requestQueue;

	public Frag_Product() {
		super();
		EventBus.getDefault()
				.register(this);
	}

	@SuppressLint("ValidFragment")
	public Frag_Product(Context ctx, String CompanyId) {
		super();
		this.ctx = ctx;
		this.CompanyId = CompanyId;
	}
	
	public void UpdateCompanyId(String CompanyId) {
		this.CompanyId = CompanyId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_product, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		this.ctx = view.getContext();
		requestQueue = App.getInstance()
				.getRequestQueue();
		//注册广播
		receiver = new Receiver();
		ctx.registerReceiver(receiver, new IntentFilter("com.bcb.choose.company"));
		ctx.registerReceiver(receiver, new IntentFilter("com.bcb.project.buy.success"));
		//标题
		title_text = (TextView) view.findViewById(R.id.title_text);
		title_text.setText("产品列表"); //setTitleValue("产品列表");
		//切换公司
		left_text = (TextView) view.findViewById(R.id.left_text);
		left_text.setText("全部公司");
		left_text.setVisibility(View.GONE);
		left_text.setOnClickListener(this);
		dropdown = (ImageView) view.findViewById(R.id.dropdown);
		dropdown.setVisibility(View.GONE);
		dropdown.setOnClickListener(this);

		CompanyId = "";
		PageNow = 1;
		null_data_layout = (LinearLayout) view.findViewById(R.id.null_data_layout);
		null_data_tip = (TextView) view.findViewById(R.id.null_data_tip);

		//产品列表数据
		recordsBeans = new ArrayList<ProductRecordsBean>();
		mProductAdapter = new ProductAdapter(ctx, recordsBeans);
		mListView = (MyListView) view.findViewById(R.id.listview_data_layout);
		mListView.setOnItemClickListener(new onClickViewProduct());
		mListView.setAdapter(mProductAdapter);
		//刷新
		loadmore_view = (RelativeLayout) view.findViewById(R.id.loadmore_view);
		refreshLayout = ((PullToRefreshLayout) view.findViewById(R.id.refresh_view));
		refreshLayout.setRefreshResultView(false);
		refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				//加载数据
				if (HttpUtils.isNetworkConnected(ctx)) {
					//清空原来的数据
					recordsBeans.clear();
					PageNow = 1;
					loadData();
					loadmore_view.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.alert(ctx, "网络延迟，请稍后再试");
					null_data_tip.setText("网络延迟，请稍后再试");
					refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				}
			}

			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
				//加载数据
				if (HttpUtils.isNetworkConnected(ctx)) {
					if (canLoadMore) {
						loadData();
					} else {
						loadmore_view.setVisibility(View.GONE);
						refreshLayout.loadmoreFinish(PullToRefreshLayout.NOMORE);
					}
				} else {
					ToastUtil.alert(ctx, "网络延迟，请稍后再试");
					null_data_tip.setText("网络延迟，请稍后再试");
					refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
				}
			}
		});
		refreshLayout.autoRefresh();
	}

	public void loadData() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("PageNow", PageNow);
			obj.put("PageSize", PageSize);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MainpageProduct, obj, TokenUtil.getEncodeToken(ctx), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "产品列表" + response.toString());

				try {
					//如果存在返回数据时
					if (PackageUtil.getRequestStatus(response, ctx)) {
						JSONObject obj = PackageUtil.getResultObject(response);
						//获取数据，要判断数据是否存在
						ProductListBean productListBean = null;
						//判断JSON对象是否为空
						if (obj != null) {
							productListBean = App.mGson.fromJson(obj.toString(), ProductListBean.class);
						}
						//存在产品列表
						if (null != productListBean && null != productListBean.Records && productListBean.Records.size() > 0) {
							canLoadMore = true;
							PageNow++;
							setupListViewVisible(true);
							//线程加锁
							synchronized (this) {
								recordsBeans.addAll(productListBean.Records);
							}
							//刷新适配器，如果存在适配器，则刷新；没有则新建并绑定适配器
							if (null != mProductAdapter) {
								mProductAdapter.notifyDataSetChanged();
							}
						} else {
							if (null != mProductAdapter) {
								mProductAdapter.notifyDataSetChanged();
							}
							canLoadMore = false;
							if (PageNow <= 1) {
								setupListViewVisible(false);
							}
						}
					} else {
						JSONObject obj = PackageUtil.getResultObject(response);
						if (null == obj) {
							//刷新适配器，如果存在适配器，则刷新；没有则新建并绑定适配器
							if (null != recordsBeans && null != mProductAdapter) {
								recordsBeans.clear();
								mProductAdapter.notifyDataSetChanged();
							}
						} else {
							canLoadMore = false;
							if (recordsBeans == null || recordsBeans.size() <= 0) {
								setupListViewVisible(false);
							} else {
								setupListViewVisible(true);
							}
						}
					}
				} catch (Exception e) {
					//刷新适配器，如果存在适配器，则刷新；没有则新建并绑定适配器
					if (null != recordsBeans && null != mProductAdapter) {
						recordsBeans.clear();
						mProductAdapter.notifyDataSetChanged();
					}
					e.printStackTrace();
				} finally {
					refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
					refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
				if (recordsBeans == null || recordsBeans.size() <= 0) {
					setupListViewVisible(false);
				}
			}
		});
		jsonRequest.setTag(BcbRequestTag.MainProductTag);
		requestQueue.add(jsonRequest);
	}

	//******************************************************************************************
	class onClickViewProduct implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			if (recordsBeans.get(position).Status == 10) {//项目状态
				return;
			}
			if (recordsBeans.get(position).Status == 20) {
				UmengUtil.eventById(ctx, R.string.list_bid_avi);
			} else {
				UmengUtil.eventById(ctx, R.string.list_bid_unavi);
			}
			//0正常标，1转让标，2福鸡包
			int type = 0;
			if (recordsBeans.get(position).Type.equals("claim_convey")) type = 1;
			else if (recordsBeans.get(position).Type.equals("mon_package")) type = 2;
			Activity_NormalProject_Introduction.launche2(ctx, recordsBeans.get(position).PackageId, 0, type);//标类型
		}
	}

	//******************************************************************************************
	private void setupListViewVisible(boolean status) {
		// 如果存在数据的时候，显示
		if (status) {
			null_data_layout.setVisibility(View.GONE);
		} else {
			null_data_tip.setText("该公司暂无产品");
			null_data_layout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dropdown:
			case R.id.left_text:
				UmengUtil.eventById(ctx, R.string.list_select);
				Activity_Station_Change.launche(ctx, left_text.getText()
						.toString());
				break;
		}
	}

	class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals("com.bcb.project.buy.success")) {
				refreshLayout.autoRefresh();
			} else if (intent.getAction()
					.equals("com.bcb.choose.company")) {
				CompanyId = intent.getStringExtra("CompanyId");
				CompanyName = intent.getStringExtra("CompanyName");
				left_text.setText(CompanyName.isEmpty() ? "全部公司" : CompanyName);
				App.saveUserInfo.setCurrentCompanyId(CompanyId);
				App.saveUserInfo.setCurrentCompanyName(CompanyName);
				refreshLayout.autoRefresh();
			}
		}
	}

	//接收事件
	public void onEventMainThread(BroadcastEvent event) {
		String flag = event.getFlag();
		if (!TextUtils.isEmpty(flag)) {
			switch (flag) {
				case BroadcastEvent.REFRESH:
				case BroadcastEvent.LOGOUT:
				case BroadcastEvent.LOGIN:
					refreshLayout.autoRefresh();
					//                    Log.d("1234", "Frag_product refresh");
					break;
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ctx.unregisterReceiver(receiver);
		EventBus.getDefault()
				.unregister(this);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	public ProductRecordsBean getFirstItemData() {
		if (recordsBeans != null && recordsBeans.size() > 0) {
			return recordsBeans.get(0);
		}
		return null;
	}
}