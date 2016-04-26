package com.bcb.presentation.view.custom.CustomDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bcb.R;

/**
 * Created by cain on 16/2/1.
 */
public class DialogWidget extends Dialog {
    Context activity;
    private View view;
    private boolean isOutSideTouch = true;
    private boolean isMaskEffect = false;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public boolean isOutSideTouch() {
        return isOutSideTouch;
    }

    public void setOutSideTouch(boolean isOutSideTouch) {
        this.isOutSideTouch = isOutSideTouch;
    }

    public DialogWidget(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }
    public DialogWidget(Context context) {
        this(context, 0);
        // TODO Auto-generated constructor stub
    }
    public DialogWidget(Context activity, View view) {
        super(activity, R.style.PasswdDialog);
        this.activity = activity;
        this.view=view;
    }
    public DialogWidget(Context activity, View view, boolean isMaskEffect) {
        super(activity, R.style.PasswdDialog);
        this.activity = activity;
        this.view = view;
        this.isMaskEffect = isMaskEffect;
    }
    public DialogWidget(Activity activity, View view,int theme) {
        super(activity,theme);
        this.activity = activity;
        this.view = view;
    }
    public DialogWidget(Activity activity, View view,int theme,boolean isOutSide) {
        super(activity,theme);
        this.activity = activity;
        this.view = view;
        setOutSideTouch(isOutSide);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(view);
        setCanceledOnTouchOutside(isOutSideTouch);

        Window win = this.getWindow();
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置蒙版效果
        if (isMaskEffect) {
            win.getDecorView().setPadding(0, 0, 0, 0);
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.alpha = 0.8f;
        } else {
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        win.setAttributes(lp);

        this.setContentView(view);
    }
}
