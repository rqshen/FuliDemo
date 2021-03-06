package com.bcb.module.myinfo.balance.trading;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.data.bean.MoneyFlowingWaterListBean;
import com.bcb.data.bean.MoneyFlowingWaterRecordsBean;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.module.myinfo.balance.trading.adapter.MoneyFlowingWaterAdapter;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;
import com.bcb.util.HttpUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.MyActivityManager;
import com.bcb.util.MyListView;
import com.bcb.util.PackageUtil;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;
import com.bcb.util.UmengUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruiqin.shen
 * 类说明：收支明细
 */
public class TradingRecordActivity extends Activity_Base {

    private MyListView mFlowingWaterListView;

    private int PageNow = 1;
    private int PageSize = 10;

    private List<MoneyFlowingWaterRecordsBean> recordsBeans;
    private MoneyFlowingWaterAdapter mMoneyFlowingWaterAdapter;

    private LinearLayout null_data_layout;

    private boolean canLoadmore = true;
    private PullToRefreshLayout refreshLayout;
    private RelativeLayout loadmore_view;
    private BcbRequestQueue requestQueue;

    public static void launche(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, TradingRecordActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(TradingRecordActivity.this);
        setBaseContentView(R.layout.activity_money_flowing_water);
        setLeftTitleVisible(true);
        setTitleValue("交易明细");
        requestQueue = MyApplication.getInstance().getRequestQueue();
        init();
        UmengUtil.eventById(this, R.string.sel_zjmx);
    }

    private void init() {

        null_data_layout = (LinearLayout) findViewById(R.id.null_data_layout);

        recordsBeans = new ArrayList<>();
        mMoneyFlowingWaterAdapter = new MoneyFlowingWaterAdapter(TradingRecordActivity.this, recordsBeans);
        mFlowingWaterListView = (MyListView) findViewById(R.id.listview_data_layout);
        mFlowingWaterListView.setOnItemClickListener(new onClickViewFlowingWater());
        mFlowingWaterListView.setAdapter(mMoneyFlowingWaterAdapter);

        //刷新
        loadmore_view = (RelativeLayout) findViewById(R.id.loadmore_view);
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        refreshLayout.setRefreshResultView(false);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(TradingRecordActivity.this)) {
                    PageNow = 1;
                    recordsBeans.clear();
                    loadFlowingWaterData();
                    loadmore_view.setVisibility(View.VISIBLE);
                } else {
                    ToastUtil.alert(TradingRecordActivity.this, "网络异常，请稍后再试");
                    refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(TradingRecordActivity.this)) {
                    if (canLoadmore) {
                        loadFlowingWaterData();
                    } else {
                        loadmore_view.setVisibility(View.GONE);
                        refreshLayout.loadmoreFinish(PullToRefreshLayout.NOMORE);
                    }
                } else {
                    ToastUtil.alert(TradingRecordActivity.this, "网络异常，请稍后再试");
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
        });
        refreshLayout.autoRefresh();
    }

    private void loadFlowingWaterData() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("PageNow", PageNow);
            obj.put("PageSize", PageSize);
            obj.put("Platform", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MoneyDetail, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【TradingRecordActivity】【onResponse】产品列表" + response.toString());

                try {
                    if (PackageUtil.getRequestStatus(response, TradingRecordActivity.this)) {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        MoneyFlowingWaterListBean mMoneyFlowingWaterList = null;
                        //判断JSON对象是否为空
                        if (obj != null) {
                            mMoneyFlowingWaterList = MyApplication.mGson.fromJson(obj.toString(), MoneyFlowingWaterListBean.class);
                        }
                        //如果存在记录
                        if (null != mMoneyFlowingWaterList && null != mMoneyFlowingWaterList.Records && mMoneyFlowingWaterList.Records.size() > 0) {
                            canLoadmore = true;
                            PageNow++;
                            setupListViewVisible(true);
                            synchronized (this) {
                                recordsBeans.addAll(mMoneyFlowingWaterList.Records);
                            }
                            //刷新适配器
                            if (null != mMoneyFlowingWaterAdapter) {
                                mMoneyFlowingWaterAdapter.notifyDataSetChanged();
                            }
                        } else {
                            canLoadmore = false;
                            if (null != mMoneyFlowingWaterAdapter) {
                                mMoneyFlowingWaterAdapter.notifyDataSetChanged();
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
        jsonRequest.setTag(BcbRequestTag.TransactionTag);
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

    //流水Item点击事件
    class onClickViewFlowingWater implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
            TradingRecordDetailActivity.launche(TradingRecordActivity.this, recordsBeans.get(position).getBillId());
        }
    }


}

