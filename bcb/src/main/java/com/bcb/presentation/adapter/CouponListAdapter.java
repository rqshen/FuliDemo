package com.bcb.presentation.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.event.BroadcastEvent;
import com.bcb.data.bean.CouponRecordsBean;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyConstants;
import com.bcb.presentation.view.activity.A_Elite_Loan;
import com.bcb.presentation.view.activity.Activity_Open_Account;
import com.bcb.presentation.view.activity.Activity_Withdraw;
import com.bcb.presentation.view.custom.AlertView.AlertView;

import java.util.List;

import de.greenrobot.event.EventBus;

public class CouponListAdapter extends BaseAdapter {

	//对话框
	private AlertView alertView;
	private Context ctx;
	private List<CouponRecordsBean> data;
	private float investAmount;
	private int couponType;

	public CouponListAdapter(Context ctx, List<CouponRecordsBean> data, float investAmount) {
		if (data != null) {
			this.ctx = ctx;
			this.data = data;
		}
		this.investAmount = investAmount;
	}

	//选择优惠券的适配器构造器
	public CouponListAdapter(Context ctx, List<CouponRecordsBean> data, float investAmount, int couponType) {
		if (data != null) {
			this.ctx = ctx;
			this.data = data;
		}
		this.investAmount = investAmount;
		this.couponType = couponType;
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
			view = View.inflate(ctx, R.layout.item_coupon2, null);
			mHolder = new Holder();
			mHolder.coupon_icon = (TextView) view.findViewById(R.id.coupon_icon);
			mHolder.layout_view = (RelativeLayout) view.findViewById(R.id.layout_view);
			mHolder.amount = (TextView) view.findViewById(R.id.coupon_amount);
			mHolder.time = (TextView) view.findViewById(R.id.coupon_time);
			mHolder.rule = (TextView) view.findViewById(R.id.coupon_rule);
			mHolder.select = (TextView) view.findViewById(R.id.select);
			view.setTag(mHolder);
		} else mHolder = (Holder) view.getTag();
		mHolder.coupon_icon.setText(data.get(pos).getName());
		mHolder.amount.setText("" + (int) (data.get(pos).getAmount()));
		if (data.get(pos).getExpireDate() != null) {
			String time = data.get(pos).getExpireDate().toString();
			mHolder.time.setText(time.substring(0, time.indexOf(" ")) + "过期");
		}
		//是否显示"使用"按钮
		if (data.get(pos).getConditionDescn() != null && !data.get(pos).getConditionDescn().isEmpty()) {
			mHolder.rule.setVisibility(View.VISIBLE);
			mHolder.rule.setText(data.get(pos).getConditionDescn());
		} else mHolder.rule.setVisibility(View.GONE);

		//1 未使用 10 冻结中  20 已使用  30 已回收  40 已过期

		if (data.get(pos).getStatus() == 1) setupUsedCoupon(mHolder);
		else setupUnusedCoupon(mHolder);

		// 选择优惠券时，传递进来的值大于等于0******************************************************************************************
		if (investAmount >= 0) {
			mHolder.select.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					if (MyConstants.WITHDRAW == data.get(pos).getCouponType()) {//提现券时，点击则跳转至提现界面
						ctx.startActivity(new Intent(ctx, Activity_Withdraw.class));
					} //如果是利息折扣券，CouponType = 16， 则也需要返回券的描述
					else if (data.get(pos).getCouponType() == 16) {
						intent.putExtra("CouponId", data.get(pos).getCouponId());
						//利息抵扣券的金额
						intent.putExtra("InterestAmount", (int) (data.get(pos).getAmount()));
						//最小借款金额
						intent.putExtra("InterestMinAmount", (int) (data.get(pos).getMinAmount()));
						LogUtil.i("bqt", "【最小借款金额】" + (int) (data.get(pos).getMinAmount()));
						//借款金额描述
						intent.putExtra("InterestDescn", data.get(pos).getConditionDescn() + "");
						//返回优惠券张数
						//	intent.putExtra("TotalCount", recordsBeans == null ? 0 : recordsBeans.size());
						((Activity) ctx).setResult(1, intent);
						((Activity) ctx).finish();
					} else {
						intent.putExtra("CouponId", data.get(pos).getCouponId());
						intent.putExtra("CouponAmount", data.get(pos).getAmount() + "");
						intent.putExtra("CouponMinAmount", data.get(pos).getMinAmount() + "");
						intent.putExtra("ConditionDescn", data.get(pos).getConditionDescn() + "");
						//如果是体验券，则返回描述信息和体验券金额
						if ((data.get(pos).getCouponType() & 1) == 1) {
							intent.putExtra("Amount", data.get(pos).getAmount());
							intent.putExtra("ConditionDescn", data.get(pos).getConditionDescn());
						}
						((Activity) ctx).setResult(1, intent);
						((Activity) ctx).finish();
					}
				}
			});
		}
		// 未使用优惠券//******************************************************************************************
		else if (investAmount == -1) {
			mHolder.select.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (MyConstants.WITHDRAW == data.get(pos).getCouponType()) {//提现券时，点击则跳转至提现界面
						ctx.startActivity(new Intent(ctx, Activity_Withdraw.class));
					} else if (MyConstants.LOAN_SUBSIDIES == data.get(pos).getCouponType()) {//借款补贴券时，点击则跳转至借款界面
						//用户还没认证时，先去认证
						if (App.mUserDetailInfo != null && App.mUserDetailInfo.HasOpenCustody)
							ctx.startActivity(new Intent(ctx, A_Elite_Loan.class));
						else ctx.startActivity(new Intent(ctx, Activity_Open_Account.class));
					} else if (MyConstants.CASH == data.get(pos).getCouponType()) {//现金券时，点击则跳转至产品列表界面
						EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.PRODUCT));
						((Activity) ctx).finish();
					} else {
						Intent intent = new Intent();
						intent.putExtra("selectCoupon", true);
						((Activity) ctx).setResult(1, intent);
						((Activity) ctx).finish();
					}
				}
			});
		}
		// 已使用优惠券、已过期优惠券//******************************************************************************************
		else if (investAmount == -2 || investAmount == -3) {
			mHolder.select.setOnClickListener(null);
		}
		return view;
	}

	//设置不可用优惠券的样式
	private void setupUnusedCoupon(Holder holder) {
		//		holder.amount.setTextColor(ctx.getResources().getColor(R.color.txt_gray));
		//		holder.time.setTextColor(ctx.getResources().getColor(R.color.txt_gray));
		//		holder.txt_yuan.setTextColor(ctx.getResources().getColor(R.color.txt_gray));
		holder.select.setVisibility(View.GONE);
		holder.layout_view.setEnabled(false);
		holder.layout_view.setBackgroundResource(R.drawable.bg_yhq_used);
	}

	//设置可用优惠券的样式
	private void setupUsedCoupon(Holder holder) {
		holder.select.setVisibility(View.VISIBLE);
		holder.layout_view.setEnabled(true);
		holder.layout_view.setBackgroundResource(R.drawable.bg_yhq_unused);
	}

	class Holder {
		TextView coupon_icon;
		TextView amount;
		TextView time;
		TextView rule;
		TextView select;
		RelativeLayout layout_view;
	}
}