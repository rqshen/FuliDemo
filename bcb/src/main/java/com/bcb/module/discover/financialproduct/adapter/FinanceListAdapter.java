package com.bcb.module.discover.financialproduct.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.data.bean.MainListBean2;
import com.bcb.presentation.view.custom.RoundProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ruiqin.shen
 * 类说明：理财列表适配器
 */
public class FinanceListAdapter extends BaseAdapter {

    private Context context;
    private List<MainListBean2.JpxmBean> data;

    //默认标的构造方法
    public FinanceListAdapter(Context context, List<MainListBean2.JpxmBean> data) {
        this.context = context;
        this.data = data;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();
        //设置ViewHolder的数据
        setDataWithViewHolder(viewHolder, position);
        return convertView;
    }

    //设置ViewHolder数据
    private void setDataWithViewHolder(ViewHolder viewHolder, int pos) {
        MainListBean2.JpxmBean bean = data.get(pos);
        viewHolder.tvRate.setText(String.valueOf(bean.Rate));
        //福袋利率
        String welfareRate = TextUtils.isEmpty(MyApplication.getInstance().getWelfare()) ? "%" : "%+" + MyApplication.getInstance().getWelfare() + "%";
        viewHolder.tvRateAdd.setText(welfareRate);
        viewHolder.tvTime.setText(String.valueOf(bean.Duration));
        //天标月标
        switch (bean.DurationExchangeType) {
            case 1:
                viewHolder.tvDw.setText("天");
                break;
            default:
                viewHolder.tvDw.setText("个月");
                break;
        }
        viewHolder.tvJe.setText("融资金额" + bean.Amount);

        //百分比
        if (bean.Balance <= 0) viewHolder.pb.setProgress(100);// || bean.ProcessPercent >= 100.0f
        else viewHolder.pb.setProgress((int) bean.ProcessPercent);
    }

    static class ViewHolder {
        @BindView(R.id.tv_rate)
        TextView tvRate;
        @BindView(R.id.tv_rate_add)
        TextView tvRateAdd;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_dw)
        TextView tvDw;
        @BindView(R.id.tv_je)
        TextView tvJe;
        @BindView(R.id.pb)
        RoundProgressBar pb;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

