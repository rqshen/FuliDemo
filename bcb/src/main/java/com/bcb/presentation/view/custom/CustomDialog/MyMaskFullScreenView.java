package com.bcb.presentation.view.custom.CustomDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;

/**
 * Created by cain on 16/2/3.
 */
public class MyMaskFullScreenView implements View.OnClickListener {
    //界面
    private View view;
    //上下文
    private Context context;
    //整体界面
    private LinearLayout layout_view;
    //主标题
    private TextView mainTitle;
    //副标题
    private TextView subTitle;
    //点击监听器
    private OnClikListener onClikListener;

    /**
     * 只含标题的构造函数
     *
     * @param context   上下文
     * @param mainText  主标题
     * @param titleStatus   标题状态
     * @param onClikListener    监听器
     */
    private MyMaskFullScreenView(Context context,    //上下文
                         String mainText,    //主标题
                         boolean titleStatus,    //标题状态
                         OnClikListener onClikListener) {
        if (titleStatus) {
            getDecorView(context, mainText, "", onClikListener);
        } else {
            getDecorView(context, "", mainText, onClikListener);
        }
    }

    /**
     * 只含有主标题的构造函数
     *
     * @param context   上下文
     * @param mainText  主标题
     * @param onClikListener    监听器
     * @return  新的MyMaskFullScreenView对象
     */
    public static MyMaskFullScreenView getInstance(Context context,
                                           String mainText,
                                           OnClikListener onClikListener) {
        return new MyMaskFullScreenView(context, mainText, true, onClikListener);
    }

    /**
     * 自由设置只创建主标题还是副标题
     *
     * @param context       上下文
     * @param titleText     标题
     * @param isMainText    是否创建主标题
     * @param onClikListener    监听器
     * @return  新的MyMaskFullScreenView对象
     */
    public static MyMaskFullScreenView getInstance(Context context,
                                                   String titleText,
                                                   boolean isMainText,
                                                   OnClikListener onClikListener) {
        return new MyMaskFullScreenView(context, titleText, isMainText, onClikListener);
    }


    /**
     * 带主标题和副标题的构造函数
     * @param context
     * @param mainText
     * @param subText
     * @param onClikListener
     */
    private MyMaskFullScreenView(Context context,    //上下文
                                 String mainText,    //主标题
                                 String subText,    //副标题
                                 OnClikListener onClikListener) {
        getDecorView(context, mainText, subText, onClikListener);
    }

    /**
     * 创建主标题和副标题的getInstance函数
     * @param context
     * @param mainText
     * @param subText
     * @param onClikListener
     * @return
     */
    public static MyMaskFullScreenView getInstance(Context context,
                                                   String mainText,
                                                   String subText,
                                                   OnClikListener onClikListener) {
        return new MyMaskFullScreenView(context, mainText, subText, onClikListener);
    }

    //默认初始化函数
    private void getDecorView(Context context, String mainText, String subText, OnClikListener onClikListener) {
        this.context = context;
        this.onClikListener = onClikListener;
        //获取所在的View
        view = LayoutInflater.from(context).inflate(R.layout.dialog_mask_view, null);
        view.setOnClickListener(this);
        //整体界面
        layout_view = (LinearLayout) view.findViewById(R.id.layout_view);
        layout_view.setOnClickListener(this);
        //主标题
        mainTitle = (TextView) view.findViewById(R.id.main_title);
        if (mainText.equalsIgnoreCase("null") || mainText.equalsIgnoreCase("")) {
            mainTitle.setVisibility(View.GONE);
        } else {
            mainTitle.setVisibility(View.VISIBLE);
            mainTitle.setText(mainText);
        }
        //副标题
        subTitle = (TextView) view.findViewById(R.id.sub_title);
        if (subText.equalsIgnoreCase("null") || subText.equalsIgnoreCase("")) {
            subTitle.setVisibility(View.GONE);
        } else {
            subTitle.setVisibility(View.VISIBLE);
            subTitle.setText(subText);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击View界面
            case R.id.layout_view:
                onClikListener.onViewClik();
                break;
        }
    }


    public interface OnClikListener {
        void onViewClik();
    }

    public View getView() {
        return view;
    }
}
