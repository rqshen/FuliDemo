package com.bcb.presentation.view.custom.CustomDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.bcb.R;

/**
 * Created by cain on 16/3/14.
 */
public class IdentifyAlertView implements View.OnClickListener {
    private View view;
    //确定按钮
    private Button button_sure;

    private OnClikListener onClikListener;

    private Context context;

    public static IdentifyAlertView getInstance(Context context, OnClikListener onClikListener) {
        return new IdentifyAlertView(context, onClikListener);
    }

    private IdentifyAlertView(Context context, OnClikListener onClikListener) {
        getDecorView(context, onClikListener);
    }

    private void getDecorView(Context context, OnClikListener onClikListener) {
        this.context = context;
        this.onClikListener = onClikListener;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_identify_view, null);
        view.setOnClickListener(this);
        button_sure = (Button)view.findViewById(R.id.button_sure);
        button_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_sure) {
            onClikListener.onSureClick();
        } else {
            onClikListener.onCancelClick();
        }
    }


    public interface OnClikListener {
        void onCancelClick();
        void onSureClick();
    }

    public View getView() {
        return view;
    }
}
