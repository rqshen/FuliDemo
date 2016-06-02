package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bcb.R;

/**
 * Created by Ray on 2016/6/2.
 *
 * @desc 聚爱申请
 */
public class Activity_Apply_Love extends Activity_Base{

    public static void launch(Context context){
        Intent intent = new Intent(context, Activity_Apply_Love.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_apply_love);
        setLeftTitleVisible(true);
        setTitleValue("聚爱");
    }
}
