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
import com.bcb.presentation.view.activity.Activity_Browser;

import java.util.List;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2017/2/26 21:19
 */
public class UPMarqueeView extends ViewFlipper {
	private Context mContext;
	/**
	 * 是否开启动画
	 */
	private boolean isSetAnimDuration = true;
	/**
	 * 时间间隔
	 */
	private int interval = 3000;
	/**
	 * 动画时间
	 */
	private int animDuration = 500;

	public UPMarqueeView(Context context) {
		super(context);
		init(context, null, 0);
	}

	public UPMarqueeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr) {
		this.mContext = context;
		setFlipInterval(interval);
		if (isSetAnimDuration) {
			Animation animIn = AnimationUtils.loadAnimation(mContext, R.anim.anim_marquee_in);
			animIn.setDuration(animDuration);
			setInAnimation(animIn);
			Animation animOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_marquee_out);
			animOut.setDuration(animDuration);
			setOutAnimation(animOut);
		}
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
					Activity_Browser.launche(getContext(), "移动端公告", datas.get(num).PageUrl);
				}
			});
			addView(tv);
		}
	}

}