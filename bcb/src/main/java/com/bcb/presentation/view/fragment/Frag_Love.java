package com.bcb.presentation.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.CouponRecordsBean;
import com.bcb.data.bean.love.LoveBean;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.ToastUtil;
import com.bcb.presentation.adapter.LoveAdapter;
import com.bcb.presentation.view.activity.Activity_Apply_Love;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 2016/6/2.
 *
 * @desc 聚爱
 */
public class Frag_Love extends Frag_Base implements View.OnClickListener{

    //标题
    private TextView title_text, right_text;
    private Context ctx;

    private LinearLayout null_data_layout;
    private boolean canLoadmore = true;
    private PullToRefreshLayout refreshLayout;
    private RelativeLayout loadmore_view;

    private int PageNow = 1;
    private int PageSize = 10;
    private List<LoveBean> loveBeens;
    private LoveAdapter loveAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_love, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.ctx = view.getContext();
        super.onViewCreated(view, savedInstanceState);
        //标题
        title_text = (TextView) view.findViewById(R.id.title_text);
        title_text.setText("聚爱");

        right_text = (TextView) view.findViewById(R.id.right_text);
        right_text.setText("我要申请");
        right_text.setOnClickListener(this);


        loveBeens = new ArrayList<>();
        loveAdapter = new LoveAdapter(ctx, loveBeens);

        //刷新
        null_data_layout = (LinearLayout) view.findViewById(R.id.null_data_layout);
        loadmore_view = (RelativeLayout) view.findViewById(R.id.loadmore_view);
        refreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.right_text:
                Activity_Apply_Love.launch(ctx);
                break;
        }
    }
}
