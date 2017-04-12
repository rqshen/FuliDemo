package com.bcb.module.myinfo.financial;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.utils.MyActivityManager;
import com.bcb.module.myinfo.financial.financialstate.FinancialStateFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 投资理财
 */
public class FinancialActivity extends FragmentActivity {
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.wyb)
    TextView wyb;
    @BindView(R.id.zyb)
    TextView zyb;
    FinancialStateFragment wyb_f;
    FinancialStateFragment zxb_f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushOneActivity(FinancialActivity.this);
        setContentView(R.layout.activity_tzjl);
        ButterKnife.bind(this);// ButterKnife.inject(this) should be called after setContentView()
        showWYB();
    }

    @OnClick({R.id.iv_left, R.id.wyb, R.id.zyb})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.wyb://稳赢宝
                showWYB();
                break;
            case R.id.zyb://涨薪宝
                showZYB();
                break;
        }
    }

    private void showZYB() {
        zyb.setBackground(getResources().getDrawable(R.drawable.stroke_r));
        wyb.setBackground(null);
        wyb.setTextColor(getResources().getColor(R.color.white));
        zyb.setTextColor(getResources().getColor(R.color.red));
        if (zxb_f == null) {
            zxb_f = FinancialStateFragment.newInstance(1);
            getSupportFragmentManager().beginTransaction().add(R.id.container, zxb_f, "AA").commit();
        } else {
            hideFrags();
            getSupportFragmentManager().beginTransaction().show(zxb_f).commit();
        }
    }

    /**
     * 稳盈宝
     */
    private void showWYB() {
        wyb.setTextColor(getResources().getColor(R.color.red));
        zyb.setTextColor(getResources().getColor(R.color.white));
        wyb.setBackground(getResources().getDrawable(R.drawable.stroke_l));
        zyb.setBackground(null);
        if (wyb_f == null) {
            wyb_f = FinancialStateFragment.newInstance(0);
            getSupportFragmentManager().beginTransaction().add(R.id.container, wyb_f, "AA").commit();
        } else {
            hideFrags();
            getSupportFragmentManager().beginTransaction().show(wyb_f).commit();
        }
    }

    private void hideFrags() {
        if (wyb_f != null && !wyb_f.isHidden())
            getSupportFragmentManager().beginTransaction().hide(wyb_f).commit();
        if (zxb_f != null && !zxb_f.isHidden())
            getSupportFragmentManager().beginTransaction().hide(zxb_f).commit();
    }
}