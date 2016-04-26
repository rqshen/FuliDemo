package com.bcb.presentation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bcb.R;
import com.bcb.data.bean.MoneyFlowingWaterRecordsBean;
import com.bcb.data.util.LogUtil;
import java.util.List;

public class MoneyFlowingWaterAdapter extends BaseAdapter {
	
	private Context ctx;
	private List<MoneyFlowingWaterRecordsBean> data;

	public MoneyFlowingWaterAdapter(Context ctx, List<MoneyFlowingWaterRecordsBean> data) {
		if (data != null) {
			this.ctx = ctx;
			this.data = data;
		}
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
		ViewHolder mViewHolder;
		if (null == view) {
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_money_flowing_water, null);

            mViewHolder = new ViewHolder();
            mViewHolder.layout_date = (LinearLayout) view.findViewById(R.id.layout_date);
            mViewHolder.value_date = (TextView) view.findViewById(R.id.value_date);
            mViewHolder.value_amount = (TextView) view.findViewById(R.id.value_amount);
            mViewHolder.value_status = (TextView) view.findViewById(R.id.value_status);
            mViewHolder.image_status = (ImageView) view.findViewById(R.id.image_status);
			view.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) view.getTag();
		}

        //设置时间
        mViewHolder.value_date.setText(data.get(pos).getTime());
        //如果时间相同则隐藏掉时间，否则显示时间
        if (pos > 0 && data.get(pos).getTime().equals(data.get(pos - 1).getTime())) {
            mViewHolder.layout_date.setVisibility(View.GONE);
        } else {
            mViewHolder.layout_date.setVisibility(View.VISIBLE);
        }

        //收入支出是不同的样式，支出为1，收入为2，其余为0
        if (data.get(pos).getType().equals("支出")) {
            mViewHolder.value_amount.setTextColor(Color.argb(255, 0, 187, 9));
            mViewHolder.value_amount.setText("-" + data.get(pos).getAmount());
        } else {
            mViewHolder.value_amount.setTextColor(Color.argb(255, 219, 56, 56));
            mViewHolder.value_amount.setText("+" + data.get(pos).getAmount());
        }

        //状态描述
        mViewHolder.value_status.setText(data.get(pos).getStatusDescn());

        //根据交易类型和状态来判断是否更改图片
        mViewHolder.image_status.setVisibility(View.VISIBLE);
        //成功状态
        if (data.get(pos).getStatusGroup() == 3) {
            mViewHolder.image_status.setBackgroundResource(R.drawable.ind);
        }
        //退回状态
        else if (data.get(pos).getStatusGroup() == 2) {
            mViewHolder.image_status.setBackgroundResource(R.drawable.money_status_back);
        }
        //失败状态
        else if (data.get(pos).getStatusGroup() == 1) {
            mViewHolder.image_status.setBackgroundResource(R.drawable.money_status_failed);
        }
        //审核中状态
        else if (data.get(pos).getStatusGroup() == 0) {
            mViewHolder.image_status.setBackgroundResource(R.drawable.money_status_doing);
        }
        //不是以上几个则隐藏
        else {
            mViewHolder.image_status.setVisibility(View.GONE);
        }
		return view;
	}


    class ViewHolder {
        LinearLayout layout_date;   //时间分组
        TextView value_date;    //时间
        TextView value_amount;  //值
        TextView value_status;  //状态描述
        ImageView image_status; //图片状态
	}

}
