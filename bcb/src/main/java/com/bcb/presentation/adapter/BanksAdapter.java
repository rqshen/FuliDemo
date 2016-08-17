package com.bcb.presentation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.BanksBean;
import com.bcb.data.util.BankLogo;
import com.bcb.data.util.LogUtil;

import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/8/4 11:49
 */
public class BanksAdapter extends BaseAdapter {
    private Context ctx;
    private List<BanksBean> data;

    public BanksAdapter(Context ctx, List<BanksBean> data) {
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
            view = View.inflate(ctx, R.layout.item_bank_charge, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_log = (ImageView) view.findViewById(R.id.iv_log);
            viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tv_once = (TextView) view.findViewById(R.id.tv_once);
            viewHolder.tv_day = (TextView) view.findViewById(R.id.tv_day);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.iv_log.setImageResource(new BankLogo().getDrawableBankLogo(data.get(pos).getBankCode()));
        viewHolder.tv_name.setText(data.get(pos).getBankName());
        viewHolder.tv_once.setText(data.get(pos).getMaxSingle() + "");
        viewHolder.tv_day.setText(data.get(pos).getMaxDay() + "");
        return view;
    }

    class ViewHolder {
        ImageView iv_log;
        TextView tv_name;
        TextView tv_once;
        TextView tv_day;
    }
}