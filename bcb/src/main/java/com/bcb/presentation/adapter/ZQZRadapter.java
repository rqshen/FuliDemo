package com.bcb.presentation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.ZQBGbean;
import com.bcb.data.util.LogUtil;

import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/10/14 15:42
 */
public class ZQZRadapter extends BaseAdapter {

	private Context ctx;
	private List<ZQBGbean.RecordsBean> data;

	public ZQZRadapter(Context ctx, List<ZQBGbean.RecordsBean> data) {
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
			mHolder.time.setText(data.get(pos).CreateDate);
			mHolder.title.setText(data.get(pos).PackageName);
			mHolder.status.setText(data.get(pos).Status);
			mHolder.time_right.setText(data.get(pos).Duration);//+"个月"
			mHolder.amount.setText(String.format("%.2f", data.get(pos).Amount) + "元");
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
