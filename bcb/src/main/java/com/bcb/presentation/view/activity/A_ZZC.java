package com.bcb.presentation.view.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.idtk.smallchart.chart.PieChart;
import com.idtk.smallchart.data.PieData;
import com.idtk.smallchart.interfaces.iData.IPieData;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class A_ZZC extends Activity_Base {

	@BindView(R.id.pie) PieChart pieChart;

	@BindView(R.id.ye) TextView ye;
	@BindView(R.id.bj) TextView bj;
	@BindView(R.id.sy) TextView sy;
	@BindView(R.id.bzj) TextView bzj;

	@BindView(R.id.ljtz) TextView ljtz;
	@BindView(R.id.ljsy) TextView ljsy;
	@BindView(R.id.ljtcz) TextView ljtcz;
	@BindView(R.id.ljtx) TextView ljtx;

	@BindView(R.id.iv) ImageView iv;
	@BindView(R.id.lj) LinearLayout lj;
	private Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		setBaseContentView(R.layout.activity_zzc);
		ButterKnife.bind(this);
		setLeftTitleVisible(true);
		setTitleValue("总资产");
		initPieChart();
	}

	/**
	 * 环形图
	 */
	private void initPieChart() {
		//柱状图数据类
		int[] mColors = {0xFFCC0000, 0xFF00CC00, 0xFF0000CC, 0xFF888800, 0xFF880088, 0xFF008888,};
		String[] mNames = {"曲线图", "柱状图", "折线图", "组合图", "环形图", "雷达图",};
		ArrayList<IPieData> mPieDataList = new ArrayList<>();
		for (int i = 0; i < mColors.length; i++) {
			PieData pieData = new PieData();
			pieData.setColor(mColors[i]);
			pieData.setName(mNames[i]);
			pieData.setValue(new Random().nextInt(10) + 1);
			mPieDataList.add(pieData);
		}

		//柱状图绘制类
		pieChart.setDataList(mPieDataList);
		pieChart.setInsideRadiusScale(0.4f);//设置"透明扇形内半径与扇形半径"比，默认为0.5f，设为0则没有内半径
		pieChart.setOutsideRadiusScale(0.4f);//设置"透明扇形外半径与扇形半径"比，默认为0.6f，设为0则没有外半径
		pieChart.setOffsetRadiusScale(1.15f);//设置点击后偏移扇形的半径与扇形半径比，默认为1.1f
		pieChart.setStartAngle(-90);//设置扇形图的起始角度，默认为正东方，顺时针为正
		pieChart.setOffsetAngle(0);//设置扇形图之间的空隙角度，默认为1°
		pieChart.setMinAngle(18);//设置扇形图显示单个扇形区域百分比文本时必须大于的最小角度，默认为30(即占比为30/360=8.3%)
		pieChart.setDecimalPlaces(1);//设置显示百分比数值的需要设置小数位数，默认为0
		pieChart.setAnimatTime(1500);//设置显示动画的时间，默认为1500
		pieChart.setAxisLength(300);//设置扇形图半径，默认 radius = Math.min(mWidth, mHeight) * 0.4f，直径大于宽高中的最小值时取宽高中的最小值
		//pieChart.setAxisTextSize(16);//设置扇形图上文字大小
		pieChart.setAxisColor(Color.WHITE);//设置扇形图上文字颜色
	}
}
