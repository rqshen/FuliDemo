package com.bcb.presentation.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcb.R;
import com.bcb.base.old.BaseFragment1;
import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.data.bean.CouponListBean;
import com.bcb.data.bean.CouponRecordsBean;
import com.bcb.util.HttpUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.MyListView;
import com.bcb.util.PackageUtil;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;
import com.bcb.presentation.adapter.CouponListAdapter;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 未使用的优惠券
 */
public class _UnusedCouponFragment1 extends BaseFragment1 {

	private static final String TAG = "_UnusedCouponFragment1";

	private Context ctx;

	private MyListView mCouponListView;
	
	private int PageNow = 1; 
	private int PageSize = 10;

	private List<CouponRecordsBean> recordsBeans;
	private CouponListAdapter mCouponListAdapter;
	
	private LinearLayout null_data_layout;

	private Receiver mReceiver;

	private boolean canLoadmore = true;
	private PullToRefreshLayout refreshLayout;
	private RelativeLayout loadmore_view;

    private BcbRequestQueue requestQueue;

    public _UnusedCouponFragment1(){
        super();
    }

    @SuppressLint("ValidFragment")
	public _UnusedCouponFragment1(Context ctx) {
		super();
        this.ctx= ctx;	
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_coupon_unused, container, false);
	}


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.ctx = view.getContext();
        requestQueue = MyApplication.getInstance().getRequestQueue();
        IntentFilter intentFilter = new IntentFilter("com.bcb.update.couponui");
        mReceiver = new Receiver();
        ctx.registerReceiver(mReceiver, intentFilter);


        null_data_layout = (LinearLayout) view.findViewById(R.id.null_data_layout);

        recordsBeans = new ArrayList<>();
		mCouponListAdapter = new CouponListAdapter(ctx, recordsBeans, -1);
        mCouponListView = (MyListView) view.findViewById(R.id.listview_data_layout);
//        mCouponListView.setOnItemClickListener(new onClickViewCoupon());
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

    //广播
	class Receiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("com.bcb.update.couponui")) {
				PageNow = 1;
                //先清空原有的数据
                recordsBeans.clear();
				loadData();	
			}		
		}
	}
		
	@Override
	public void onDestroy() {
		super.onDestroy();
		ctx.unregisterReceiver(mReceiver);
	}


    private void loadData() {
	    /*1	未使用
	    10	冻结中
	    20	已使用
	    30	已回收
	    40	已过期*/
    	JSONObject obj = new JSONObject();
		try {
			obj.put("PageNow", PageNow);
			obj.put("PageSize", PageSize);	
			obj.put("Status", 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Select_Coupon, obj, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
	            LogUtil.i("bqt", "【_UnusedCouponFragment1】【onResponse】未使用的优惠券" + response.toString());

                try {
                    if(PackageUtil.getRequestStatus(response, ctx)){
                        JSONObject obj = PackageUtil.getResultObject(response);
                        CouponListBean mCouponList = null;
                        if (obj != null) {
                            mCouponList = MyApplication.mGson.fromJson(obj.toString(), CouponListBean.class);
                        }
                        //存在数据时
                        if (null != mCouponList && null != mCouponList.Records && mCouponList.Records.size() > 0) {
                            canLoadmore = true;
                            PageNow++;
                            setupListViewVisible(true);
                            synchronized (this){
                                recordsBeans.addAll(mCouponList.Records);
                                Collections.sort(recordsBeans);
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
	
	class onClickViewCoupon implements OnItemClickListener {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            LogUtil.d("1234", "type = " + recordsBeans.get(position).getCouponType());
            Intent intent = new Intent();
			intent.putExtra("selectCoupon", true);
			((Activity)ctx).setResult(1, intent);
			((Activity)ctx).finish();
		}
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
