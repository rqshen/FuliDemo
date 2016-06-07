package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bcb.R;

/**
 * Created by Ray on 2016/6/6.
 *
 * @desc 聚爱申请成功
 */
public class Activity_Apply_Love_Success extends Activity_Base implements View.OnClickListener{

    //立即查看
    private Button check_button;


    public static void launch(Context context){
        Intent intent = new Intent(context, Activity_Apply_Love_Success.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_apply_love_success);
        setLeftTitleVisible(true);
        check_button = (Button) findViewById(R.id.check_button);
        check_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.check_button:
                Activity_My_Love.launch(this);
                break;
        }
    }
}
