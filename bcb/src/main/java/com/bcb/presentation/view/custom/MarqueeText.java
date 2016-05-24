package com.bcb.presentation.view.custom;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * Created by Ray on 2015/11/15.
 * 跑马灯,无需焦点也能跑动
 */
public class MarqueeText extends TextView {

	public MarqueeText(Context con) {
		super(con);
	}

	public MarqueeText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	public boolean isFocused() {
		return true;
	}
	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

}
