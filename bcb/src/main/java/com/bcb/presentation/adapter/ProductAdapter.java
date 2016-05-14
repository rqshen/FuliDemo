package com.bcb.presentation.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.data.bean.ProductRecordsBean;

import java.util.List;

public class ProductAdapter extends BaseAdapter {
	
	private Context ctx;
	private List<ProductRecordsBean> data;
    //判断是否新手标
    private boolean isNewProduct = false;

	//默认标的构造方法
    public ProductAdapter(Context ctx, List<ProductRecordsBean> data) {
        if (data != null) {
            this.ctx = ctx;
            this.data = data;
        }
    }

    public ProductAdapter(Context ctx, List<ProductRecordsBean> data, boolean isNewProduct) {
        if (data != null) {
            this.ctx = ctx;
            this.data = data;
            this.isNewProduct = isNewProduct;
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
		ViewHolder viewHolder;
		if (null == view) {
			view = View.inflate(ctx, R.layout.item_product, null);
			viewHolder = new ViewHolder();
			setupViewholder(viewHolder, view);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		//设置ViewHolder的数据
		setDataWithViewHolder(viewHolder, pos);
		return view;
	}

	//初始化ViewHolder
	private void setupViewholder(ViewHolder viewHolder, View view) {
		viewHolder.name = (TextView) view.findViewById(R.id.name);
		viewHolder.rate = (TextView) view.findViewById(R.id.rate);
		viewHolder.fukubukuro_rate = (TextView) view.findViewById(R.id.fukubukuro_rate);
		viewHolder.duration = (TextView) view.findViewById(R.id.duration);
        viewHolder.duration_description = (TextView) view.findViewById(R.id.duration_description);
		viewHolder.amountBalance = (TextView) view.findViewById(R.id.amountBalance);
        viewHolder.valuePercent = (TextView) view.findViewById(R.id.value_percent);
        viewHolder.progressPercent = (ProgressBar) view.findViewById(R.id.progress_percent);
        viewHolder.progressPercent.setMax(100);
		viewHolder.newProductIndicator = (ImageView) view.findViewById(R.id.new_product_image);
	}

	//设置ViewHolder数据
	private void setDataWithViewHolder(ViewHolder viewHolder, int pos) {
		viewHolder.name.setText(data.get(pos).getName());
		viewHolder.rate.setText(data.get(pos).getRate() + data.get(pos).getRewardRate() + "");
		//福袋利率
		String welfareRate = TextUtils.isEmpty(App.getInstance().getWelfare()) ? "" : "+" + App.getInstance().getWelfare() + "%";
		viewHolder.fukubukuro_rate.setText(welfareRate);
        viewHolder.duration.setText(data.get(pos).getDuration() + "");
		//天标月标
		switch (data.get(pos).getDurationExchangeType()) {
			case 1:
				viewHolder.duration_description.setText("天");
				break;
			case 2:
				viewHolder.duration_description.setText("个月");
				break;
			default:
				viewHolder.duration.setText("");
				break;
		}
		viewHolder.amountBalance.setText((int)data.get(pos).getAmountBalance() + " 元" );
		//是否显示新标预告
		if (isNewProduct) {
			viewHolder.newProductIndicator.setVisibility(View.VISIBLE);
			viewHolder.newProductIndicator.setBackgroundResource(R.drawable.item_new_product_background);
		} else {
			viewHolder.newProductIndicator.setVisibility(View.GONE);
		}
        //百分比
        float percent = (100 - 100 * (data.get(pos).getAmountBalance()/data.get(pos).getAmountTotal()));
		if (data.get(pos).getAmountBalance() <= 0) {
			viewHolder.valuePercent.setText("售罄");
			viewHolder.progressPercent.setProgressDrawable(ctx.getResources().getDrawable(R.drawable.item_progress_gray));
		} else {
			viewHolder.valuePercent.setText(String.format("%.1f", percent) + "%");
		}
        viewHolder.progressPercent.setProgress((int) percent);
	}



	class ViewHolder {
		TextView name;
        TextView rate;
		TextView fukubukuro_rate;
		TextView duration;
        TextView duration_description;
		TextView amountBalance;
        TextView valuePercent;
        ProgressBar progressPercent;
        ImageView newProductIndicator;
	}
}

