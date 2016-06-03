package com.bcb.presentation.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.presentation.view.activity.Activity_Apply_Love;

/**
 * Created by Ray on 2016/6/2.
 *
 * @desc 聚爱
 */
public class Frag_Love extends Frag_Base implements View.OnClickListener{

    //标题
    private TextView title_text, right_text;
    private Context ctx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_love, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.ctx = view.getContext();
        super.onViewCreated(view, savedInstanceState);
        //标题
        title_text = (TextView) view.findViewById(R.id.title_text);
        title_text.setText("聚爱");

        right_text = (TextView) view.findViewById(R.id.right_text);
        right_text.setText("我要申请");
        right_text.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.right_text:
                Activity_Apply_Love.launch(ctx);
                break;
        }
    }
}
