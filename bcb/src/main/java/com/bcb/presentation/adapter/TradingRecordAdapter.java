package com.bcb.presentation.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.TradingRecordListBean;
import com.bcb.data.util.LogUtil;

import java.util.List;

public class TradingRecordAdapter extends BaseAdapter {
	
	private Context ctx;
	private List<TradingRecordListBean.InvetDetailBean.RecordsBean> data;

	public TradingRecordAdapter(Context ctx, List<TradingRecordListBean.InvetDetailBean.RecordsBean> data) {
		if (data != null) {
			this.ctx = ctx;
			this.data = data;
		}
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
		Holder mHolder;
		if (null == view) {
			view = View.inflate(ctx, R.layout.item_trading_record, null);
			mHolder = new Holder();
			mHolder.layout_name = (LinearLayout) view.findViewById(R.id.layout_name);
			mHolder.title = (TextView) view.findViewById(R.id.title);
			mHolder.time = (TextView) view.findViewById(R.id.time);
			mHolder.time_right = (TextView) view.findViewById(R.id.time_right);
			mHolder.status = (TextView) view.findViewById(R.id.status);
			mHolder.amount = (TextView) view.findViewById(R.id.amount);
			view.setTag(mHolder);
		} else {
			mHolder = (Holder) view.getTag();
		}
		//设置时间
		if (null != data.get(pos)) {
			if (!TextUtils.isEmpty(data.get(pos).getPayTime())) {
				mHolder.time.setText(data.get(pos).getPayTime().substring(0, 10));
				//				//如果时间相同则隐藏掉时间，否则显示时间
				//				if (pos > 0 && !TextUtils.isEmpty(data.get(pos - 1).getPayTime()) && data.get(pos).getPayTime()
				// .substring(0,10).equals(data.get(pos - 1).getPayTime().substring(0,10))) {
				//					mHolder.layout_date.setVisibility(View.GONE);
				//				} else {
				//					mHolder.layout_date.setVisibility(View.VISIBLE);
				//				}
			}

			//判断是否为空，包括 null 、"null"、""
			if (data.get(pos).getPackageName() != null) {
				mHolder.title.setText((data.get(pos).getPackageName().equalsIgnoreCase("null") || data.get(pos).getPackageName()
						.equalsIgnoreCase("")) ? "" : data.get(pos).getPackageName());
			} else {
				mHolder.title.setText("");
			}

			//判断是否为空，包括 null 、"null"、""
			if (data.get(pos).getOrderStatus() != null) {
				mHolder.status.setText((data.get(pos).getOrderStatus().equalsIgnoreCase("null") || data.get(pos).getOrderStatus()
						.equalsIgnoreCase("")) ? "" : data.get(pos).getOrderStatus());
			} else {
				mHolder.status.setText("");
			}
			mHolder.time_right.setText(data.get(pos).getDuration());//+"个月"
			mHolder.amount.setText(String.format("%.2f", data.get(pos).getOrderAmount()) + "元");
		}

		return view;
	}

	class Holder {
		LinearLayout layout_name;   //时间分组
		TextView title;
		TextView amount;    //金额
		TextView time;
		TextView time_right;
		TextView status;
	}

}
