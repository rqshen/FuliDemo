package com.bcb.presentation.view.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.UrlsTwo;
import com.bcb.data.bean.SlbList;
import com.bcb.utils.HttpUtils;
import com.bcb.utils.LogUtil;
import com.bcb.utils.MyActivityManager;
import com.bcb.utils.MyListView;
import com.bcb.utils.PackageUtil;
import com.bcb.utils.ToastUtil;
import com.bcb.utils.TokenUtil;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 收益记录－－就是一个可以下拉刷新、加载更多的listview
 */
public class A_Slb_List extends Activity_Base {

    private MyListView mListView;
    private List<SlbList.SlbSyBean> mList;
    private MyBaseAdapter mMyBaseAdapter;

    private int PageNow = 1;
    private int PageSize = 10;

    private LinearLayout null_data_layout;
    private boolean canLoadmore = true;
    private PullToRefreshLayout refreshLayout;
    private RelativeLayout loadmore_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(A_Slb_List.this);
        setBaseContentView(R.layout.activity_slb_list);
        setLeftTitleVisible(true);
        setTitleValue("收益记录");
        init();
    }

    private void init() {
        null_data_layout = (LinearLayout) findViewById(R.id.null_data_layout);
        mList = new ArrayList<>();
        mMyBaseAdapter = new MyBaseAdapter();
        mListView = (MyListView) findViewById(R.id.lv);
        mListView.setAdapter(mMyBaseAdapter);

        //刷新
        loadmore_view = (RelativeLayout) findViewById(R.id.loadmore_view);
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        refreshLayout.setRefreshResultView(false);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(A_Slb_List.this)) {
                    PageNow = 1;
                    mList.clear();
                    loadData();
                    loadmore_view.setVisibility(View.VISIBLE);
                } else {
                    ToastUtil.alert(A_Slb_List.this, "网络异常，请稍后再试");
                    refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(A_Slb_List.this)) {
                    if (canLoadmore) {
                        loadData();
                    } else {
                        loadmore_view.setVisibility(View.GONE);
                        refreshLayout.loadmoreFinish(PullToRefreshLayout.NOMORE);
                    }
                } else {
                    ToastUtil.alert(A_Slb_List.this, "网络异常，请稍后再试");
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
        });
        refreshLayout.autoRefresh();
    }

    private void loadData() {
        String encodeToken = TokenUtil.getEncodeToken(this);
        JSONObject obj = new JSONObject();
        try {
            obj.put("PageNow", PageNow);
            obj.put("PageSize", PageSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UrlSlbSY, obj, encodeToken, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【A_Slb_List】【onResponse】收益列表" + response.toString());
                try {
                    if (PackageUtil.getRequestStatus(response, A_Slb_List.this)) {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        SlbList mSlbList = null;
                        if (obj != null) mSlbList = MyApplication.mGson.fromJson(obj.toString(), SlbList.class);
                        //如果存在记录
                        if (null != mSlbList && null != mSlbList.Records && mSlbList.Records.size() > 0) {
                            canLoadmore = true;
                            PageNow++;
                            setupListViewVisible(true);
                            //添加新的数据到集合中
                            synchronized (this) {
                                mList.addAll(mSlbList.Records);
                            }
                            //刷新适配器
                            if (null != mMyBaseAdapter) mMyBaseAdapter.notifyDataSetChanged();
                            //不存在记录
                        } else {
                            canLoadmore = false;
                            if (null != mMyBaseAdapter) mMyBaseAdapter.notifyDataSetChanged();
                            if (PageNow <= 1) setupListViewVisible(false);
                        }
                    } else {
                        canLoadmore = false;
                        if (mList == null || mList.size() <= 0) setupListViewVisible(false);
                        else setupListViewVisible(true);
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
                if (mList == null || mList.size() <= 0) setupListViewVisible(false);
                else setupListViewVisible(true);
            }
        });
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }


    //设置是否显示列表数据
    private void setupListViewVisible(boolean visible) {
        if (visible) {
            null_data_layout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            null_data_layout.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

    class MyBaseAdapter extends BaseAdapter {
        private ViewHolder mViewHolder;

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList == null ? null : mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView != null) mViewHolder = (ViewHolder) convertView.getTag();
            else {
                convertView = LayoutInflater.from(A_Slb_List.this).inflate(R.layout.item_slb, null);
                mViewHolder = new ViewHolder();
                mViewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                mViewHolder.tv_monery = (TextView) convertView.findViewById(R.id.tv_monery);
                convertView.setTag(mViewHolder);
            }
            if (mList != null) {
                SlbList.SlbSyBean bean = mList.get(position);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    mViewHolder.tv_time.setText(format.format(format.parse(bean.ProfitDate)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mViewHolder.tv_monery.setText("+" + bean.Profit);
            }
            return convertView;
        }
    }

    class ViewHolder {
        public TextView tv_time;
        public TextView tv_monery;
    }
}