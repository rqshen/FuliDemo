package com.bcb.presentation.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.BanksBean;
import com.bcb.presentation.adapter.BanksAdapter;
import com.bcb.presentation.view.custom.MyListView;

import java.util.ArrayList;

public class Activity_Charge_Tips extends Activity_Base {
    MyListView lv_banks;
    BanksAdapter adapter;
    ArrayList<BanksBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_charge_tips);
        lv_banks = (MyListView) findViewById(R.id.lv_banks);
        list = getIntent().getParcelableArrayListExtra("data");

        adapter = new BanksAdapter(Activity_Charge_Tips.this, list);
        lv_banks.setAdapter(adapter);
        TextView view = new TextView(Activity_Charge_Tips.this);
        view.setTextColor(Color.TRANSPARENT);
        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        view.setText("不在底部加个东西，会被压缩");
        lv_banks.addFooterView(view);
    }
}
