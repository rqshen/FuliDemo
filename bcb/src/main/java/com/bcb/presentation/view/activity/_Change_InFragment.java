package com.bcb.presentation.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcb.R;
import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.UrlsOne;
import com.bcb.data.bean.ZQBGbean;
import com.bcb.utils.HttpUtils;
import com.bcb.utils.LogUtil;
import com.bcb.utils.MyListView;
import com.bcb.utils.PackageUtil;
import com.bcb.utils.ToastUtil;
import com.bcb.utils.TokenUtil;
import com.bcb.presentation.adapter.ZQZRadapter;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;
import com.bcb.base.BaseFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 转让中，已转让
 */
public class _Change_InFragment extends BaseFragment implements AdapterView.OnItemClickListener {
	private Context ctx;

	private MyListView lv;
	private int Status;//	状态 【1 转让中】【 0已完成】
	private int PageNow = 1;
	private int PageSize = 10;

	private List<ZQBGbean.RecordsBean> recordsBeans;
	private ZQZRadapter mCouponListAdapter;

	private LinearLayout null_data_layout;
	private boolean canLoadmore = true;
	private PullToRefreshLayout refreshLayout;
	private RelativeLayout loadmore_view;

	//******************************************************************************************

	/**
	 * 构造时把传入的参数带进来
	 */
	public static _Change_InFragment newInstance(int Status) {
		Bundle bundle = new Bundle();
		bundle.putInt("Status", Status);
		_Change_InFragment fragment = new _Change_InFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			Status = bundle.getInt("Status");
		}
	}
	//******************************************************************************************

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_change_in, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		this.ctx = view.getContext();
		null_data_layout = (LinearLayout) view.findViewById(R.id.null_data_layout);
		recordsBeans = new ArrayList<>();
		mCouponListAdapter = new ZQZRadapter(ctx, recordsBeans);
		lv = (MyListView) view.findViewById(R.id.listview_data_layout);
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
			obj.put("Status", Status);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.SEARCHCLAIMCONVEY, obj, TokenUtil.getEncodeToken(ctx), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【状态 1 转让中 0已完成】" + Status);

				LogUtil.i("bqt", "债权转让列表" + response.toString());

				try {
					if (PackageUtil.getRequestStatus(response, ctx)) {
						JSONObject obj = PackageUtil.getResultObject(response);
						ZQBGbean mCouponList = null;
						if (obj != null) {
							mCouponList = MyApplication.mGson.fromJson(obj.toString(), ZQBGbean.class);
						}
						//存在数据时
						if (null != mCouponList && null != mCouponList.Records && mCouponList.Records.size() > 0) {
							canLoadmore = true;
							PageNow++;
							setupListViewVisible(true);
							synchronized (this) {
								recordsBeans.addAll(mCouponList.Records);
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
					LogUtil.i("bqt", "【_Change_InFragment】【onResponse】" + e.toString());
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
		MyApplication.getInstance()
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
		Activity_ZRXQ.launche(ctx, recordsBeans.get(position).Id, Status);
	}
}