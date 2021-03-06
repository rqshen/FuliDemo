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
import com.bcb.base.old.BaseFragment1;
import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.data.bean.loan.RepaymentListBean;
import com.bcb.data.bean.loan.RepaymentRecordsBean;
import com.bcb.util.HttpUtils;
import com.bcb.util.MyListView;
import com.bcb.util.PackageUtil;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;
import com.bcb.presentation.adapter.RepaymentAdapter;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cain on 16/1/13.
 */
public class _RepaymentFragment1 extends BaseFragment1 {


    private Context context;
    private String assetCode;

    private int PageNow = 1;
    private int PageSize = 36;

    //还款列表
    private MyListView repaymentListView;

    //还款列表数据和适配器
    private List<RepaymentRecordsBean> recordsBeans;
    private RepaymentAdapter repaymentAdapter;

    private LinearLayout null_data_layout;

    private boolean canLoadmore = false;
    private PullToRefreshLayout refreshLayout;
    private RelativeLayout loadmore_view;

    private BcbRequestQueue requestQueue;

    public _RepaymentFragment1() {
        super();
    }

    @SuppressLint("ValidFragment")
    public _RepaymentFragment1(Context context, String assetCode) {
        super();
        this.context = context;
        this.assetCode = assetCode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_repayment, container, false);
    }

    //初始化页面要在这里进行，多线程情况下，在onCreateView中初始化会崩溃
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        requestQueue = MyApplication.getInstance().getRequestQueue();
        setupView(view);
        loanRepaymentData();
    }

    //初始化界面元素
    private void setupView(View view) {
        recordsBeans = new ArrayList<RepaymentRecordsBean>();
        repaymentAdapter = new RepaymentAdapter(context, recordsBeans);
        repaymentListView = (MyListView) view.findViewById(R.id.repayment_listView);
        repaymentListView.setAdapter(repaymentAdapter);

        null_data_layout = (LinearLayout) view.findViewById(R.id.null_data_layout);

        //刷新
        loadmore_view = (RelativeLayout) view.findViewById(R.id.loadmore_view);
        refreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
        refreshLayout.setRefreshResultView(false);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(context)) {
                    PageNow = 1;
                    loanRepaymentData();
                    loadmore_view.setVisibility(View.VISIBLE);
                } else {
                    ToastUtil.alert(context, "网络异常，请稍后再试");
                    refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(context)) {
                    if (canLoadmore) {
                        loanRepaymentData();
                    } else {
                        loadmore_view.setVisibility(View.GONE);
                        refreshLayout.loadmoreFinish(PullToRefreshLayout.NOMORE);
                    }
                } else {
                    ToastUtil.alert(context, "网络异常，请稍后再试");
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
        });
        refreshLayout.autoRefresh();
    }

    //获取借款详情数据
    private void loanRepaymentData() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("AssetCode", assetCode);
            jsonObject.put("PageNow", PageNow);
            jsonObject.put("PageSize", PageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MyLoanRepaymentMessage, jsonObject, TokenUtil.getEncodeToken(context), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    //判断状态是否存在
                    if (PackageUtil.getRequestStatus(response, context)) {
                        JSONObject jsonObject = PackageUtil.getResultObject(response);
                        RepaymentListBean listBean = null;
                        if (jsonObject != null) {
                            listBean = MyApplication.mGson.fromJson(jsonObject.toString(), RepaymentListBean.class);
//                            Log.d("1234", "PageNow = " + PageNow + " listBean = " + listBean.toString());
                        }
                        //存在还款记录时
                        if (listBean.Records != null && listBean.Records.size() > 0) {
                            canLoadmore = true;
                            if (1 == PageNow){
                                recordsBeans.clear();
                            }
                            PageNow++;
                            setupListViewVisible(true);
                            if ((PageNow - 1) == listBean.PageNow){
                                synchronized (this) {
                                    recordsBeans.addAll(listBean.Records);
                                }
                                if (repaymentAdapter != null) {
                                    repaymentAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            canLoadmore = false;
                            if(null != repaymentAdapter){
                                repaymentAdapter.notifyDataSetChanged();
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
        jsonRequest.setTag(BcbRequestTag.MyLoanRepaymentMessageTag);
        requestQueue.add(jsonRequest);
    }

    private void setupListViewVisible(boolean status) {
        if (status) {
            repaymentListView.setVisibility(View.VISIBLE);
            null_data_layout.setVisibility(View.GONE);
        }
        //表示不存在数据时，隐藏listView
        else {
            repaymentListView.setVisibility(View.GONE);
            null_data_layout.setVisibility(View.VISIBLE);
        }
    }
}