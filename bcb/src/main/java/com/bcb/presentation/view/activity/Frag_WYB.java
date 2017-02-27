package com.bcb.presentation.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.MainListBean2;
import com.bcb.data.bean.WYBbean;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyListView;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.adapter.MainAdapter2;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;
import com.bcb.presentation.view.fragment.Frag_Base;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2017/2/27 17:05
 */
public class Frag_WYB extends Frag_Base implements AdapterView.OnItemClickListener {
	private Context ctx;

	private MyListView lv;
	private int Status;//	【 0稳赢】【1涨薪宝】
	private int PageNow = 1;
	private int PageSize = 10;

	private List<MainListBean2.JpxmBean> recordsBeans;
	private MainAdapter2 mCouponListAdapter;

	private LinearLayout null_data_layout;
	private boolean canLoadmore = true;
	private PullToRefreshLayout refreshLayout;
	private RelativeLayout loadmore_view;

	//******************************************************************************************

	/**
	 * 构造时把传入的参数带进来，
	 */
	public static Frag_WYB newInstance(int Status) {
		Bundle bundle = new Bundle();
		bundle.putInt("Status", Status);
		Frag_WYB fragment = new Frag_WYB();
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
		return inflater.inflate(R.layout.fragment_wyb, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		this.ctx = view.getContext();
		null_data_layout = (LinearLayout) view.findViewById(R.id.null_data_layout);
		recordsBeans = new ArrayList<>();
		mCouponListAdapter = new MainAdapter2(ctx, recordsBeans);
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
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = UrlsOne.WYB;
		if (Status == 1) url = UrlsOne.ZXB;
		BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(ctx), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【稳赢宝】" + Status + "--" + response.toString());

				try {
					if (PackageUtil.getRequestStatus(response, ctx)) {
						JSONObject obj = PackageUtil.getResultObject(response);
						WYBbean mCouponList = null;
						if (obj != null) {
							mCouponList = App.mGson.fromJson(obj.toString(), WYBbean.class);
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
		MainListBean2.JpxmBean jpxm = recordsBeans.get(position);
		//判断是否属于新标预告的状态，根据状态来判断是否可点击
		if (jpxm.Status == 10) Toast.makeText(ctx, "Status == 10，不可购买", Toast.LENGTH_SHORT).show();
		//0正常标，1转让标，2福鸡包
		int type = 0;//prj_package则为普通标
		if (jpxm.Type.equals("claim_convey")) type = 1;//claim_convey则为债权转让标
		else if (jpxm.Type.equals("mon_package")) type = 2;//mon_package为福鸡宝
		if (jpxm.Old) Activity_NormalProject_Introduction.launche2(ctx, jpxm.PackageId, 0, type);
		else Activity_NormalProject_Introduction.launche2(ctx, jpxm.PackageId, 0, type);
	}
}