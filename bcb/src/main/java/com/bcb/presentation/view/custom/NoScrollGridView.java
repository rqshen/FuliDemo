package com.bcb.presentation.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * Created by Ray on 2016/5/31.
 *
 * @desc 没有滑动的GridView，主要为了嵌套在ScrollView中使用，能够自动增长显示
 */
public class NoScrollGridView extends GridView {

    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollGridView(Context context) {
        super(context);
    }

    public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
