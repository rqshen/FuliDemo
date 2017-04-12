package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.module.homepager.morefinance.MoreFinanceActivity;
import com.bcb.utils.UmengUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.dg.spinnerwheel.WheelVerticalView;
import com.dg.spinnerwheel.adapters.ArrayWheelAdapter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ray on 2016/6/7.
 *
 * @desc 当天已经领取每日福利的结果页面
 */
public class Activity_Daily_Welfare_Static extends Activity_Base implements View.OnClickListener{

    private Context ctx;
    private TextView welfare_value, welfare_totalInterest;

    //滚屏文字
    private WheelVerticalView value_scroll;
    //滚屏计时器
    private Timer adTimer;
    private TimerTask adTimerTask;
    //是否停止滚动
    private boolean isPause;
    private ArrayWheelAdapter<String> adapter;

    private static final int SCROLL = 100;//滚屏文字

    private TextView join_count;//参与人数
    private String[] rotateValues;//滚动内容

    /**
     * @param ctx
     * @param welfare_value 加息值
     * @param totalInterest 累计收益
     * @param joinCount     参与人数
     * @param rotateValues  滚屏文字
     */
    public static void launche(Context ctx, String welfare_value, String totalInterest, String joinCount, String[] rotateValues){
        Intent intent = new Intent(ctx, Activity_Daily_Welfare_Static.class);
        intent.putExtra("welfare_value", welfare_value);
        intent.putExtra("totalInterest", totalInterest);
        intent.putExtra("join_count", joinCount);
        intent.putExtra("rotateValues", rotateValues);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_welfare_static);
        ctx = this;
        setLeftTitleVisible(true);
        setLeftTitleListener(this);
        setTitleValue("每日福利");
        welfare_value = (TextView) findViewById(R.id.welfare_value);
        String welfare = getIntent().getStringExtra("welfare_value");
        welfare_value.setText(welfare);
        String totalInterest = getIntent().getStringExtra("totalInterest");
        welfare_totalInterest = (TextView) findViewById(R.id.welfare_totalInterest);
        welfare_totalInterest.setText(totalInterest);

        //马上投资
        findViewById(R.id.btn_welfare_check).setOnClickListener(this);
        //累计收益
        findViewById(R.id.ll_text).setOnClickListener(this);
        //活动规则按钮
        findViewById(R.id.btn_welfare_rule).setOnClickListener(this);

        //初始化滚动文字
        rotateValues = getIntent().getStringArrayExtra("rotateValues");
        //文字滚动
        value_scroll = (WheelVerticalView) findViewById(R.id.value_scroll);
        isPause = false;
        startRotate();

        join_count = (TextView) findViewById(R.id.join_count);
        String joinCount = getIntent().getStringExtra("join_count");
        if (!TextUtils.isEmpty(joinCount)){
            join_count.setText(joinCount);
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SCROLL:
                    value_scroll.scroll(1, 1000);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //恢复滚屏
        if (isPause){
            if (null == adTimer){
                adTimer = new Timer();
                adTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(SCROLL);
                    }
                };
                adTimer.schedule(adTimerTask, 1000, 2000);
            }
            isPause = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //关闭滚屏定时器
        if (adTimerTask != null)
            adTimerTask.cancel();
        if (adTimer != null){
            adTimer.cancel();
            adTimer.purge();
            adTimer = null;
            isPause = true;
        }
    }

    /**
     * 初始化并开始滚动文字
     */
    private void startRotate(){
        if (!this.isFinishing()){
            adapter = new ArrayWheelAdapter<>(this, rotateValues);
            adapter.setTextGravity(Gravity.CENTER);
            adapter.setTextSize(12);
            adapter.setTextColor(getResources().getColor(R.color.txt_middle_gray));
            adapter.setTextTypeface(Typeface.DEFAULT);
            value_scroll.setVisibleItems(3);
            value_scroll.setViewAdapter(adapter);
            value_scroll.setCurrentItem(0);
            value_scroll.setCyclic(true);
            value_scroll.setEnabled(false);

            if (null == adTimer){
                adTimer = new Timer();
                adTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(SCROLL);
                    }
                };
                adTimer.schedule(adTimerTask, 1000, 2000);
            }
        }
    }

    /**
     *显示活动规则对话框
     */
    private void showRuleDialog(){
        final AlertView dialog = new AlertView(this, R.style.alertviewstyle);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_welfare_rule, null);
        layout.findViewById(R.id.dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_text://跳转到投资记录
                UmengUtil.eventById(ctx, R.string.self_tzjl);
                Activity_Money_Flowing_Water.launche(ctx);
//                finish();
                break;
            case R.id.btn_welfare_check://跳转到首页产品列表
                //EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.PRODUCT));
                startActivity(new Intent(ctx, MoreFinanceActivity.class));
                finish();
                break;
            case R.id.btn_welfare_rule://活动规则
                showRuleDialog();
                break;
            case R.id.back_img:
                finish();
                break;
        }

    }
}
