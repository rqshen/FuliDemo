package com.bcb.presentation.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.CouponListBean;
import com.bcb.data.bean.CouponRecordsBean;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyListView;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.adapter.CouponListAdapter;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Frag_ExpiredCoupon extends Frag_Base {

	private static final String TAG = "Frag_Expired";

    private Context ctx;

	private MyListView mCouponListView;
	
	private int PageNow = 1; 
	private int PageSize = 10;

	private List<CouponRecordsBean> recordsBeans;
	private CouponListAdapter mCouponListAdapter;
	
	private LinearLayout null_data_layout;

    private boolean canLoadmore = true;
    private PullToRefreshLayout refreshLayout;
    private RelativeLayout loadmore_view;

    private BcbRequestQueue requestQueue;

    public Frag_ExpiredCoupon(){
        super();
    }

    @SuppressLint("ValidFragment")
	public Frag_ExpiredCoupon(Context ctx) {
		super();
        this.ctx= ctx;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_coupon_expried, container, false);
	}


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.ctx = view.getContext();
        requestQueue = App.getInstance().getRequestQueue();
        null_data_layout = (LinearLayout) view.findViewById(R.id.null_data_layout);
        //产品列表数据
        recordsBeans = new ArrayList<>();
        mCouponListAdapter = new CouponListAdapter(ctx, recordsBeans, -3);
        mCouponListView = (MyListView) view.findViewById(R.id.listview_data_layout);
        mCouponListView.setAdapter(mCouponListAdapter);

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
	
    private void loadData() {
    	JSONObject obj = new JSONObject();
		try {
			obj.put("PageNow", PageNow);
			obj.put("PageSize", PageSize);	
			obj.put("Status", 40);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Select_Coupon, obj, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【Frag_UnusedCoupon】【onResponse】已过期的优惠券" + response.toString());
                try {
                    boolean flag = PackageUtil.getRequestStatus(response, ctx);
                    if(flag){
                        JSONObject obj = PackageUtil.getResultObject(response);
                        CouponListBean mCouponList = null;
                        if (obj != null) {
                            mCouponList = App.mGson.fromJson(obj.toString(), CouponListBean.class);
                        }
                        //如果数据存在
                        if (null != mCouponList && null != mCouponList.Records && mCouponList.Records.size() > 0) {
                            canLoadmore = true;
                            PageNow++;
                            setupListViewVisible(true);
                            synchronized (this) {
                                recordsBeans.addAll(mCouponList.Records);
                            }
                            //刷新适配器，如果适配器不存在，则创建并绑定适配器
                            if (null != mCouponListAdapter) {
                                mCouponListAdapter.notifyDataSetChanged();
                            }

                        } else {
                            canLoadmore = false;
                            if(null != mCouponListAdapter){
                                mCouponListAdapter.notifyDataSetChanged();
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
                } else {
                    setupListViewVisible(true);
                }
            }
        });
        jsonRequest.setTag(BcbRequestTag.BCB_SELECT_COUPON_REQUEST);
        requestQueue.add(jsonRequest);

	}


    //加载优惠券，true 表示有数据，false 表示没数据
    private void setupListViewVisible(boolean state) {
        if (state) {
            null_data_layout.setVisibility(View.GONE);
            mCouponListView.setVisibility(View.VISIBLE);
        } else {
            null_data_layout.setVisibility(View.VISIBLE);
            mCouponListView.setVisibility(View.GONE);
        }
    }
}
