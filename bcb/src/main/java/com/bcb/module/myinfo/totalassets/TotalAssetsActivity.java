package com.bcb.module.myinfo.totalassets;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.idtk.smallchart.chart.PieChart;
import com.idtk.smallchart.data.PieData;
import com.idtk.smallchart.interfaces.iData.IPieData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bcb.MyApplication.mUserWallet;

/**
 * Created by ruiqin.shen
 * 类说明：总资产
 */
public class TotalAssetsActivity extends Activity_Base {

    @BindView(R.id.pie)
    PieChart pieChart;
    @BindView(R.id.service_cz)
    TextView service_cz;
    @BindView(R.id.ye)
    TextView ye;
    @BindView(R.id.bj)
    TextView bj;
    @BindView(R.id.sy)
    TextView sy;
    @BindView(R.id.bzj)
    TextView bzj;
    @BindView(R.id.ljtz)
    TextView ljtz;
    @BindView(R.id.ljsy)
    TextView ljsy;
    @BindView(R.id.ljtcz)
    TextView ljtcz;
    @BindView(R.id.ljtx)
    TextView ljtx;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.lj)
    LinearLayout lj;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setBaseContentView(R.layout.activity_zzc);
        ButterKnife.bind(this);
        setLeftTitleVisible(true);
        setTitleValue("总资产");
        service_cz.setText(String.format("%.2f", mUserWallet.TotalAsset));
        ye.setText(String.format("%.2f", mUserWallet.BalanceAmount));
        bj.setText(String.format("%.2f", mUserWallet.InvestingAmount));
        sy.setText(String.format("%.2f", mUserWallet.LeftInterest));
        bzj.setText(String.format("%.2f", mUserWallet.SecurityAmount));
        ljtz.setText(String.format("%.2f", mUserWallet.InvestAmount));
        ljsy.setText(String.format("%.2f", mUserWallet.InvestIncome));
        ljtcz.setText(String.format("%.2f", mUserWallet.RechargeAmount));
        ljtx.setText(String.format("%.2f", mUserWallet.WithdrawAmount));
        initPieChart();
    }

    /**
     * 环形图
     */
    private void initPieChart() {
        //柱状图数据类
        ArrayList<IPieData> mPieDataList = new ArrayList<>();
        int[] mColors = {0xFFfb4977, 0xFFffc760, 0xFF6fe621, 0xFF4fccff, 0xffffffff, 0xfff8f8f8};//0xfff8f8f8
        String[] mNames = {"账户余额", "在投本金", "应计收益", "借款保证金", "", ""};
        float[] values = {0, 0, 0, 0, 0.01f, 0};

        values[0] = (float) MyApplication.mUserWallet.BalanceAmount;
        values[1] = (float) MyApplication.mUserWallet.InvestingAmount;
        values[2] = (float) MyApplication.mUserWallet.LeftInterest;
        values[3] = (float) MyApplication.mUserWallet.SecurityAmount;
        if (values[0] >= 0.01f) {
            mColors[4] = mColors[0];
        } else if (values[1] >= 0.01f) {
            mColors[4] = mColors[1];
        } else if (values[2] >= 0.01f) {
            mColors[4] = mColors[2];
        } else if (values[3] >= 0.01f) {
            mColors[4] = mColors[3];
        } else {
            mColors[4] = mColors[5];
            values[5] = 1;
        }
        for (int i = 0; i < mColors.length; i++) {
            PieData pieData = new PieData();
            pieData.setColor(mColors[i]);
            pieData.setName(mNames[i]);
            pieData.setValue(values[i]);
            mPieDataList.add(pieData);
        }

        //柱状图绘制类
        pieChart.setDataList(mPieDataList);
        pieChart.setInsideRadiusScale(0.4f);//设置"透明扇形内半径与扇形半径"比，默认为0.5f，设为0则没有内半径
        pieChart.setOutsideRadiusScale(0.4f);//设置"透明扇形外半径与扇形半径"比，默认为0.6f，设为0则没有外半径
        pieChart.setOffsetRadiusScale(1.0f);//设置点击后偏移扇形的半径与扇形半径比，默认为1.1f
        //pieChart.setStartAngle(-90);//设置扇形图的起始角度，默认为正东方，顺时针为正
        pieChart.setOffsetAngle(0);//设置扇形图之间的空隙角度，默认为1°
        pieChart.setMinAngle(18);//设置扇形图显示单个扇形区域百分比文本时必须大于的最小角度，默认为30(即占比为30/360=8.3%)
        pieChart.setDecimalPlaces(1);//设置显示百分比数值的需要设置小数位数，默认为0
        pieChart.setAnimatTime(1500);//设置显示动画的时间，默认为1500
        pieChart.setAxisLength(300);//设置扇形图半径，默认 radius = Math.min(mWidth, mHeight) * 0.4f，直径大于宽高中的最小值时取宽高中的最小值
        pieChart.setAxisTextSize(0);//设置扇形图上文字大小
        pieChart.setAxisColor(Color.WHITE);//设置扇形图上文字颜色
    }

    boolean isShow = true;

    @OnClick(R.id.iv)
    public void trans(View view) {
        if (isShow) {
            iv.setImageResource(R.drawable.zzc_up);
            lj.setVisibility(View.VISIBLE);
        } else {
            lj.setVisibility(View.GONE);
            iv.setImageResource(R.drawable.zzc_down);
        }
        isShow = !isShow;
    }
}
