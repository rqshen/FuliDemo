package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.TZJLbean;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyListView;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.adapter.TZJLAdapter;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;
import com.bcb.presentation.view.fragment.Frag_Base;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Frag_TZJL_2 extends Frag_Base implements AdapterView.OnItemClickListener {
	private Context ctx;

	private MyListView lv;
	private int Status;//	【 0稳赢，打包】【1涨薪宝，三标】
	private int Tab;//	1为持有中，2为已结束

	private int PageNow = 1;
	private int PageSize = 10;

	TZJLbean tzjLbean;
	private List<TZJLbean.InvetDetailBean.RecordsBean> recordsBeans;
	private TZJLAdapter mCouponListAdapter;

	private LinearLayout null_data_layout;
	private boolean canLoadmore = true;
	private PullToRefreshLayout refreshLayout;
	private RelativeLayout loadmore_view;

	//******************************************************************************************

	/**
	 * 构造时把传入的参数带进来，
	 */
	public static Frag_TZJL_2 newInstance(int Status, int Tab) {
		Bundle bundle = new Bundle();
		bundle.putInt("Status", Status);
		bundle.putInt("Tab", Tab);
		Frag_TZJL_2 fragment = new Frag_TZJL_2();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			Status = bundle.getInt("Status");
			Tab = bundle.getInt("Tab");
		}
	}
	//******************************************************************************************

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_tzjl, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		this.ctx = view.getContext();
		null_data_layout = (LinearLayout) view.findViewById(R.id.null_data_layout);
		recordsBeans = new ArrayList<>();
		mCouponListAdapter = new TZJLAdapter(ctx, recordsBeans);
		lv = (MyListView) view.findViewById(R.id.listview_data_layout);
		view.findViewById(R.id.button_confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ctx, Activity_CP.class));
			}
		});
		lv.setOnItemClickListener(this);
		lv.setAdapter(mCouponListAdapter);
		//刷新
		loadmore_view = (RelativeLayout) view.findViewById(R.id.loadmore_view);
		refreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
		refreshLayout.setRefreshResultView(false);
		refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

				if (HttpUtils.isNetworkConnected(ctx)) {
					PageNow = 1;
					recordsBeans.clear();
					loadData();
					loadmore_view.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.alert(ctx, "网络异常，请稍后再试");
					refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				}
			}

			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

				if (HttpUtils.isNetworkConnected(ctx)) {
					if (canLoadmore) {
						loadData();
					} else {
						loadmore_view.setVisibility(View.GONE);
						refreshLayout.loadmoreFinish(PullToRefreshLayout.NOMORE);
					}
				} else {
					ToastUtil.alert(ctx, "网络异常，请稍后再试");
					refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
				}
			}
		});
		refreshLayout.autoRefresh();
	}

	//******************************************************************************************
	private void loadData() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("PageNow", PageNow);
			obj.put("PageSize", PageSize);
			obj.put("Tab", Tab);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = UrlsOne.WYB_JL;//打包，稳赢
		if (Status == 1) url = UrlsOne.ZXB_JL;
		BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(ctx), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【打包稳赢宝投资记录】" + Status + "--" + Tab + "--" + response.toString());

				try {
					if (PackageUtil.getRequestStatus(response, ctx)) {
						JSONObject obj = PackageUtil.getResultObject(response);
						tzjLbean = null;
						if (obj != null) {
							tzjLbean = App.mGson.fromJson(obj.toString(), TZJLbean.class);
							if (Status == 0) {
								((Frag_TZJL_1) getParentFragment()).yjsy.setText(String.format("%.2f", tzjLbean.PackInterest));
								((Frag_TZJL_1) getParentFragment()).ztbj.setText(String.format("%.2f", tzjLbean.PackPrincipal));
							} else {
								((Frag_TZJL_1) getParentFragment()).yjsy.setText(String.format("%.2f", tzjLbean.OriginalInterest));
								((Frag_TZJL_1) getParentFragment()).ztbj.setText(String.format("%.2f", tzjLbean.OriginalPrincipal));
							}
						}

						//存在数据时
						if (null != tzjLbean && null != tzjLbean.InvetDetail.Records && tzjLbean.InvetDetail.Records.size() > 0) {
							canLoadmore = true;
							PageNow++;
							setupListViewVisible(true);
							synchronized (this) {
								recordsBeans.addAll(tzjLbean.InvetDetail.Records);
								//								Collections.sort(recordsBeans);
							}
							if (null != mCouponListAdapter) {
								mCouponListAdapter.notifyDataSetChanged();
							}
						} else {
							canLoadmore = false;
							if (null != mCouponListAdapter) {
								mCouponListAdapter.notifyDataSetChanged();
							}
							if (PageNow <= 1) {
								setupListViewVisible(false);
							}
						}
					}
					//没有返回数据，要判断原先是否存在数据
					else {
						canLoadmore = false;
						if (recordsBeans == null || recordsBeans.size() <= 0) {
							setupListViewVisible(false);
						} else {
							setupListViewVisible(true);
						}
					}
				} catch (Exception e) {
					LogUtil.i("bqt", "【Frag_Change_In】【onResponse】" + e.toString());
				} finally {
					refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
					refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.i("bqt", "2");
				refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
				if (recordsBeans == null || recordsBeans.size() <= 0) {
					setupListViewVisible(false);
				} else {
					setupListViewVisible(true);
				}
			}
		});
		jsonRequest.setTag(UrlsOne.SEARCHCLAIMCONVEY);
		App.getInstance()
				.getRequestQueue()
				.add(jsonRequest);
	}

	//加载优惠券，true 表示有数据，false 表示没数据
	private void setupListViewVisible(boolean state) {
		if (state) {
			null_data_layout.setVisibility(View.GONE);
			lv.setVisibility(View.VISIBLE);
		} else {
			null_data_layout.setVisibility(View.VISIBLE);
			lv.setVisibility(View.GONE);
		}
	}

	//******************************************************************************************
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//投资详情
		Activity_Project_Investment_Details.launche(ctx, recordsBeans.get(position).OrderNo + "", Status);
	}
}