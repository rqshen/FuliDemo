package com.bcb.presentation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.JEnterprise;

import java.util.List;

public class MyPopupListAdapter extends BaseAdapter {
	private ViewHolder holder;
	private Context context;
	private List<JEnterprise.EnterpriseListBean> mList;

	public MyPopupListAdapter(Context context, List<JEnterprise.EnterpriseListBean> mList) {
		this.context = context;
		this.mList = mList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.pop_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else holder = (ViewHolder) convertView.getTag();
		holder.name.setText(mList.get(position).Name);
		return convertView;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView name;
	}
}
