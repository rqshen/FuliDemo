package com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.old.BaseFragment1;
import com.bcb.constant.ProjectListType;
import com.bcb.data.bean.TZJLbean;
import com.bcb.module.discover.financialproduct.InvestmentFinanceActivity;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.MyFinancialStateFragment;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.adapter.MyFinancialListAdapter;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.adapter.MyFinancialListFinishAdapter;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.FinancialDetailActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.UrlsOne;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;
import com.bcb.util.HttpUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.MyListView;
import com.bcb.util.PackageUtil;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 投资理财 持有中， 已结束的 订单列表
 * 通过status和tab来区分
 * status表示稳盈宝和涨薪宝 0 稳赢  1涨薪
 * tab表示持有中和已结束  1为持有中，2为已结束
 */
public class MyFinancialListFragment extends BaseFragment1 implements AdapterView.OnItemClickListener {
    private Context ctx;

    private MyListView lv;
    private String Status;//	【 0稳赢，打包】【1涨薪宝，三标】
    private int Tab;//	1为持有中，2为已结束

    private int PageNow = 1;
    private int PageSize = 10;

    TZJLbean tzjLbean;
    private List<TZJLbean.InvetDetailBean.RecordsBean> recordsBeans;
    private MyFinancialListAdapter mCouponListAdapter;
    private MyFinancialListFinishAdapter mCouponListFinishAdapter;

    private LinearLayout null_data_layout;
    private boolean canLoadmore = true;
    private PullToRefreshLayout refreshLayout;
    private RelativeLayout loadmore_view;
    private static String EXTRA_STATUS = "status";
    private static String EXTRA_TAB = "Tab";

    //******************************************************************************************

    /**
     * 构造时把传入的参数带进来，
     */
    public static MyFinancialListFragment newInstance(String Status, int Tab) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_STATUS, Status);
        bundle.putInt(EXTRA_TAB, Tab);
        MyFinancialListFragment fragment = new MyFinancialListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Status = bundle.getString(EXTRA_STATUS);
            Tab = bundle.getInt(EXTRA_TAB);
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
        lv = (MyListView) view.findViewById(R.id.listview_data_layout);
        view.findViewById(R.id.button_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, InvestmentFinanceActivity.class));
            }
        });
        lv.setOnItemClickListener(this);

        if (Tab == 1) {//持有中
            mCouponListAdapter = new MyFinancialListAdapter(ctx, recordsBeans);
            lv.setAdapter(mCouponListAdapter);
        } else {
            mCouponListFinishAdapter = new MyFinancialListFinishAdapter(ctx, recordsBeans);
            lv.setAdapter(mCouponListFinishAdapter);
        }

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


        String url = UrlsOne.Month_MyFinancial_List;//打包，稳赢
        if (Status.equals(ProjectListType.MONTH)) {
            url = UrlsOne.Month_MyFinancial_List;//打包，稳赢
        } else if (Status == ProjectListType.DAY) {
            url = UrlsOne.Day_MyFinancial_List;
        }

        BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(ctx), new BcbRequest
                .BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (PackageUtil.getRequestStatus(response, ctx)) {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        LogUtil.e("myfinal", obj.toString());
                        tzjLbean = null;
                        if (obj != null) {
                            float tempYjsy = 0;//应计收益
                            float tempZtbj = 0;//再投本金
                            tzjLbean = MyApplication.mGson.fromJson(obj.toString(), TZJLbean.class);
                            if (Status.equals(ProjectListType.MONTH)) {
                                tempYjsy = tzjLbean.PackInterest;
                                tempZtbj = tzjLbean.PackPrincipal;
                            } else if (Status.equals(ProjectListType.DAY)) {
                                tempYjsy = tzjLbean.ChickenInterest;
                                tempZtbj = tzjLbean.ChickenPrincipal;
                            }
                            ((MyFinancialStateFragment) getParentFragment()).yjsy.setText(String.format("%.2f", tempYjsy));
                            ((MyFinancialStateFragment) getParentFragment()).ztbj.setText(String.format("%.2f", tempZtbj));
                        }

                        //存在数据时
                        if (null != tzjLbean && null != tzjLbean.InvetDetail.Records && tzjLbean.InvetDetail.Records.size() > 0) {
                            canLoadmore = true;
                            PageNow++;
                            setupListViewVisible(true);
                            synchronized (this) {
                                recordsBeans.addAll(tzjLbean.InvetDetail.Records);
                            }
                            if (Tab == 1) {//持有中
                                if (null != mCouponListAdapter) {
                                    mCouponListAdapter.notifyDataSetChanged();
                                }
                            } else {
                                if (null != mCouponListFinishAdapter) {
                                    mCouponListFinishAdapter.notifyDataSetChanged();
                                }
                            }

                        } else {
                            canLoadmore = false;
                            if (Tab == 1) {//持有中
                                if (null != mCouponListAdapter) {
                                    mCouponListAdapter.notifyDataSetChanged();
                                }
                            } else {
                                if (null != mCouponListFinishAdapter) {
                                    mCouponListFinishAdapter.notifyDataSetChanged();
                                }
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
                    LogUtil.i("bqt", "【_Change_InFragment1】【onResponse】" + e.toString());
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

    /**
     * 投资详情的点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //投资详情
        FinancialDetailActivity.launche(ctx, recordsBeans.get(position).OrderNo + "", Status);
    }
}