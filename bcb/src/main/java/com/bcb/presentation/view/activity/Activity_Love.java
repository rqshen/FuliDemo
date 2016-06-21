package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.love.LoveBean;
import com.bcb.data.bean.transaction.LoveListBean;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyListView;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.adapter.LoveAdapter;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

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
    private BcbRequestQueue requestQueue;

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
        setLeftTitleVisible(true);
        setRightBtnVisiable(View.VISIBLE);
        setRightBtnImg(R.drawable.ico_info, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        requestQueue = App.getInstance().getRequestQueue();
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
        JSONObject obj = new JSONObject();
        try {
            obj.put("PageNow", PageNow);
            obj.put("PageSize", PageSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.LoveProduct, obj, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //如果存在返回数据时
                    if(PackageUtil.getRequestStatus(response, ctx)) {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        //获取数据，要判断数据是否存在
                        LoveListBean loveListBean = null;
                        //判断JSON对象是否为空
                        if (obj != null) {
                            LogUtil.d("1234", obj.toString());
                            loveListBean = App.mGson.fromJson(obj.toString(), LoveListBean.class);
                        }
                        //存在产品列表
                        if (null != loveListBean && null != loveListBean.Records && loveListBean.Records.size() > 0) {
                            canLoadmore = true;
                            PageNow++;
                            setupListViewVisible(true);
                            //线程加锁
                            synchronized (this) {
                                loveBeens.addAll(loveListBean.Records);
                            }
                            //刷新适配器，如果存在适配器，则刷新；没有则新建并绑定适配器
                            if (null != loveAdapter) {
                                loveAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (null != loveAdapter) {
                                loveAdapter.notifyDataSetChanged();
                            }
                            canLoadmore = false;
                            if (PageNow <= 1) {
                                setupListViewVisible(false);
                            }
                        }
                    } else {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        if (null == obj){
                            //刷新适配器，如果存在适配器，则刷新；没有则新建并绑定适配器
                            if (null != loveBeens && null != loveAdapter) {
                                loveBeens.clear();
                                loveAdapter.notifyDataSetChanged();
                            }
                        }else{
                            canLoadmore = false;
                            if (loveBeens == null || loveBeens.size() <= 0) {
                                setupListViewVisible(false);
                            } else {
                                setupListViewVisible(true);
                            }
                        }
                    }
                }catch (Exception e) {
                    //刷新适配器，如果存在适配器，则刷新；没有则新建并绑定适配器
                    if (null != loveBeens && null != loveAdapter) {
                        loveBeens.clear();
                        loveAdapter.notifyDataSetChanged();
                    }
                    e.printStackTrace();
                }finally {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                if (loveBeens == null || loveBeens.size() <= 0) {
                    setupListViewVisible(false);
                }
            }
        });
        jsonRequest.setTag(BcbRequestTag.LoveProductTag);
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
        String url = loveBeens.get(position).getJumplink();
        String title = loveBeens.get(position).getTitle();
        String content = loveBeens.get(position).getDescription();
        if (!TextUtils.isEmpty(url)){
            Activity_Browser.launcheFromLove(ctx, "聚爱", true, title, content, url);
        }
    }
}
