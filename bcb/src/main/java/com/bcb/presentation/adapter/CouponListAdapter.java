package com.bcb.presentation.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.presentation.view.activity.Activity_Withdraw;
import com.bcb.data.bean.CouponRecordsBean;
import com.bcb.data.util.LogUtil;

import java.util.List;

public class CouponListAdapter extends BaseAdapter {
	
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
			view = View.inflate(ctx, R.layout.item_coupon, null);
			mHolder = new Holder();
			mHolder.coupon_icon = (TextView)view.findViewById(R.id.coupon_icon);
			mHolder.item_bg_layout = (LinearLayout) view.findViewById(R.id.item_bg_layout);
			mHolder.layout_view = (RelativeLayout) view.findViewById(R.id.layout_view);
            mHolder.txt_yuan = (TextView)view.findViewById(R.id.txt_yuan);
			mHolder.amount = (TextView) view.findViewById(R.id.coupon_amount);
			mHolder.time = (TextView) view.findViewById(R.id.coupon_time);
			mHolder.rule = (TextView) view.findViewById(R.id.coupon_rule);
			mHolder.select = (TextView) view.findViewById(R.id.select);
			
			view.setTag(mHolder);
		} else {
			mHolder = (Holder) view.getTag();
		}
        //设置名称
        mHolder.coupon_icon.setText(data.get(pos).getName());

		//根据CouponType设置优惠券类型，然后进行券的背景
		switch (data.get(pos).getCouponType()) {
			//体验券
            case 1:
            //免息券
            case 4:
            case 1048576:
				mHolder.layout_view.setBackgroundResource(R.color.red);
				break;

            //现金券
			case 2:
				mHolder.layout_view.setBackgroundResource(R.color.company_name);
				break;

            //提现券
			case 8:
				mHolder.layout_view.setBackgroundResource(R.color.green);
				break;
		}

		mHolder.amount.setText(String.format("%.2f", data.get(pos).getAmount()));
		if (data.get(pos).getExpireDate() != null) {
			String time = data.get(pos).getExpireDate().toString();
			mHolder.time.setText(time.substring(0, time.indexOf(" ")) + "过期");
		}

		if (data.get(pos).getConditionDescn() != null && !data.get(pos).getConditionDescn().isEmpty()) {
            mHolder.rule.setVisibility(View.VISIBLE);
			mHolder.rule.setText(data.get(pos).getConditionDescn());
		} else {
            mHolder.rule.setVisibility(View.GONE);
        }

		// 选择优惠券时，传递进来的值大于等于0
		if (investAmount >= 0) {
			//判断couponType是否为1，如果为1，则表示是新手体验标
			if (couponType == 1 || investAmount >= data.get(pos).getMinAmount()) {
				mHolder.select.setVisibility(View.VISIBLE);
				mHolder.item_bg_layout.setEnabled(true);
				mHolder.item_bg_layout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
                        //提现券时，点击则跳转至提现界面
						if (data.get(pos).getCouponType() == 8) {
                            gotoNewPage(Activity_Withdraw.class);
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra("CouponId", data.get(pos).getCouponId());
                            intent.putExtra("CouponAmount", data.get(pos).getAmount()+"");
                            intent.putExtra("CouponMinAmount", data.get(pos).getMinAmount()+"");
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
			//如果不是体验券且输入金额最小使用金额
			else {
				setupUnusedCoupon(mHolder);
			}
		} 
		// 未使用优惠券
		else if (investAmount == -1) {
            mHolder.item_bg_layout.setEnabled(true);
            mHolder.item_bg_layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //提现券时，点击则跳转至提现界面
                    if (data.get(pos).getCouponType() == 8) {
                        gotoNewPage(Activity_Withdraw.class);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("selectCoupon", true);
                        ((Activity) ctx).setResult(1, intent);
                        ((Activity) ctx).finish();
                    }
                }
            });
		} 
		// 已使用优惠券
		else if (investAmount == -2) {
			setupUnusedCoupon(mHolder);
		} 
		// 已过期优惠券
		else if (investAmount == -3) {
            setupUnusedCoupon(mHolder);
		} 
		
		return view;
	}
    //跳转至新页面
    private void gotoNewPage(Class<? extends Activity> object) {
        Intent intent = new Intent(ctx, object);
        ctx.startActivity(intent);
        ((Activity) ctx).finish();
    }

	//设置不可用优惠券的样式
	private void setupUnusedCoupon(Holder holder) {
		holder.amount.setTextColor(ctx.getResources().getColor(R.color.txt_gray));
		holder.time.setTextColor(ctx.getResources().getColor(R.color.txt_gray));
		holder.txt_yuan.setTextColor(ctx.getResources().getColor(R.color.txt_gray));
		holder.select.setVisibility(View.GONE);
		holder.item_bg_layout.setEnabled(false);
		holder.layout_view.setBackgroundResource(R.color.gray);
	}

	class Holder {
		TextView coupon_icon;
		TextView amount;
		TextView time;
		TextView rule;
        TextView txt_yuan;
		TextView select;
		LinearLayout item_bg_layout;
		RelativeLayout layout_view;
	}
}
