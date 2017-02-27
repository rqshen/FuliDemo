package com.bcb.presentation.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.data.bean.MainListBean2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainAdapter extends BaseAdapter {
	
	private Context ctx;
	private List<MainListBean2.XbygBean> data;
	
	//默认标的构造方法
	public MainAdapter(Context ctx, List<MainListBean2.XbygBean> data) {
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
			view = View.inflate(ctx, R.layout.item_main_yy, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else viewHolder = (ViewHolder) view.getTag();
		//设置ViewHolder的数据
		setDataWithViewHolder(viewHolder, pos);
		return view;
	}
	
	//设置ViewHolder数据
	private void setDataWithViewHolder(ViewHolder viewHolder, int pos) {
		MainListBean2.XbygBean bean = data.get(pos);
		viewHolder.tvRate.setText(String.valueOf(bean.Rate));
		//福袋利率
		String welfareRate = TextUtils.isEmpty(App.getInstance().getWelfare()) ? "%" : "%+" + App.getInstance().getWelfare() + "%";
		viewHolder.tvRateAdd.setText(welfareRate);
		viewHolder.tvTime.setText(String.valueOf(bean.Duration));
		//天标月标
		switch (bean.DurationExchangeType) {
			case 1:
				viewHolder.tvDw.setText("天");
				break;
			default:
				viewHolder.tvDw.setText("个月");
				break;
		}
		viewHolder.tvJe.setText("融资金额" + bean.Amount + "元");

		//是否已预约
		if (bean.PackageStatus==1) {//0 未预约    1 已预约
			viewHolder.tvUp.setText("已预约");
			viewHolder.tvUp.setTextColor(0xff424954);
			viewHolder.tvDown.setVisibility(View.VISIBLE);
			viewHolder.tvDown.setText(bean.PredictCount + "人");
			viewHolder.rlYy.setBackgroundColor(0x00110011);//透明
		} else {
			viewHolder.tvUp.setText("预约");
			viewHolder.tvUp.setTextColor(0xff4499f8);
			viewHolder.tvDown.setVisibility(View.GONE);
			viewHolder.rlYy.setBackgroundResource(R.drawable.bg_circle);
		}
	}
	
	static class ViewHolder {
		@BindView(R.id.tv_rate) TextView tvRate;
		@BindView(R.id.tv_rate_add) TextView tvRateAdd;
		@BindView(R.id.tv_time) TextView tvTime;
		@BindView(R.id.tv_dw) TextView tvDw;
		@BindView(R.id.tv_je) TextView tvJe;
		@BindView(R.id.tv_up) TextView tvUp;
		@BindView(R.id.tv_down) TextView tvDown;
		@BindView(R.id.rl_yy) RelativeLayout rlYy;

		ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}

