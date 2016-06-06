package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.util.SpinnerWheelUtil;

/**
 * Created by Ray on 2016/6/2.
 *
 * @desc 聚爱申请
 */
public class Activity_Apply_Love extends Activity_Base implements View.OnClickListener{

    //募捐天数
    private SeekBar seekBar;
    private TextView limit_date;

    //目标金额
    private TextView tag_amount;
    //受助人与本人关系
    private LinearLayout ll_relationship;
    private TextView tv_relationship;
    private String[] relationships;
    private int index_relationship;

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

        //目标金额
        tag_amount = (TextView) findViewById(R.id.tag_amount);
        tag_amount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // 先判断输入框的数字是否正常，允许输入两个小数点
                String temp = tag_amount.getText().toString();
                int inputcount = 0, inputstart = 0;
                while ((inputstart = temp.indexOf(".", inputstart)) >= 0) {
                    inputstart += ".".length();
                    inputcount++;
                }
                //删除一开始就输入的小数点
                if (temp.indexOf(".") == 0 || inputcount > 1) {
                    s.delete(temp.indexOf("."), temp.length());
                }

                //只保留小数点后面两位小数
                if (temp.indexOf(".") > 0 && temp.length() - temp.indexOf(".") > 2) {
                    s.delete(temp.indexOf(".") + 3, temp.length());
                }

                //无法募捐1亿以上的金额
                if (temp.indexOf(".") <= 0 && temp.length() > 9) {
                    s.delete(9, temp.length());
                }
            }
        });

        //受助人与本人关系
        index_relationship = 0;
        relationships = getResources().getStringArray(R.array.relationship);
        ll_relationship = (LinearLayout) findViewById(R.id.ll_relationship);
        ll_relationship.setOnClickListener(this);
        tv_relationship = (TextView) findViewById(R.id.tv_relationship);

        //募捐天数
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_relationship:
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, relationships, index_relationship, new SpinnerWheelUtil.OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        index_relationship = currentItem;
                        tv_relationship.setText(relationships[currentItem]);
                    }
                });
                break;
        }
    }
}
