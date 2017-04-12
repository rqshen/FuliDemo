package com.bcb.presentation.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bcb.R;
import com.bcb.module.myinfo.financial.financialdetail.projectdetail.ProjectDetailActivity;

import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2017/2/26 21:19
 */
public class UPMarqueeView extends ViewFlipper {

	public UPMarqueeView(Context context) {
		super(context);
		init(context, null, 0);
	}

	public UPMarqueeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr) {
		setFlipInterval(3000);
		Animation animIn = AnimationUtils.loadAnimation(getContext(), R.anim.anim_marquee_in);
		animIn.setDuration(500);
		setInAnimation(animIn);
		Animation animOut = AnimationUtils.loadAnimation(getContext(), R.anim.anim_marquee_out);
		animOut.setDuration(500);
		setOutAnimation(animOut);
	}

	/**
	 * 设置循环滚动的View数组
	 */
	public void setViews(final List<UPMarqueeViewData> datas) {
		if (datas == null || datas.size() == 0) return;
		int size = datas.size();
		for (int i = 0; i < size; i++) {
			final int num = i;
			TextView tv = new TextView(getContext());
			tv.setText(datas.get(i).Title);
			tv.setTextColor(0xff424954);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ProjectDetailActivity.launche(getContext(), "公告", datas.get(num).PageUrl);
				}
			});
			addView(tv);
		}
	}
}