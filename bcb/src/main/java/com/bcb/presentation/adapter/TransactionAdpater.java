package com.bcb.presentation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.TransactionBean;
import com.bcb.data.util.LogUtil;

import java.util.List;

/**
 * Created by cain on 15/12/28.
 */
public class TransactionAdpater extends BaseAdapter {
    private Context ctx;
    private List<TransactionBean> data;

    public TransactionAdpater(Context ctx, List<TransactionBean> data) {
        this.ctx = ctx;
        this.data = data;
    }

    @Override
    public int getCount() {
        LogUtil.d("this.data.size()", "" + this.data.size());
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
        ViewHolder viewHolder;
        if (null == view) {
            view = View.inflate(ctx, R.layout.item_transaction, null);
            viewHolder = new ViewHolder();
            viewHolder.time = (TextView) view.findViewById(R.id.transaction_time);
            viewHolder.amount = (TextView) view.findViewById(R.id.transaction_amount);
            viewHolder.status = (TextView) view.findViewById(R.id.transaction_status);
            viewHolder.description = (TextView) view.findViewById(R.id.transaction_description);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //时间
        viewHolder.time.setText(data.get(pos).getTime());

        //设计金额
        if (data.get(pos).getType() == 0) {
            viewHolder.amount.setTextColor(ctx.getResources().getColor(R.color.txt_green));
            viewHolder.amount.setText("-" + data.get(pos).getAmount() + "元");
        } else {
            viewHolder.amount.setTextColor(ctx.getResources().getColor(R.color.red));
            viewHolder.amount.setText("+" + data.get(pos).getAmount() + "元");
        }

        //交易状态
        //判断是否为空，包括 null、"null"、""
        if (data.get(pos).getDescn() != null
                && !data.get(pos).getCategoryName().equalsIgnoreCase("null")
                && !data.get(pos).getCategoryName().equalsIgnoreCase("")) {
            viewHolder.status.setText(data.get(pos).getCategoryName());
        } else {
            viewHolder.status.setText("");
        }

        //描述
        //判断是否为空，包括 null、"null"、""
        if (data.get(pos).getDescn() != null
                && !data.get(pos).getDescn().equalsIgnoreCase("null")
                && !data.get(pos).getDescn().equalsIgnoreCase("")) {
            viewHolder.description.setText(data.get(pos).getDescn());
        } else {
            viewHolder.description.setText("");
        }

        return view;
    }

    class ViewHolder {
        TextView time;
        TextView amount;
        TextView status;
        TextView description;
    }
}
