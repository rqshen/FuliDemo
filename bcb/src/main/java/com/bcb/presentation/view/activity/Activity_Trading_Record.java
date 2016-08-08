package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.TradingRecordListBean;
import com.bcb.data.bean.TradingRecordRecordsBean;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.MyListView;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.adapter.TradingRecordAdapter;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 投资记录
 */
public class Activity_Trading_Record extends Activity_Base {

    private static final String TAG = "Activity_Trading_Record";

    //累计总额
    private TextView TotalAmount;
    //累计收益
    private TextView income;

    private MyListView mTradingRecordListView;

    private int PageNow = 1;
    private int PageSize = 10;

    private List<TradingRecordRecordsBean> recordsBeans;
    private TradingRecordAdapter mTradingRecordAdapter;

    private LinearLayout null_data_layout;

    private boolean canLoadmore = true;
    private PullToRefreshLayout refreshLayout;
    private RelativeLayout loadmore_view;

    private BcbRequestQueue requestQueue;

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
        requestQueue = App.getInstance().getRequestQueue();
        init();
    }

    private void init() {
        TotalAmount = (TextView) findViewById(R.id.TotalAmount);
        income = (TextView) findViewById(R.id.income);
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
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.TradingRecord, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【Activity_Trading_Record】【onResponse】投资列表" + response.toString());

                try {
                    if (PackageUtil.getRequestStatus(response, Activity_Trading_Record.this)) {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        //累计交易金额
                        double totlaAmount = obj.getDouble("TotalAmount");
                        String amount = String.format("%.2f", totlaAmount);
                        if (!TextUtils.isEmpty(amount)) {
                            TotalAmount.setText(amount);
                        }
                        //累计收益金额
                        double totalInterestAmount = obj.getDouble("TotalInterestAmount");
                        String totalIncome = String.format("%.2f", totalInterestAmount);
                        if (!TextUtils.isEmpty(totalIncome)) {
                            income.setText(totalIncome);
                        }
                        TradingRecordListBean mTradingRecordList = App.mGson.fromJson(obj.getJSONObject("InvetDetail").toString(), TradingRecordListBean.class);
                        //判断是否存在交易记录
                        if (null != mTradingRecordList && null != mTradingRecordList.Records && mTradingRecordList.Records.size() > 0) {
                            canLoadmore = true;
                            PageNow++;
                            setupListViewVisible(true);
                            synchronized (this) {
                                recordsBeans.addAll(mTradingRecordList.Records);
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
        jsonRequest.setTag(BcbRequestTag.TradeRecordTag);
        requestQueue.add(jsonRequest);
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
            Activity_Project_Investment_Details.launche(Activity_Trading_Record.this, recordsBeans.get(position).getOrderNo() + "");
        }
    }


}