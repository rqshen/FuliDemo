package com.bcb.presentation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.loan.LoanListRecordsBean;

import java.util.List;

/**
 * Created by cain on 16/1/13.
 */
public class LoanListAdapter extends BaseAdapter {
    private Context context;
    private List<LoanListRecordsBean> data;

    public LoanListAdapter(Context context, List<LoanListRecordsBean> data) {
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
            view = View.inflate(context, R.layout.item_loanlist, null);
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
        viewHolder.loanType = (TextView) view.findViewById(R.id.loan_type);
        viewHolder.loanAmount = (TextView) view.findViewById(R.id.loan_amount);
        viewHolder.loanDate = (TextView) view.findViewById(R.id.loan_date);
        viewHolder.loanStatus = (TextView) view.findViewById(R.id.loan_status);
    }

    //设置ViewHolder的数据
    private void setDataWithViewHolder(ViewHolder viewHolder, int pos) {
        //借款类型
        viewHolder.loanType.setText(data.get(pos).LoanType);
        //借款金额
        viewHolder.loanAmount.setText(String.format("%.2f", data.get(pos).Amount) + "元");
        //借款日期
        viewHolder.loanDate.setText(data.get(pos).LoanDate);
        //借款状态
        viewHolder.loanStatus.setText(data.get(pos).Status);
    }

    class ViewHolder {
        TextView loanType; //借款类型（标题）
        TextView loanAmount; //借款金额
        TextView loanDate;  //借款日期
        TextView loanStatus; //借款状态
    }
}
