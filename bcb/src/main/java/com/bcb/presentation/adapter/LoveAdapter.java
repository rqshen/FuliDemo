package com.bcb.presentation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = View.inflate(ctx, R.layout.item_love, null);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView name;
        CircleProgressBar love_circle_progress;
    }
}
