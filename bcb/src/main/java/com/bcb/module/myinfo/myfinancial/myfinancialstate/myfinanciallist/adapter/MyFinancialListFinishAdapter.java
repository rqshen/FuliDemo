package com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.TZJLbean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 投资理财列表的适配器
 */
public class MyFinancialListFinishAdapter extends BaseAdapter {

    private Context ctx;
    private List<TZJLbean.InvetDetailBean.RecordsBean> data;

    //默认标的构造方法
    public MyFinancialListFinishAdapter(Context ctx, List<TZJLbean.InvetDetailBean.RecordsBean> data) {
        this.ctx = ctx;
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
    public View getView(final int pos, View view, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (null == view) {
            view = LayoutInflater.from(ctx).inflate(R.layout.item_tzjl_finish, arg2, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else viewHolder = (ViewHolder) view.getTag();
        //设置ViewHolder的数据
        setDataWithViewHolder(viewHolder, pos);
        return view;
    }

    //设置ViewHolder数据
    private void setDataWithViewHolder(ViewHolder viewHolder, int pos) {
        TZJLbean.InvetDetailBean.RecordsBean bean = data.get(pos);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatD = new SimpleDateFormat("dd");
            SimpleDateFormat formatM = new SimpleDateFormat("M月");
            SimpleDateFormat formatY = new SimpleDateFormat("yyyy年");
            Date qxr_ = format.parse(bean.CreateDate);//***************************************************************************
            viewHolder.day.setText(formatD.format(qxr_));//天
            viewHolder.month.setText(formatM.format(qxr_));//月
            viewHolder.tv_top.setText(formatY.format(qxr_));//年

            if (pos > 0 && format.format(qxr_).equals(format.format(format.parse(data.get(pos - 1).CreateDate)))) {
                viewHolder.left.setVisibility(View.INVISIBLE);
            } else viewHolder.left.setVisibility(View.VISIBLE);//必须有else语句
            if (pos > 0 && formatY.format(qxr_).equals(formatY.format(format.parse(data.get(pos - 1).CreateDate)))) {
                viewHolder.tv_top.setVisibility(View.GONE);
                viewHolder.v_top.setVisibility(View.GONE);
            } else {
                viewHolder.tv_top.setVisibility(View.VISIBLE);
                viewHolder.v_top.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.je.setText(String.format("%.2f", bean.OrderAmount));
        viewHolder.tzts.setText(bean.TotalDays + "");//
        viewHolder.tvBottom.setText(bean.StatusTips);
    }

    static class ViewHolder {
        @BindView(R.id.day)
        TextView day;
        @BindView(R.id.month)
        TextView month;
        @BindView(R.id.left)
        RelativeLayout left;
        @BindView(R.id.je)
        TextView je;
        @BindView(R.id.tzts)
        TextView tzts;
        @BindView(R.id.tv_bottom)
        TextView tvBottom;
        @BindView(R.id.tv_top)
        TextView tv_top;
        @BindView(R.id.v_top)
        View v_top;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

