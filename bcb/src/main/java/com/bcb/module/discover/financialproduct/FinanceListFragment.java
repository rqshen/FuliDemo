package com.bcb.module.discover.financialproduct;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.old.BaseFragment1;
import com.bcb.constant.ProjectListType;
import com.bcb.data.bean.MainListBean2;
import com.bcb.data.bean.WYBbean;
import com.bcb.module.discover.adapter.FinanceListAdapter;
import com.bcb.module.discover.financialproduct.dayprogram.DayProgramIntroductionActivity;
import com.bcb.module.discover.financialproduct.monthproject.MonthProgramIntroductionActivity;
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
 * 描述：产品列表
 * 作者：baicaibang
 * 时间：2017/2/27 17:05
 */
public class FinanceListFragment extends BaseFragment1 implements AdapterView.OnItemClickListener {
    private Context context;

    private MyListView lv;
    private String Status;//	【 0稳赢 散标】【1涨薪宝 原始标】 【3周盈宝 散标】
    private int PageNow = 1;
    private int PageSize = 10;

    private List<MainListBean2.JpxmBean> recordsBeans;
    private FinanceListAdapter mCouponListAdapter;

    private LinearLayout null_data_layout;
    private boolean canLoadmore = true;
    private PullToRefreshLayout refreshLayout;
    private RelativeLayout loadmore_view;

    //******************************************************************************************

    /**
     * 构造时把传入的参数带进来，
     */
    public static FinanceListFragment newInstance(String Status) {
        Bundle bundle = new Bundle();
        bundle.putString("Status", Status);
        FinanceListFragment fragment = new FinanceListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Status = bundle.getString("Status");
        }
    }
    //******************************************************************************************

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wyb, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.context = view.getContext();
        null_data_layout = (LinearLayout) view.findViewById(R.id.null_data_layout);
        recordsBeans = new ArrayList<>();
        mCouponListAdapter = new FinanceListAdapter(context, recordsBeans);
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

                if (HttpUtils.isNetworkConnected(context)) {
                    PageNow = 1;
                    recordsBeans.clear();
                    loadData();
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
                        loadData();
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

    //******************************************************************************************
    private void loadData() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("PageNow", PageNow);
            obj.put("PageSize", PageSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = UrlsOne.Month_Finance_LIST;//请求的地址

        if (Status.equals(ProjectListType.MONTH)) {//稳盈宝
            url = UrlsOne.Month_Finance_LIST;
        } else if (Status.equals(ProjectListType.DAY)) {//周盈宝
            url = UrlsOne.Day_Finance_LIST;
        }

        BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(context), new BcbRequest
                .BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (PackageUtil.getRequestStatus(response, context)) {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        WYBbean mCouponList = null;
                        if (obj != null) {
                            mCouponList = MyApplication.mGson.fromJson(obj.toString(), WYBbean.class);
                        }
                        //存在数据时
                        if (null != mCouponList && null != mCouponList.Records && mCouponList.Records.size() > 0) {
                            canLoadmore = true;
                            PageNow++;
                            setupListViewVisible(true);
                            synchronized (this) {
                                recordsBeans.addAll(mCouponList.Records);
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
                    LogUtil.i("bqt", "【_Change_InFragment1】【onResponse】" + e.toString());
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
        MainListBean2.JpxmBean jpxm = recordsBeans.get(position);
        //判断是否属于新标预告的状态，根据状态来判断是否可点击
        if (jpxm.Status == 10) {
            Toast.makeText(context, "Status == 10，不可购买", Toast.LENGTH_SHORT).show();
        }
        //0稳盈宝【月】，1涨薪宝【普通】，2周盈宝【周】
        if (jpxm.Type != null && jpxm.Type.equals(ProjectListType.MONTH)) {//稳盈宝【月】mon_package
            MonthProgramIntroductionActivity.launche(context, jpxm.PackageId);
        } else if (jpxm.Type != null && jpxm.Type.equals(ProjectListType.DAY)) {//周盈宝，周
            DayProgramIntroductionActivity.launche(context, jpxm.PackageId);
        }
    }
}