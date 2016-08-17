package com.bcb.presentation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.loan.RepaymentRecordsBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by cain on 16/1/15.
 */
public class RepaymentAdapter extends BaseAdapter {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private Context context;
    private List<RepaymentRecordsBean> data;

    //默认构造函数
    public RepaymentAdapter(Context context, List<RepaymentRecordsBean> data) {
        if (data != null) {
            this.context = context;
            this.data = data;
        }
    }

    @Override
    public int getCount() {
        return null == this.data ? 0 : this.data.size();
    }

    @Override
    public Object getItem(int i) {
        return null == this.data ? null : this.data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (null == view) {
            view = View.inflate(context, R.layout.item_repayment_list, null);
            viewHolder = new ViewHolder();
            setupViewholder(viewHolder, view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        //设置ViewHolder的数据
        setDataWithViewHolder(viewHolder, i);
        return view;
    }

    //初始化ViewHolder
    private void setupViewholder(ViewHolder viewHolder, View view) {
        viewHolder.repaymentDate = (TextView) view.findViewById(R.id.repayment_date);
        viewHolder.repaymentAmount = (TextView) view.findViewById(R.id.repayment_amount);
        viewHolder.repaymentPenalty = (TextView) view.findViewById(R.id.repayment_penalty);
        viewHolder.repaymentStatus = (TextView) view.findViewById(R.id.repayment_status);
    }

    //设置ViewHolder 数据
    private void setDataWithViewHolder(ViewHolder viewHolder, int pos) {
        //设置还款日
        try {
            viewHolder.repaymentDate.setText(format.format(format.parse(data.get(pos).PayDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //设置还款金额
        viewHolder.repaymentAmount.setText(String.format("%.2f", data.get(pos).Amount));
        //设置罚息
        if (data.get(pos).LatePenalty > 0) {
            viewHolder.repaymentPenalty.setVisibility(View.VISIBLE);
            viewHolder.repaymentPenalty.setText("(含" + String.format("%.2f", data.get(pos).LatePenalty) + "元罚息)");
        } else {
            viewHolder.repaymentPenalty.setVisibility(View.GONE);
        }
        //设置还款状态
        viewHolder.repaymentStatus.setText(data.get(pos).Status);
    }

    class ViewHolder {
        TextView repaymentDate;     //还款日
        TextView repaymentAmount;   //还款金额
        TextView repaymentPenalty;  //罚息
        TextView repaymentStatus;   //还款状态
    }
}
