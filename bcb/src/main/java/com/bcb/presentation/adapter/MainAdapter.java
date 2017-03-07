package com.bcb.presentation.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.event.BroadcastEvent;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.MainListBean2;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.ProgressDialogrUtils;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.view.activity.Activity_Login;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

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
			view = LayoutInflater.from(ctx).inflate(R.layout.item_main_yy, arg2, false);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else viewHolder = (ViewHolder) view.getTag();
		//设置ViewHolder的数据
		setDataWithViewHolder(viewHolder, pos);
		return view;
	}
	
	//设置ViewHolder数据
	private void setDataWithViewHolder(ViewHolder viewHolder, final int pos) {
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
		viewHolder.tvJe.setText("融资金额 " + bean.Amount + "");

		//是否已预约
		if (bean.PackageStatus == 1) {//0 未预约    1 已预约
			viewHolder.tvUp.setText("已预约");
			viewHolder.tvUp.setTextColor(0xff424954);
			viewHolder.tvDown.setVisibility(View.VISIBLE);
			viewHolder.tvDown.setText(bean.PredictCount + "人");
			viewHolder.rlYy.setBackgroundColor(0x00110011);//透明
			viewHolder.rlYy.setOnClickListener(null);
		} else {
			viewHolder.tvUp.setText("预约");
			viewHolder.tvUp.setTextColor(0xff4499f8);
			viewHolder.tvDown.setVisibility(View.GONE);
			viewHolder.rlYy.setBackgroundResource(R.drawable.bg_circle);
			viewHolder.rlYy.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (TokenUtil.getEncodeToken(ctx) != null) {
						requestAnnounce(pos);
					} else {
						Activity_Login.launche(ctx);
//						ToastUtil.alert(ctx, "请登录后再操作");
					}
				}
			});
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

	//点击请求预约
	private void requestAnnounce(final int pos) {
		JSONObject obj = new JSONObject();
		try {
			ProgressDialogrUtils.show(ctx,"正在加载数据...");
			obj.put("UniqueId", data.get(pos).PackageId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.RequestAnnounce, obj, TokenUtil.getEncodeToken(ctx), pos, new BcbRequest.BcbIndexCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response, int index) {
				LogUtil.i("bqt", "【预约】"+index+"---"+response.toString());

				try {
					if (response.getInt("status") == 1) {
						//设置对应位置的数据
						if (response.getJSONObject("result").getInt("PredictCount") > 0) {
							data.get(index).PredictCount=response.getJSONObject("result").getInt("PredictCount");
							App.saveUserInfo.setPreviewInvest(data.get(pos).PackageId);
							//更新数据
							notifyDataSetChanged();
							EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.REFRESH));
						}
					} else {
						ToastUtil.alert(ctx, response.getString("message").isEmpty() ? "预约失败" : response.getString("message"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				ProgressDialogrUtils.hide();
			}

			@Override
			public void onErrorResponse(Exception error) {
				ProgressDialogrUtils.hide();
			}
		});
		App.getInstance().getRequestQueue().add(jsonRequest);
	}

}

