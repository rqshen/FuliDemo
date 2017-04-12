package com.bcb.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;

import com.bcb.R;
import com.dg.spinnerwheel.AbstractWheel;
import com.dg.spinnerwheel.adapters.ArrayWheelAdapter;

/**
 * Created by Ray on 2016/4/29.
 *
 * @desc
 */
public class SpinnerWheelUtil {

    private static SpinnerWheelUtil spinnerWheelUtil;

    public static SpinnerWheelUtil getInstance(){
        if (null == spinnerWheelUtil){
            spinnerWheelUtil = new SpinnerWheelUtil();
        }
        return spinnerWheelUtil;
    }


    public interface OnDoneClickListener{
        void onClick(int currentItem);
    }

    /**
     * 初始化底部弹出的SpinnerWheelDialog
     * @param context
     * @param types
     * @param currItem
     * @param doneClickListener
     */
    public void initSpinnerWheelDialog(Context context, String[] types, int currItem, final OnDoneClickListener doneClickListener) {
        View view = View.inflate(context, R.layout.layout_common_wheel, null);
        final AbstractWheel wheel = (AbstractWheel) view.findViewById(R.id.wheel);
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<>(context, types);
        adapter.setTextSize(20);
        adapter.setTextColor(context.getResources().getColor(R.color.txt_black));
        wheel.setViewAdapter(adapter);
        wheel.setCurrentItem(currItem);
        wheel.setVisibleItems(3);
        wheel.setCyclic(false);
        wheel.setInterpolator(new AnticipateOvershootInterpolator());
        final Dialog dialog = getActionSheet(context, view, Gravity.BOTTOM);
        view.findViewById(R.id.action_sheet_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.action_sheet_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneClickListener.onClick(wheel.getCurrentItem());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * action sheet dialog
     *
     * @param context
     * @param view
     * @return
     */
    private Dialog getActionSheet(Context context, View view, int gravity) {
        final Dialog dialog = new Dialog(context, R.style.action_sheet);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int screen[] = getScreenSize(context);
        lp.width = screen[0];
        lp.height = screen[1]/3;
        window.setGravity(gravity); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.action_sheet_animation); // 添加动画
        return dialog;
    }

    /**
     * ScreenSize
     * @param context
     * @return
     */
    private int[] getScreenSize(Context context) {
        int[] screenSize = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenSize[0] = dm.widthPixels;
        screenSize[1] = dm.heightPixels;
        return screenSize;
    }
}
