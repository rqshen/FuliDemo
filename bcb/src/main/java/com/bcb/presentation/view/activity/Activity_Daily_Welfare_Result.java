package com.bcb.presentation.view.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.event.MainActivityEvent;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.Animation.BounceInterpolator;

import de.greenrobot.event.EventBus;

/**
 * Created by Ray on 2016/5/11.
 *
 * @desc
 */
public class Activity_Daily_Welfare_Result extends Activity_Base implements View.OnClickListener{

    private LinearLayout coin;//弹跳金币
    private LinearLayout ll_text;//按钮下面文本
    private TextView btn_welfare_check;//立即查看
    private ImageView activity_close;//关闭按钮

    private Activity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉状态栏
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setAttributes(attrs);

        setContentView(R.layout.activity_daily_welfare_result);
        ctx = this;
        coin = (LinearLayout) findViewById(R.id.coin);

        ll_text = (LinearLayout) findViewById(R.id.ll_text);
        ll_text.setOnClickListener(this);
        btn_welfare_check = (TextView) findViewById(R.id.btn_welfare_check);
        btn_welfare_check.setOnClickListener(this);

        activity_close = (ImageView) findViewById(R.id.activity_close);
        activity_close.setOnClickListener(this);

        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 0.5f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 0.5f, 1f);
        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", 0, -height/5);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(coin, scaleX,scaleY,translationY);
        objectAnimator.setInterpolator(new BounceInterpolator());
        objectAnimator.setDuration(1200);
        objectAnimator.start();
    }

    /**
     * 启动
     * @param ctx
     */
    public static void launche(Context ctx){
        Intent intent = new Intent(ctx, Activity_Daily_Welfare_Result.class);
        ctx.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    //仅用于界面延迟销毁
    private final int destroy = 100;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case destroy:
                    MyActivityManager.getInstance().finishAllActivity();
                    overridePendingTransition(0, 0);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_text://跳转到投资记录
                UmengUtil.eventById(ctx, R.string.self_tzjl);
                Activity_Trading_Record.launche(ctx);
                finish();
                break;
            case R.id.btn_welfare_check://跳转到首页产品列表
                EventBus.getDefault().post(new MainActivityEvent(MainActivityEvent.PRODUCT));
                finish();
                overridePendingTransition(0, 0);
                handler.sendEmptyMessageDelayed(destroy, 50);
                break;
            case R.id.activity_close:
                finish();
                overridePendingTransition(0, 0);
                break;
        }
    }
}
