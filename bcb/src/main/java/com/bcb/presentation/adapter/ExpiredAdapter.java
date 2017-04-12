package com.bcb.presentation.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.MyApplication;
import com.bcb.data.bean.ExpiredRecordsBean;

import java.util.List;


/**
 * Created by cain on 16/1/4.
 */
public class ExpiredAdapter extends BaseAdapter {

    private Context context;
    private List<ExpiredRecordsBean> data;
    //构造函数
    public ExpiredAdapter(Context context, List<ExpiredRecordsBean> data) {
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
        if (view == null) {
            view = View.inflate(context, R.layout.item_product, null);
            viewHolder = new ViewHolder();
            setupViewholder(viewHolder, view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        //设置ViewHolder数据
        setDataWithViewHolder(viewHolder, i);

        return view;
    }

    //初始化ViewHolder
    private void setupViewholder(ViewHolder viewHolder, View view) {
        viewHolder.name = (TextView) view.findViewById(R.id.name);
        viewHolder.rate = (TextView) view.findViewById(R.id.rate);
        viewHolder.fukubukuro_rate = (TextView) view.findViewById(R.id.fukubukuro_rate);
        viewHolder.duration = (TextView) view.findViewById(R.id.duration);
        viewHolder.duration_description = (TextView) view.findViewById(R.id.duration_description);
        viewHolder.amountBalance = (TextView) view.findViewById(R.id.amountBalance);
        viewHolder.valuePercent = (TextView) view.findViewById(R.id.value_percent);
        viewHolder.progressPercent = (ProgressBar) view.findViewById(R.id.progress_percent);
        viewHolder.progressPercent.setMax(100);
    }

    //设置ViewHolder数据
    private void setDataWithViewHolder(ViewHolder viewHolder, int pos) {

        //项目名称
        viewHolder.name.setText(data.get(pos).getName());

        //借款利率
        viewHolder.rate.setText(data.get(pos).getRate() + data.get(pos).getRewardRate() + "");

        //福袋利率
        String welfareRate = TextUtils.isEmpty(MyApplication.getInstance().getWelfare()) ? "" : "+" + MyApplication.getInstance().getWelfare() + "%";
        viewHolder.fukubukuro_rate.setText(welfareRate);

        //借款期限
        viewHolder.duration.setText(data.get(pos).getDuration() + "");

        //天标月标
        switch (data.get(pos).getDurationExchangeType()) {
            case 1:
                viewHolder.duration_description.setText("天");
                break;
            case 2:
                viewHolder.duration_description.setText("个月");
                break;
            default:
                viewHolder.duration.setText("");
                break;
        }
        //可投金额
        viewHolder.amountBalance.setText((int) data.get(pos).getAmountBalance() + " 元");
        //百分比
        float percent = ((100 - 100 * (data.get(pos).getAmountBalance()/data.get(pos).getAmountTotal())));
        //如果标的卖完，则显示售罄字样
        if (data.get(pos).getAmountBalance() <=0) {
            viewHolder.valuePercent.setText("售罄");
        } else {
            viewHolder.valuePercent.setText(String.format("%.1f", percent) + "%");
        }
        viewHolder.progressPercent.setProgress((int) percent);
    }

    class ViewHolder {
        TextView name;
        TextView rate;
        TextView fukubukuro_rate;
        TextView duration;
        TextView duration_description;
        TextView amountBalance;
        TextView valuePercent;
        ProgressBar progressPercent;
    }
}
