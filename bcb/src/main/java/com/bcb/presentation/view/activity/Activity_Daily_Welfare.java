package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.bcb.R;
import com.bcb.data.util.UIUtil;
import com.bcb.data.util.UmengUtil;
import com.dg.spinnerwheel.WheelVerticalView;
import com.dg.spinnerwheel.adapters.ArrayWheelAdapter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ray on 2016/5/10.
 *
 * @desc 每日福利
 */
public class Activity_Daily_Welfare extends Activity_Base {
    private ImageView img_background;//整个动画加背景
    private AnimationDrawable welfare_animationDrawable;//动画图片

    //滚屏文字
    private WheelVerticalView value_scroll;
    //滚屏计时器
    private Timer adTimer;
    private TimerTask adTimerTask;
    //是否停止滚动
    private boolean isPause;
    private ArrayWheelAdapter<String> adapter;

    private static final int SCROLL = 100;//滚屏文字

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_daily_welfare);

        //设置系统状态栏背景色
        UIUtil.initSystemBar(this, R.color.red);

        setTitleValue("每日福利");

        setLeftTitleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengUtil.eventById(Activity_Daily_Welfare.this, R.string.welfare_back);
                finish();
            }
        });

        //初始化背景动画
        img_background = (ImageView) findViewById(R.id.img_background);
        img_background.setImageResource(R.drawable.daily_welfare_frame);
        welfare_animationDrawable = (AnimationDrawable) img_background.getDrawable();

        //文字滚动
        value_scroll  = (WheelVerticalView) findViewById(R.id.value_scroll);
        isPause = false;
        startRotate();

    }

    /**
     * 初始化并开始滚动文字
     */
    private void startRotate(){
        String[] rotateValues = getResources().getStringArray(R.array.rotateValues);
        if (!this.isFinishing()){
            adapter = new ArrayWheelAdapter<>(this, rotateValues);
            adapter.setTextGravity(Gravity.CENTER);
            adapter.setTextSize(12);
            adapter.setTextColor(getResources().getColor(R.color.red));
            adapter.setTextTypeface(Typeface.DEFAULT);
            value_scroll.setVisibleItems(5);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (null != welfare_animationDrawable){
            welfare_animationDrawable.start();
        }
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
        if (null != welfare_animationDrawable){
            welfare_animationDrawable.stop();
        }
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
     * @param ctx
     */
    public static void launche(Context ctx){
        Intent intent = new Intent(ctx, Activity_Daily_Welfare.class);
        ctx.startActivity(intent);
    }
}
