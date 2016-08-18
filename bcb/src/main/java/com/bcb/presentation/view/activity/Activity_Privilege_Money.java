package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.PrivilegeMoneyBasic;
import com.bcb.data.bean.PrivilegeMoneyDto;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.MyListView;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.adapter.PrivilegeMoneyAdapter;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 2016/7/7.
 *
 * @desc 特权本金
 */
public class Activity_Privilege_Money extends Activity_Base implements AdapterView.OnItemClickListener, PrivilegeMoneyAdapter.IloadAfterRegeist {
    private BcbRequestQueue requestQueue;
    private List<PrivilegeMoneyDto> datas;
    private MyListView mListView;
    private PrivilegeMoneyAdapter myAdapter;

    private Context ctx;
    private LinearLayout null_data_layout;
    private boolean canLoadmore = false;//就一条数据，不需要加载更多
    private PullToRefreshLayout refreshLayout;
    private RelativeLayout loadmore_view;
    TextView tv_shouyi_all, tv_benjin, tv_shouyi, total_privilege_money;
    ImageView iv_about;

    public static void launch(Context context) {
        Intent intent = new Intent(context, Activity_Privilege_Money.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_privilege_money);
        total_privilege_money = (TextView) findViewById(R.id.total_privilege_money);
        tv_shouyi_all = (TextView) findViewById(R.id.tv_shouyi_all);
        tv_benjin = (TextView) findViewById(R.id.tv_benjin);
        tv_shouyi = (TextView) findViewById(R.id.tv_shouyi);
        iv_about = (ImageView) findViewById(R.id.iv_about);
        total_privilege_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, Activity_Money_Flowing_Water.class));
//                if (App.mUserDetailInfo != null && App.mUserDetailInfo.HasOpenCustody) startActivity(new Intent(ctx, Activity_Money_Flowing_Water.class));
//                else startActivity(new Intent(ctx, Activity_Open_Account.class));
            }
        });
        iv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Browser.launche(Activity_Privilege_Money.this, "关于特权本金", UrlsTwo.AboutExpiredProjectIntroduction);
            }
        });
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager.getInstance().pushOneActivity(Activity_Privilege_Money.this);
        setTitleValue("特权本金");
        setLeftTitleVisible(true);
//        setRightTitleValue("兑换", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(Activity_Privilege_Money.this, "兑换", Toast.LENGTH_SHORT).show();
//            }
//        });

        requestQueue = App.getInstance().getRequestQueue();
        ctx = this;
        datas = new ArrayList<>();
        myAdapter = new PrivilegeMoneyAdapter(this, datas);
        myAdapter.setIloadAfterRegeist(this);
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

//******************************************************************************************
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserPrivilegeMoneyDto, null, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【特权金返回数据】" + response.toString());
                if (PackageUtil.getRequestStatus(response, ctx)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    //判断JSON对象是否为空
                    if (data != null) {
                        PrivilegeMoneyBasic bean = App.mGson.fromJson(data.toString(), PrivilegeMoneyBasic.class);
                        tv_shouyi_all.setText(String.format("%.2f", bean.TotalIncome));
                        tv_benjin.setText(String.format("%.2f", bean.ActivedPrincipal));
                        tv_shouyi.setText(String.format("%.2f", bean.ActivedIncome));

                        //列表
                        JSONArray jsonArray = data.optJSONArray("DataList");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject item = jsonArray.getJSONObject(i);
                                    datas.add(App.mGson.fromJson(item.toString(), PrivilegeMoneyDto.class));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                    setupListViewVisible(true);
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.UserPrivilegeMoneyDtoTag);
        requestQueue.add(jsonRequest);
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


    @Override
    public void loadAfterRegeist() {
        refreshLayout.autoRefresh();
    }
}
