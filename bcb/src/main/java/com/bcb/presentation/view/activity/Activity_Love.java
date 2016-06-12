package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.love.LoveBean;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.MyListView;
import com.bcb.data.util.ToastUtil;
import com.bcb.presentation.adapter.LoveAdapter;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 2016/6/12.
 *
 * @desc 聚爱
 */
public class Activity_Love extends Activity_Base implements AdapterView.OnItemClickListener {

    private Context ctx;
    private LinearLayout null_data_layout;
    private boolean canLoadmore = true;
    private PullToRefreshLayout refreshLayout;
    private RelativeLayout loadmore_view;

    private int PageNow = 1;
    private int PageSize = 10;
    private List<LoveBean> loveBeens;
    private LoveAdapter loveAdapter;
    private MyListView mListView;

    public static void launche(Context context) {
        Intent intent = new Intent(context, Activity_Love.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_love);
        ctx = this;
        //标题
        setTitleValue("聚爱");
        setRightTitleValue("", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
            }
        });

        loveBeens = new ArrayList<>();
        loveAdapter = new LoveAdapter(ctx, loveBeens);
        mListView = (MyListView) findViewById(R.id.listview_data_layout);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(loveAdapter);

        //刷新
        null_data_layout = (LinearLayout) findViewById(R.id.null_data_layout);
        loadmore_view = (RelativeLayout) findViewById(R.id.loadmore_view);
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        refreshLayout.setRefreshResultView(false);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(ctx)) {
                    PageNow = 1;
                    loveBeens.clear();
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
        //测试数据
        LoveBean loveBean;
        for(int i=0;i<10;i++){
            loveBean = new LoveBean();
            loveBean.setCompany("公司" + i);
            loveBean.setDesc("说明(这里最多要显示20个字符)1234567890" + i);
            loveBean.setName("测试名称" + i);
            loveBean.setProgress(50 + i);
            loveBean.setMoney(3000 + i);
            loveBean.setSupport(20 + i);
            loveBeens.add(loveBean);
        }
        setupListViewVisible(true);
        loveAdapter.notifyDataSetChanged();
        refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
        refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);

    }

    private void setupListViewVisible(boolean status) {
        // 如果存在数据的时候，显示
        if (status) {
            null_data_layout.setVisibility(View.GONE);
        } else {
            null_data_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
