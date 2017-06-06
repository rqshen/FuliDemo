package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.data.bean.TransactionBean;
import com.bcb.data.bean.TransactionListBean;
import com.bcb.util.HttpUtils;
import com.bcb.util.MyActivityManager;
import com.bcb.util.MyListView;
import com.bcb.util.PackageUtil;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;
import com.bcb.util.UmengUtil;
import com.bcb.presentation.adapter.TransactionAdpater;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** 交易明细 */
public class Activity_Transaction_Detail extends Activity_Base {

    private static final String TAG = "Activity_Transaction_Detail";

    private MyListView mFlowingWaterListView;

    private int PageNow = 1;
    private int PageSize = 10;
    //数据和适配器
    private List<TransactionBean> recordsBeans;
    private TransactionAdpater transactionAdpater;

    private LinearLayout null_data_layout;

    private boolean canLoadmore = true;
    private PullToRefreshLayout refreshLayout;
    private RelativeLayout loadmore_view;

    private BcbRequestQueue requestQueue;
    public static void launche(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, Activity_Transaction_Detail.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Transaction_Detail.this);
        setBaseContentView(R.layout.activity_transaction_detail);
        setLeftTitleVisible(true);
        setTitleValue("交易明细");
        requestQueue = MyApplication.getInstance().getRequestQueue();
        init();
        UmengUtil.eventById(this, R.string.sel_zjmx);
    }

    private void init() {
        null_data_layout = (LinearLayout) findViewById(R.id.null_data_layout);
        //数据和适配器初始化
        recordsBeans = new ArrayList<>();
        transactionAdpater = new TransactionAdpater(Activity_Transaction_Detail.this, recordsBeans);
        mFlowingWaterListView = (MyListView) findViewById(R.id.listview_data_layout);
        mFlowingWaterListView.setAdapter(transactionAdpater);

        //刷新
        loadmore_view = (RelativeLayout) findViewById(R.id.loadmore_view);
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        refreshLayout.setRefreshResultView(false);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(Activity_Transaction_Detail.this)) {
                    PageNow = 1;
                    recordsBeans.clear();
                    loadFlowingWaterData();
                    loadmore_view.setVisibility(View.VISIBLE);
                } else {
                    ToastUtil.alert(Activity_Transaction_Detail.this, "网络异常，请稍后再试");
                    refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(Activity_Transaction_Detail.this)) {
                    if (canLoadmore) {
                        loadFlowingWaterData();
                    } else {
                        loadmore_view.setVisibility(View.GONE);
                        refreshLayout.loadmoreFinish(PullToRefreshLayout.NOMORE);
                    }
                } else {
                    ToastUtil.alert(Activity_Transaction_Detail.this, "网络异常，请稍后再试");
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
        });
        refreshLayout.autoRefresh();
    }

    private void loadFlowingWaterData() {
        JSONObject data = PackageUtil.pkgMyPageList(PageNow, PageSize);
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MoneyDetail, data, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");
                    if(status == 1) {
                        TransactionListBean transactionListBean = null;
                        //判断JSON对象是否为空
                        transactionListBean = MyApplication.mGson.fromJson(response.getString("result"), TransactionListBean.class);
                        //如果存在记录
                        if (null != transactionListBean && null != transactionListBean.Records && transactionListBean.Records.size() > 0) {
                            canLoadmore = true;
                            PageNow++;
                            setupListViewVisible(true);
                            synchronized (this){
                                recordsBeans.addAll(transactionListBean.Records);
                            }
                            //刷新适配器
                            if(null != transactionAdpater){
                                transactionAdpater.notifyDataSetChanged();
                            }
                        } else {
                            canLoadmore = false;
                            //刷新适配器
                            if(null != transactionAdpater){
                                transactionAdpater.notifyDataSetChanged();
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

                } catch (JSONException e) {
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
                } else {
                    setupListViewVisible(true);
                }
            }
        });
        jsonRequest.setTag(BcbRequestTag.TranscationDetailTag);
        requestQueue.add(jsonRequest);
    }

    //设置是否显示列表数据
    private void setupListViewVisible(boolean visible) {
        if (visible) {
            null_data_layout.setVisibility(View.GONE);
            mFlowingWaterListView.setVisibility(View.VISIBLE);
        } else {
            null_data_layout.setVisibility(View.VISIBLE);
            mFlowingWaterListView.setVisibility(View.GONE);
        }
    }
}
