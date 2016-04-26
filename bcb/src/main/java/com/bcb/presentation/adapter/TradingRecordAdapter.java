package com.bcb.presentation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.TradingRecordRecordsBean;
import com.bcb.data.util.LogUtil;

import java.util.List;

public class TradingRecordAdapter extends BaseAdapter {
	
	private Context ctx;
	private List<TradingRecordRecordsBean> data;

	public TradingRecordAdapter(Context ctx, List<TradingRecordRecordsBean> data) {
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
			mHolder.name = (TextView) view.findViewById(R.id.name);
			mHolder.status = (TextView) view.findViewById(R.id.status);
			mHolder.amount = (TextView) view.findViewById(R.id.amount);
			mHolder.time = (TextView) view.findViewById(R.id.time);		
			view.setTag(mHolder);
		} else {
			mHolder = (Holder) view.getTag();
		}
		//判断是否为空，包括 null 、"null"、""
		if (data.get(pos).getPackageName() != null) {
			mHolder.name.setText((data.get(pos).getPackageName().equalsIgnoreCase("null") || data.get(pos).getPackageName().equalsIgnoreCase(""))
					? "" : data.get(pos).getPackageName());
		} else {
			mHolder.name.setText("");
		}

		//判断是否为空，包括 null 、"null"、""
		if (data.get(pos).getOrderStatus() != null) {
			mHolder.status.setText((data.get(pos).getOrderStatus().equalsIgnoreCase("null") || data.get(pos).getOrderStatus().equalsIgnoreCase(""))
					? "" : data.get(pos).getOrderStatus());
		} else {
			mHolder.status.setText("");
		}

		mHolder.amount.setText(String.format("%.2f", data.get(pos).getOrderAmount()) + "元");

		//判断是否为空，包括 null 、"null"、""
		if (data.get(pos).getPayTime() != null) {
			mHolder.time.setText((data.get(pos).getPayTime().equalsIgnoreCase("null") || data.get(pos).getPayTime().equalsIgnoreCase(""))
					? "" : data.get(pos).getPayTime());
		}

		return view;
	}

	class Holder {				
		TextView name;
		TextView status;
		TextView amount;
		TextView time;	
	}

}
