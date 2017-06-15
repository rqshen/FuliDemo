package com.bcb.module.myinfo.balance.trading.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.MoneyFlowingWaterRecordsBean;

import java.util.List;

public class MoneyFlowingWaterAdapter extends BaseAdapter {

    private Context ctx;
    private List<MoneyFlowingWaterRecordsBean> data;

    public MoneyFlowingWaterAdapter(Context ctx, List<MoneyFlowingWaterRecordsBean> data) {
        if (data != null) {
            this.ctx = ctx;
            this.data = data;
        }
    }

    @Override
    public int getCount() {
        return null == this.data ? 0 : this.data.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null == this.data ? null : this.data.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
        ViewHolder mViewHolder;
        if (null == view) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_money_flowing_water, null);
            mViewHolder = new ViewHolder();
            mViewHolder.layout_date = (LinearLayout) view.findViewById(R.id.layout_date);
            mViewHolder.value_date = (TextView) view.findViewById(R.id.value_date);
            mViewHolder.value_amount = (TextView) view.findViewById(R.id.value_amount);
            mViewHolder.value_status = (TextView) view.findViewById(R.id.value_status);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        //设置时间
        mViewHolder.value_date.setText(data.get(pos).getTime());
        //如果时间相同则隐藏掉时间，否则显示时间
        if (pos > 0 && data.get(pos).getTime().equals(data.get(pos - 1).getTime())) {
            mViewHolder.layout_date.setVisibility(View.GONE);
        } else {
            mViewHolder.layout_date.setVisibility(View.VISIBLE);
        }

        //收入支出是不同的样式，收入支付类型（1收入0支出）
        if (data.get(pos).getType() == 0) {
            mViewHolder.value_amount.setTextColor(Color.argb(255, 0, 187, 9));
            mViewHolder.value_amount.setText("-" + String.format("%.2f", data.get(pos).getAmount()) + "元");
        } else {
            mViewHolder.value_amount.setTextColor(Color.argb(255, 219, 56, 56));
            mViewHolder.value_amount.setText("+" + String.format("%.2f", data.get(pos).getAmount()) + "元");
        }

        //状态描述
        mViewHolder.value_status.setText(data.get(pos).getStatus());
        return view;
    }


    class ViewHolder {
        LinearLayout layout_date;   //时间分组
        TextView value_date;    //时间
        TextView value_amount;  //值
        TextView value_status;  //状态描述
    }

}
