package com.bcb.module.myinfo.welfare;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.module.discover.financialproduct.InvestmentFinanceActivity;
import com.bcb.module.myinfo.balance.trading.TradingRecordActivity;
import com.bcb.util.MyActivityManager;
import com.bcb.util.UmengUtil;

/**
 * Created by ruiqin.shen
 * 类说明：每日福利点击 后的结果
 */
public class DailyWelfareResultActivity extends Activity_Base implements View.OnClickListener {

    private RelativeLayout coin;//弹跳金币
    private LinearLayout ll_text;//按钮下面文本
    private TextView btn_welfare_check;//立即查看
    private ImageView activity_close;//关闭按钮
    private TextView welfare_value;//增加的收益
    private TextView welfare_totalInterest;//累计收益

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

        coin = (RelativeLayout) findViewById(R.id.coin);

        ll_text = (LinearLayout) findViewById(R.id.ll_text);
        ll_text.setOnClickListener(this);
        btn_welfare_check = (TextView) findViewById(R.id.btn_welfare_check);
        btn_welfare_check.setOnClickListener(this);
        welfare_value = (TextView) findViewById(R.id.welfare_value);
        String value = getIntent().getStringExtra("welfare_value");
        if (!TextUtils.isEmpty(value)) {
            welfare_value.setText(value + "%");
        }
        welfare_totalInterest = (TextView) findViewById(R.id.welfare_totalInterest);
        String totalInterest = getIntent().getStringExtra("totalInterest");
        if (!TextUtils.isEmpty(totalInterest)) {
            welfare_totalInterest.setText(totalInterest);
        }

        activity_close = (ImageView) findViewById(R.id.activity_close);
        activity_close.setOnClickListener(this);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 0.5f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 0.5f, 1f);
        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", 0, -height / 5);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(coin, scaleX, scaleY, translationY);
        objectAnimator.setInterpolator(new BounceInterpolator());
        objectAnimator.setDuration(1200);
        objectAnimator.start();
    }

    /**
     * 启动
     *
     * @param ctx
     * @param value         福利值
     * @param totalInterest 累计收益
     */
    public static void launche(Context ctx, String value, String totalInterest) {
        Intent intent = new Intent(ctx, DailyWelfareResultActivity.class);
        intent.putExtra("welfare_value", value);
        intent.putExtra("totalInterest", totalInterest);
        ctx.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.sendEmptyMessageDelayed(destroy, 50);
    }

    //仅用于界面延迟销毁
    private final int destroy = 100;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case destroy:
                    MyActivityManager.getInstance().finishAllActivity();
                    overridePendingTransition(0, 0);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_text://跳转到投资记录
                UmengUtil.eventById(ctx, R.string.self_tzjl);
                handler.sendEmptyMessage(destroy);
                TradingRecordActivity.launche(ctx);
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.btn_welfare_check://跳转到首页产品列表
                //EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.PRODUCT));
                startActivity(new Intent(ctx, InvestmentFinanceActivity.class));
                finish();
                overridePendingTransition(0, 0);
                //handler.sendEmptyMessageDelayed(destroy, 50);
                break;
            case R.id.activity_close:
                finish();
                overridePendingTransition(0, 0);
                handler.sendEmptyMessageDelayed(destroy, 50);
                break;
        }
    }
}
