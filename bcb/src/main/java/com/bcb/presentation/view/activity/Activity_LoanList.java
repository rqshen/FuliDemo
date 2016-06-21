package com.bcb.presentation.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcb.R;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.adapter.LoanListAdapter;
import com.bcb.common.app.App;
import com.bcb.data.bean.loan.LoanListBean;
import com.bcb.data.bean.loan.LoanListRecordsBean;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.MyListView;
import com.bcb.data.util.ToastUtil;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cain on 15/12/24.
 */
public class Activity_LoanList extends Activity_Base {

    private static final String TAG = "Activity_LoanList";

    //刷新
    private PullToRefreshLayout refreshLayout;
    private RelativeLayout loadmore_view;
    private boolean canLoadmore = true;
    private int PageNow = 1;
    private int PageSize = 10;

    //没有数据的时候
    private LinearLayout null_data_layout;

    //列表数据和适配器
    private MyListView personListView;
    private LoanListAdapter loanListAdapter;
    private List<LoanListRecordsBean> recordsBeans;

    private BcbRequestQueue requestQueue;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_LoanList.this);
        setBaseContentView(R.layout.activity_loanlist);
        setLeftTitleVisible(true);
        setTitleValue("借款列表");
        requestQueue = App.getInstance().getRequestQueue();
        setupView();
    }

    private void setupView() {
        //没有数据的时候
        null_data_layout = (LinearLayout) findViewById(R.id.null_data_layout);
        //listView
        recordsBeans = new ArrayList<>();
        loanListAdapter = new LoanListAdapter(Activity_LoanList.this, recordsBeans);
        personListView = (MyListView) findViewById(R.id.loan_personlistview);
        personListView.setOnItemClickListener(new onClickViewLoanDetail());
        personListView.setAdapter(loanListAdapter);

        //刷新
        loadmore_view = (RelativeLayout) findViewById(R.id.loadmore_view);
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        refreshLayout.setRefreshResultView(false);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(Activity_LoanList.this)) {
                    PageNow = 1;
                    recordsBeans.clear();
                    loadLoanListData();
                    loadmore_view.setVisibility(View.VISIBLE);
                } else {
                    ToastUtil.alert(Activity_LoanList.this, "网络异常，请稍后再试");
                    refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

                if (HttpUtils.isNetworkConnected(Activity_LoanList.this)) {
                    if (canLoadmore) {
                        loadLoanListData();
                    } else {
                        loadmore_view.setVisibility(View.GONE);
                        refreshLayout.loadmoreFinish(PullToRefreshLayout.NOMORE);
                    }
                } else {
                    ToastUtil.alert(Activity_LoanList.this, "网络异常，请稍后再试");
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
        });
        refreshLayout.autoRefresh();
    }

    /******************************* 借款列表数据 **************************************************/
    private void loadLoanListData() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("PageNow", PageNow);
            jsonObject.put("PageSize", PageSize);
        } catch (Exception e){
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MyLoanListMessage, jsonObject, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    //如果存在返回数据时
                    if(PackageUtil.getRequestStatus(response, Activity_LoanList.this)) {
                        JSONObject resultObject = PackageUtil.getResultObject(response);
                        //获取数据，要判断数据是否存在
                        LoanListBean loanListBean = null;
                        //判断JSON对象是否为空
                        if (resultObject != null) {
                            loanListBean = App.mGson.fromJson(resultObject.toString(), LoanListBean.class);
                        }
                        //存在借款列表
                        if (null != loanListBean.Records && loanListBean.Records.size() > 0) {
                            canLoadmore = true;
                            PageNow++;
                            setupListViewVisible(true);
                            //线程加锁
                            synchronized (this) {
                                recordsBeans.addAll(loanListBean.Records);
                            }
                            //刷新适配器，如果存在适配器，则刷新；没有则新建并绑定适配器
                            if(null != loanListAdapter){
                                loanListAdapter.notifyDataSetChanged();
                            }
                        }
                        //不存在列表时
                        else {
                            canLoadmore = false;
                            //如果适配器已经存在则更新适配器的数据，不存在则不用管
                            if(null != loanListAdapter){
                                loanListAdapter.notifyDataSetChanged();
                            }
                            if (PageNow <= 1) {
                                setupListViewVisible(false);
                            }
                        }
                    }
                    //没有数据返回的时候，需要判断原来的数据是否存在
                    // 如果不存在，则显示没有数据的结果，如果原来存在数据，则什么都不用干
                    else {
                        canLoadmore = false;
                        //判断是否是Token过期，如果过期则跳转至登陆界面
                        if (response.getInt("status") == -5) {
                            Activity_Login.launche(Activity_LoanList.this);
                            finish();
                        }
                        if (recordsBeans == null || recordsBeans.size() <= 0) {
                            setupListViewVisible(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    setupListViewVisible(false);
                }finally {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                if (recordsBeans == null || recordsBeans.size() <= 0) {
                    setupListViewVisible(false);
                } else {
                    setupListViewVisible(true);
                }
            }
        });
        jsonRequest.setTag(BcbRequestTag.MyLoanListMessageTag);
        requestQueue.add(jsonRequest);
    }

    //加载借款列表数据，false 表示没有数据，true 表示有数据
    private void setupListViewVisible(boolean status){
        //表示存在数据时，显示listView
        if (status) {
            null_data_layout.setVisibility(View.GONE);
            personListView.setVisibility(View.VISIBLE);
        }
        //表示不存在数据时，隐藏listView
        else {
            null_data_layout.setVisibility(View.VISIBLE);
            personListView.setVisibility(View.GONE);
        }
    }

    //产品列表的Item点击事件监听器
    class onClickViewLoanDetail implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3) {
            Activity_LoanList_Detail.launche(Activity_LoanList.this, recordsBeans.get(position).UniqueId, recordsBeans.get(position).AssetCode);
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }

}