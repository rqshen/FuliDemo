package com.bcb.presentation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.BankItem;
import com.bcb.data.util.BankLogo;
import com.bcb.data.util.LogUtil;

import java.util.List;

public class BankListAdapter extends BaseAdapter {
	
	private Context ctx;
	private List<BankItem> data;

	public BankListAdapter(Context ctx, List<BankItem> data) {
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
			view = View.inflate(ctx, R.layout.item_bank, null);
			mHolder = new Holder();
			mHolder.bank_icon = (ImageView) view.findViewById(R.id.bank_icon);
			mHolder.bank_name = (TextView) view.findViewById(R.id.bank_name);
			mHolder.bank_rule = (TextView) view.findViewById(R.id.bank_rule);
			view.setTag(mHolder);
		} else {
			mHolder = (Holder) view.getTag();
		}

		BankLogo bankLogo = new BankLogo();
		mHolder.bank_icon.setBackgroundResource(bankLogo.getDrawableBankLogo(data.get(pos).getBankCode()));

		if (data.get(pos).getBankName() != null) {
			mHolder.bank_name.setText(data.get(pos).getBankName());
		}
        //如果单笔限额和每日限额都大于0.0万时才显示
		if (data.get(pos).getMaxSingle() > 0 && data.get(pos).getMaxDay() > 0) {
			mHolder.bank_rule.setVisibility(View.VISIBLE);
			mHolder.bank_rule.setText("单笔限额"+ data.get(pos).getMaxSingle() + "，每日限额" + data.get(pos).getMaxDay());
		} else {
			mHolder.bank_rule.setVisibility(View.GONE);
		}
		return view;
	}

	class Holder {		
		ImageView bank_icon;
		TextView bank_name;
		TextView bank_rule;
	}

}