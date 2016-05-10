package com.bcb.presentation.view.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.presentation.adapter.ProductAdapter;
import com.bcb.common.app.App;
import com.bcb.R;
import com.bcb.data.bean.ProductListBean;
import com.bcb.data.bean.ProductRecordsBean;
import com.bcb.common.net.UrlsOne;
import com.bcb.presentation.view.activity.Activity_NormalProject_Introduction;
import com.bcb.presentation.view.activity.Activity_Station_Change;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyListView;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Frag_Product extends Frag_Base implements OnClickListener {
	
	private static final String TAG = "Frag_Product";

	private MyListView mListView;
	private List<ProductRecordsBean> recordsBeans;
	private ProductAdapter mProductAdapter;
	
	private boolean canLoadMore = false;
	private int PageNow;
    private int PageSize = 10;
    //标题
	private TextView title_text, left_text;
	private ImageView dropdown;

	private String CompanyId, CompanyName = "";
	private LinearLayout null_data_layout;

	private Context ctx;

	private Receiver receiver;

    //刷新
    private PullToRefreshLayout refreshLayout;
    //加载更多
    private RelativeLayout loadmore_view;

    private BcbRequestQueue requestQueue;

    public Frag_Product(){
        super();
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
        requestQueue = BcbNetworkManager.newRequestQueue(ctx);
        //注册广播
        receiver = new Receiver();
        ctx.registerReceiver(receiver, new IntentFilter("com.bcb.choose.company"));
        ctx.registerReceiver(receiver, new IntentFilter("com.bcb.project.buy.success"));
        //标题
        title_text = (TextView) view.findViewById(R.id.title_text);
        title_text.setText("产品列表");
        //判断设备是否Android4.4以上，如果是，则表示使用了浸入式状态栏，需要设置状态栏的位置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((LinearLayout) view.findViewById(R.id.layout_topbar)).setVisibility(View.VISIBLE);
        }
        //切换公司
        left_text = (TextView) view.findViewById(R.id.left_text);
        left_text.setText("全部公司");
        left_text.setOnClickListener(this);
        dropdown = (ImageView) view.findViewById(R.id.dropdown);
        dropdown.setVisibility(View.VISIBLE);
        dropdown.setOnClickListener(this);

        CompanyId = "";
        PageNow = 1;
        null_data_layout = (LinearLayout) view.findViewById(R.id.null_data_layout);

        //产品列表数据
        recordsBeans = new ArrayList<ProductRecordsBean>();
        mProductAdapter = new ProductAdapter(ctx, recordsBeans);
        mListView = (MyListView) view.findViewById(R.id.listview_data_layout);
        mListView.setOnItemClickListener(new onClickViewProduct());
        mListView.setAdapter(mProductAdapter);
        //刷新
        loadmore_view = (RelativeLayout)view.findViewById(R.id.loadmore_view);
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
                    ToastUtil.alert(ctx, "网络异常，请稍后再试");
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
                    ToastUtil.alert(ctx, "网络异常，请稍后再试");
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
        });
        refreshLayout.autoRefresh();
    }

    public void loadData() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("CompanyId", CompanyId);
            obj.put("PageNow", PageNow);
            obj.put("PageSize", PageSize);
			obj.put("Platform", 2);
            LogUtil.d("CompanyId", CompanyId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MainpageProduct, obj, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //如果存在返回数据时
                    if(PackageUtil.getRequestStatus(response, ctx)) {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        //获取数据，要判断数据是否存在
                        ProductListBean productListBean = null;
                        //判断JSON对象是否为空
                        if (obj != null) {
                            productListBean = App.mGson.fromJson(obj.toString(), ProductListBean.class);
                        }
                        //存在产品列表
                        if (null != productListBean.Records && productListBean.Records.size() > 0) {
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
                        canLoadMore = false;
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
                }
            }
        });
        jsonRequest.setTag(BcbRequestTag.MainProductTag);
        requestQueue.add(jsonRequest);
	}

    class onClickViewProduct implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3) {
            if (recordsBeans.get(position).getStatus() == 10) {
                return;
            }
            if (recordsBeans.get(position).getStatus() == 20) {
                UmengUtil.eventById(ctx, R.string.list_bid_avi);
            } else {
                UmengUtil.eventById(ctx, R.string.list_bid_unavi);
            }
            Activity_NormalProject_Introduction.launche(ctx,
                    recordsBeans.get(position).getPackageId(),
                    recordsBeans.get(position).getName(),
                    recordsBeans.get(position).getCouponType());
        }
    }

    private void setupListViewVisible(boolean status) {
        if (status) {
            null_data_layout.setVisibility(View.GONE);
        } else {
            null_data_layout.setVisibility(View.VISIBLE);
        }
    }

	@Override
	public void onClick(View v) {
        switch (v.getId()) {
			case R.id.dropdown:
			case R.id.left_text:
                UmengUtil.eventById(ctx, R.string.list_select);
				Activity_Station_Change.launche(ctx, left_text.getText().toString());
				break;
	    }
	}

	class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.bcb.project.buy.success")) {
                refreshLayout.autoRefresh();
            } else if (intent.getAction().equals("com.bcb.choose.company")) {
                CompanyId = intent.getStringExtra("CompanyId");
                CompanyName = intent.getStringExtra("CompanyName");
                left_text.setText(CompanyName.isEmpty() ? "全部公司" : CompanyName);
                App.saveUserInfo.setCurrentCompanyId(CompanyId);
                App.saveUserInfo.setCurrentCompanyName(CompanyName);
                refreshLayout.autoRefresh();
            }
		}
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
		ctx.unregisterReceiver(receiver);
	}

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


}