package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bcb.R;

/**
 * Created by Ray on 2016/6/2.
 *
 * @desc 聚爱申请
 */
public class Activity_Apply_Love extends Activity_Base{

    //募捐天数
    private SeekBar seekBar;
    private TextView limit_date;

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

        limit_date = (TextView) findViewById(R.id.limit_date);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                limit_date.setText(String.valueOf(progress + 3));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
