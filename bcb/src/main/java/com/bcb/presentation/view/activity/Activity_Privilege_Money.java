package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcb.R;
import com.bcb.data.bean.PrivilegeMoneyDto;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.MyListView;
import com.bcb.data.util.ToastUtil;
import com.bcb.presentation.adapter.PrivilegeMoneyAdapter;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 2016/7/7.
 * @desc 特权本金
 */
public class Activity_Privilege_Money extends Activity_Base implements AdapterView.OnItemClickListener{

    private List<PrivilegeMoneyDto> datas;
    private MyListView mListView;
    private PrivilegeMoneyAdapter myAdapter;

    private Context ctx;
    private LinearLayout null_data_layout;
    private boolean canLoadmore = true;
    private PullToRefreshLayout refreshLayout;
    private RelativeLayout loadmore_view;


    public static void launch(Context context){
        Intent intent = new Intent(context, Activity_Privilege_Money.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_privilege_money);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager.getInstance().pushOneActivity(Activity_Privilege_Money.this);
        setTitleValue("特权本金");
        setLeftTitleVisible(true);
        setRightTitleValue("兑换", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ctx = this;
        datas = new ArrayList<>();
        myAdapter = new PrivilegeMoneyAdapter(this,datas);
        mListView = (MyListView) findViewById(R.id.listview_data_layout);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(myAdapter);

        //刷新
        null_data_layout = (LinearLayout) findViewById(R.id.null_data_layout);
        loadmore_view = (RelativeLayout) findViewById(R.id.loadmore_view);
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        refreshLayout.setRefreshResultView(false);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(ctx)) {
//                    PageNow = 1;
                    datas.clear();
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
//                    if (canLoadmore) {
//                        loadData();
//                    } else {
//                        loadmore_view.setVisibility(View.GONE);
//                        refreshLayout.loadmoreFinish(PullToRefreshLayout.NOMORE);
//                    }
                } else {
                    ToastUtil.alert(ctx, "网络异常，请稍后再试");
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
        });
        refreshLayout.autoRefresh();

    }

    private void loadData(){
        //测试数据
        for (int i=0;i<10;i++){
            PrivilegeMoneyDto dto = new PrivilegeMoneyDto();
            dto.setTitle("10000特权本金" + i);
            dto.setIncome(i);
            dto.setTerm("2016年7月10日过期");
            datas.add(dto);
        }
        myAdapter.notifyDataSetChanged();
        setupListViewVisible(true);
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
