package com.bcb.presentation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.PrivilegeMoneyDto;

import java.util.List;

/**
 * Created by Ray on 2016/7/8.
 *
 * @desc 特权本金
 */
public class PrivilegeMoneyAdapter extends BaseAdapter{

    private Context ctx;
    private List<PrivilegeMoneyDto> datas;

    public PrivilegeMoneyAdapter(Context ctx, List<PrivilegeMoneyDto> datas) {
        this.ctx = ctx;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return null == datas ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == view){
            view = View.inflate(ctx, R.layout.item_privilege_money,null);
            viewHolder = new ViewHolder();
            setupViewholder(viewHolder,view);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        //设置ViewHolder的数据
        setDataWithViewHolder(viewHolder, position);
        return view;
    }

    private class ViewHolder{
        public TextView title,income,term;
    }

    //初始化ViewHolder
    private void setupViewholder(ViewHolder viewHolder, View view) {
        viewHolder.title = (TextView) view.findViewById(R.id.title);
        viewHolder.income = (TextView) view.findViewById(R.id.income);
        viewHolder.term = (TextView) view.findViewById(R.id.term);
    }

    //设置ViewHolder数据
    private void setDataWithViewHolder(ViewHolder viewHolder, int pos) {
        viewHolder.title.setText(datas.get(pos).getTitle());
        viewHolder.income.setText(String.format("￥%f",datas.get(pos).getIncome()));
        viewHolder.term.setText(datas.get(pos).getTerm());
    }
}
