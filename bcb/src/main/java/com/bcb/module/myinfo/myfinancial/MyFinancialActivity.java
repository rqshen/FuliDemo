package com.bcb.module.myinfo.myfinancial;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.constant.ProjectListType;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.MyFinancialStateFragment;
import com.bcb.util.MyActivityManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ruiqin.shen
 * 类说明：我的理财
 */
public class MyFinancialActivity extends FragmentActivity {
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.wyb)
    TextView wyb;
    @BindView(R.id.tv_zyb)
    TextView tvZyb;
    MyFinancialStateFragment wyb_f;//稳盈宝
    MyFinancialStateFragment zyb_f;//周盈宝

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushOneActivity(MyFinancialActivity.this);
        setContentView(R.layout.activity_tzjl);
        ButterKnife.bind(this);// ButterKnife.inject(this) should be called after setContentView()
        changeColorAndShowPage(0);
    }

    @OnClick({R.id.iv_left, R.id.wyb, R.id.tv_zyb})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.wyb://稳赢宝
                changeColorAndShowPage(0);
                break;
            case R.id.tv_zyb://周盈宝
                changeColorAndShowPage(1);
                break;
        }
    }

    int lastClickPosition = -1;//上一次点击的位置

    private void changeColorAndShowPage(int posision) {
        if (lastClickPosition != posision) {//点击不重复，响应点击事件
            recoverLastTab(); //还原上一个位置的字体颜色 和背景
            changeCurrentTab(posision);//更改当前位置的字体颜色和背景
            lastClickPosition = posision;//将上一个位置的值更新
        }
    }

    /**
     * 恢复上一个位置的颜色
     */
    private void recoverLastTab() {
        switch (lastClickPosition) {
            case 0:
                wyb.setTextColor(ContextCompat.getColor(this, R.color.white));
                wyb.setBackground(null);
                if (wyb_f != null && !wyb_f.isHidden()) {
                    getSupportFragmentManager().beginTransaction().hide(wyb_f).commit();
                }
                break;
            case 1:
                tvZyb.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvZyb.setBackground(null);
                if (zyb_f != null && !zyb_f.isHidden()) {
                    getSupportFragmentManager().beginTransaction().hide(zyb_f).commit();
                }
                break;
        }
    }

    /**
     * 更改当前点击的颜色
     */
    private void changeCurrentTab(int currentClickPosition) {
        switch (currentClickPosition) {
            case 0://稳盈宝选中
                wyb.setTextColor(ContextCompat.getColor(this, R.color.red));
                wyb.setBackground(ContextCompat.getDrawable(this, R.drawable.stroke_l));
                if (wyb_f == null) {
                    wyb_f = MyFinancialStateFragment.newInstance(ProjectListType.MONTH);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, wyb_f, "AA").commit();
                } else {
                    getSupportFragmentManager().beginTransaction().show(wyb_f).commit();
                }
                break;
            case 1://周盈宝选中
                tvZyb.setTextColor(ContextCompat.getColor(this, R.color.red));
                tvZyb.setBackground(ContextCompat.getDrawable(this, R.drawable.stroke_r));
                if (zyb_f == null) {
                    zyb_f = MyFinancialStateFragment.newInstance(ProjectListType.DAY);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, zyb_f, "AA").commit();
                } else {
                    getSupportFragmentManager().beginTransaction().show(zyb_f).commit();
                }
                break;
        }
    }

}