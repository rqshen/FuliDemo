package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.Project_Investment_Details_Bean;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.MyListView;

import java.util.List;

/**
 * 回款计划
 */
public class A_TZ_Cheques extends Activity_Base {

    private MyListView mListView;
    private List<Project_Investment_Details_Bean.Plar> mList;
    private MyBaseAdapter mMyBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(A_TZ_Cheques.this);
        setBaseContentView(R.layout.activity_tz__cheques);
        Intent intent = getIntent();
        if (intent != null && intent.getSerializableExtra("data") != null) {
            mList = ((Project_Investment_Details_Bean) intent.getSerializableExtra("data")).getRepaymentPlan();
            if (mList != null && mList.size() > 0) {
                mMyBaseAdapter = new MyBaseAdapter();
                mListView = (MyListView) findViewById(R.id.lv);
                mListView.setAdapter(mMyBaseAdapter);
            }
        }
        setLeftTitleVisible(true);
        setTitleValue("回款计划");
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
                convertView = LayoutInflater.from(A_TZ_Cheques.this).inflate(R.layout.item_tz, null);
                mViewHolder = new ViewHolder();
                mViewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                mViewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                mViewHolder.tv_monery = (TextView) convertView.findViewById(R.id.tv_monery);
                convertView.setTag(mViewHolder);
            }
            if (mList != null) {
                Project_Investment_Details_Bean.Plar bean = mList.get(position);
                mViewHolder.tv_name.setText("第" + bean.Period + "个月");
                mViewHolder.tv_time.setText(String.format("%.2f", bean.Principal));
                mViewHolder.tv_monery.setText(String.format("%.2f", bean.Inverest));
            }
            return convertView;
        }
    }

    class ViewHolder {
        public TextView tv_name;
        public TextView tv_time;
        public TextView tv_monery;
    }
}