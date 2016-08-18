package com.bcb.presentation.view.custom.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.bcb.R;
import com.bcb.presentation.view.activity.Activity_Open_Account;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/8/18 14:23
 */
public class BasicDialog extends Dialog implements View.OnClickListener {
    Context context;

    public BasicDialog(Context context) {
        this(context, 0);
    }

    public BasicDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        initView();
    }

    public void initView() {
        setContentView(R.layout.fragment_dialog);
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.iv_open).setOnClickListener(this);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_open:
                context.startActivity(new Intent(context, Activity_Open_Account.class));
                dismiss();
                break;
            default:
                break;
        }
    }
}
