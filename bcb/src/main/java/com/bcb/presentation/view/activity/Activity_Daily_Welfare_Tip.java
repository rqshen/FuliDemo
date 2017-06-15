package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bcb.R;
import com.bcb.base.old.Activity_Base;

/**
 * Created by Ray on 2016/5/13.
 *
 * @desc 不在时间段提示
 */
public class Activity_Daily_Welfare_Tip extends Activity_Base implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_welfare_tip);
        setLeftTitleVisible(true);
        setLeftTitleListener(this);
        setTitleValue("每日福利");
    }

    /**
     * 启动
     * @param ctx
     */
    public static void launche(Context ctx){
        Intent intent = new Intent(ctx, Activity_Daily_Welfare_Tip.class);
        ctx.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_img:
                finish();
                break;
        }
    }
}
