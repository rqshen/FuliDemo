package com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.backpayment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.data.bean.Project_Investment_Details_Bean;
import com.bcb.util.MyActivityManager;

import java.util.List;

import static com.bcb.R.id.back_img;

/**
 * Created by ruiqin.shen
 * 类说明：稳盈宝、涨薪宝 回款计划
 */
public class BackPaymentActivity extends Activity_Base {
    private TextView have, left, tv_top;
    private ListView mListView;
    private List<Project_Investment_Details_Bean.Plar> mList;
    private BaseAdapter mMyBaseAdapter;
    private LinearLayout null_data_layout;
    int Status = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(BackPaymentActivity.this);
        setBaseContentView(R.layout.activity_tz__cheques);
        null_data_layout = (LinearLayout) findViewById(R.id.null_data_layout);
        mListView = (ListView) findViewById(R.id.lv);
        have = (TextView) findViewById(R.id.have);
        left = (TextView) findViewById(R.id.left);
        tv_top = (TextView) findViewById(R.id.tv_top);
        Intent intent = getIntent();
        if (intent != null) {
            Project_Investment_Details_Bean bean = (Project_Investment_Details_Bean) intent.getSerializableExtra("data");
            Status = getIntent().getIntExtra("Status", 1);
            if (bean != null) {
                //列表
                mList = bean.RepaymentPlan;
                if (mList != null && mList.size() > 0) {
                    null_data_layout.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    if (Status == 1) {
                        tv_top.setVisibility(View.GONE);
                        mListView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.hk_head, null));
                        mMyBaseAdapter = new MyBaseAdapter();
                    } else {
                        tv_top.setVisibility(View.VISIBLE);
                        tv_top.setText("最长可持有" + bean.RepaymentAllPeriod + "，已收益" + bean.RepaymentHadPeriod);
                        mListView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.hk_head2, null));
                        mMyBaseAdapter = new MyBaseAdapter2();
                    }
                    mListView.setAdapter(mMyBaseAdapter);
                    //已收本息，剩余本息
                    have.setText(String.format("%.2f", bean.DonePrincipalInterest));
                    left.setText(String.format("%.2f", bean.PrePrincipalInterest));
//					float v_have = 0, v_left = 0;
//					for (int i = 0; i < mList.size(); i++) {
//						if (mList.get(i).Repayed == 1) v_have += (mList.get(i).Interest+mList.get(i).Principal);
//						else v_left += (mList.get(i).Interest+mList.get(i).Principal);
//					}
//					have.setText(String.format("%.2f", v_have));//有个叫Repayed值来区分已还和待还的。
//					left.setText(String.format("%.2f", v_left));
                } else {
                    null_data_layout.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }
            }
        }
        setLeftTitleVisible(true);
        setTitleValue("回款计划");
        layout_title.setBackgroundColor(getResources().getColor(R.color.red));
        title_text.setTextColor(getResources().getColor(R.color.white));
        dropdown.setImageResource(R.drawable.return_delault);
        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        (findViewById(back_img)).setVisibility(View.GONE);
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
                convertView = LayoutInflater.from(BackPaymentActivity.this).inflate(R.layout.item_tz, null);
                mViewHolder = new ViewHolder();
                mViewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                mViewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                mViewHolder.tv_monery = (TextView) convertView.findViewById(R.id.tv_monery);
                convertView.setTag(mViewHolder);
            }
            if (mList != null) {
                Project_Investment_Details_Bean.Plar bean = mList.get(position);
                //				mViewHolder.tv_name.setText("第" + bean.Period + "期\n" + new SimpleDateFormat("yyyy-MM-dd").format
                // (bean.PayDate));
                mViewHolder.tv_name.setText(bean.Description.replaceFirst("期", "期\n"));
                mViewHolder.tv_time.setText(String.format("%.2f", bean.Principal));
                mViewHolder.tv_monery.setText(String.format("%.2f", bean.Interest));
            }
            return convertView;
        }
    }

    class MyBaseAdapter2 extends BaseAdapter {
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
                convertView = LayoutInflater.from(BackPaymentActivity.this).inflate(R.layout.item_tz2, null);
                mViewHolder = new ViewHolder();
                mViewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                mViewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                mViewHolder.tv_monery = (TextView) convertView.findViewById(R.id.tv_monery);
                mViewHolder.iv_s = (ImageView) convertView.findViewById(R.id.iv_s);
                convertView.setTag(mViewHolder);
            }
            if (mList != null) {
                Project_Investment_Details_Bean.Plar bean = mList.get(position);
                //				mViewHolder.tv_name.setText("第" + bean.Period + "期\n" + new SimpleDateFormat("yyyy-MM-dd").format
                // (bean.PayDate));
                mViewHolder.tv_name.setText(bean.Description.replaceFirst("期", "期\n"));
                mViewHolder.tv_time.setText(String.format("%.2f", bean.Principal));
                mViewHolder.tv_monery.setText(String.format("%.2f", bean.Interest));
                if (bean.Repayed == 1) mViewHolder.iv_s.setImageResource(R.drawable.hk_ok);
                else mViewHolder.iv_s.setImageResource(R.drawable.hk_no);
            }
            return convertView;
        }
    }

    class ViewHolder {
        public TextView tv_name;
        public TextView tv_time;
        public TextView tv_monery;
        public ImageView iv_s;
    }
}