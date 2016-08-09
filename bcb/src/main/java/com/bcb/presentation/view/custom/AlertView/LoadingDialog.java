package com.bcb.presentation.view.custom.AlertView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.presentation.view.custom.CustomGifView;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/8/9 10:16
 */
public class LoadingDialog extends Dialog {
    private RelativeLayout root;
    private TextView tv_messag;

    public LoadingDialog(Context context) {
        super(context, R.style.loading_dialog);
        initView(context);
    }


    private void initView(Context context) {
        setContentView(R.layout.loading_dialog);
        root = (RelativeLayout) findViewById(R.id.root);
        tv_messag = (TextView) findViewById(R.id.tv_messag);
        root.addView(new CustomGifView(context, R.drawable.loading_gif));
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
    }

    @Override
    public void setOnCancelListener(OnCancelListener listener) {
        super.setOnCancelListener(listener);
    }

    public void setMessage(String string) {
        tv_messag.setVisibility(View.VISIBLE);
        tv_messag.setText(string);
    }
}