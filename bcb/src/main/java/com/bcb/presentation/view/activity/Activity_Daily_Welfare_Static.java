package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.event.BroadcastEvent;
import com.bcb.data.util.UmengUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by Ray on 2016/6/7.
 *
 * @desc 当天已经领取每日福利的结果页面
 */
public class Activity_Daily_Welfare_Static extends Activity_Base implements View.OnClickListener{

    private Context ctx;
    private TextView welfare_value, welfare_totalInterest,btn_welfare_check;
    private LinearLayout ll_text;//按钮下面文本

    /**
     * 启动
     * @param ctx
     */
    public static void launche(Context ctx, String welfare_value, String totalInterest){
        Intent intent = new Intent(ctx, Activity_Daily_Welfare_Static.class);
        intent.putExtra("welfare_value", welfare_value);
        intent.putExtra("totalInterest", totalInterest);
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
        btn_welfare_check = (TextView) findViewById(R.id.btn_welfare_check);
        btn_welfare_check.setOnClickListener(this);
        ll_text = (LinearLayout) findViewById(R.id.ll_text);
        ll_text.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_text://跳转到投资记录
                UmengUtil.eventById(ctx, R.string.self_tzjl);
                Activity_Money_Flowing_Water.launche(ctx);
                finish();
                break;
            case R.id.btn_welfare_check://跳转到首页产品列表
                EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.PRODUCT));
                finish();
                break;
            case R.id.back_img:
                finish();
                break;
        }

    }
}
