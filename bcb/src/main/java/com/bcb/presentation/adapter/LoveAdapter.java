package com.bcb.presentation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.love.LoveBean;
import com.bcb.presentation.view.custom.CircleProgressBar;

import java.util.List;

/**
 * Created by Ray on 2016/6/7.
 *
 * @desc 聚爱首页 Adapter
 */
public class LoveAdapter extends BaseAdapter{

    private List<LoveBean> loveBeens;
    private Context ctx;

    public LoveAdapter(Context ctx, List<LoveBean> loveBeens) {
        this.ctx = ctx;
        this.loveBeens = loveBeens;
    }

    @Override
    public int getCount() {
        return null == loveBeens ? 0 : loveBeens.size();
    }

    @Override
    public Object getItem(int position) {
        return null == loveBeens ? null : loveBeens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = View.inflate(ctx, R.layout.item_love, null);
            viewHolder = new ViewHolder();
            setupViewholder(viewHolder, convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置ViewHolder的数据
        setDataWithViewHolder(viewHolder, position);
        return convertView;
    }

    //初始化ViewHolder
    private void setupViewholder(ViewHolder viewHolder, View view) {
        viewHolder.name = (TextView) view.findViewById(R.id.name);
        viewHolder.desc = (TextView) view.findViewById(R.id.desc);
        viewHolder.company = (TextView) view.findViewById(R.id.company);
        viewHolder.love_money = (TextView) view.findViewById(R.id.love_money);
        viewHolder.love_support = (TextView) view.findViewById(R.id.love_support);
        viewHolder.love_circle_progress = (CircleProgressBar) view.findViewById(R.id.love_circle_progress);
    }

    //设置ViewHolder数据
    private void setDataWithViewHolder(ViewHolder viewHolder, int pos) {
        viewHolder.name.setText(loveBeens.get(pos).getName());
        viewHolder.desc.setText(loveBeens.get(pos).getDesc());
        viewHolder.company.setText(loveBeens.get(pos).getCompany());
        viewHolder.love_money.setText(loveBeens.get(pos).getMoney() + "元");
        viewHolder.love_support.setText(loveBeens.get(pos).getSupport() + "次");
        viewHolder.love_circle_progress.setProgress(loveBeens.get(pos).getProgress());
    }

    private class ViewHolder {
        TextView name;
        TextView desc;
        TextView company;
        TextView love_money;
        TextView love_support;
        CircleProgressBar love_circle_progress;
    }
}
