package com.bcb.base.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;


/**
 * Created by ruiqin.shen
 * 类说明：自定义toolBar
 */

public class ToolbarView extends RelativeLayout {
    public ToolbarView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public ToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public ToolbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    TextView toolbarTitle;
    Toolbar toolBar;

    /**
     * 初始化视图
     */
    private void initView(Context context, AttributeSet attrs, int defStyle) {

        LayoutInflater.from(context).inflate(R.layout.view_toolbar, this);//加载布局

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
    }

    /**
     * 设置标题
     */
    public void setToolBarTitle(String title) {
        toolbarTitle.setText(title);
    }

    /**
     * 设置字体颜色
     */
    public void setToolBarTitleColor(Context context, int color) {
        toolbarTitle.setTextColor(ContextCompat.getColor(context, color));
    }

    /**
     * 返回ToolBar
     *
     * @return
     */
    public Toolbar getToolBar() {
        return toolBar;
    }

}
