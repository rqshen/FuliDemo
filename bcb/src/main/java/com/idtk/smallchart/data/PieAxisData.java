package com.idtk.smallchart.data;

import android.graphics.RectF;

import com.idtk.smallchart.interfaces.iData.IPieAxisData;

import java.util.Arrays;

/**
 * Created by Idtk on 2016/6/22.
 * Blog : http://www.idtkm.com
 * GitHub : https://github.com/Idtk
 * 描述 : 扇形图坐标系数据类
 * Center : name、textSize、color、width、isText
 * Round : in/out/offsetRadiusScale、startAngle、minAngle、decimal
 */
public class PieAxisData extends ChartData implements IPieAxisData {

	private float insideRadiusScale = 0.5f;//"透明扇形内半径与扇形半径"比
	private float outsideRadiusScale = 0.6f;//"透明扇形外半径与扇形半径"比
	private float offsetRadiusScale = 1.1f;//点击后偏移扇形的半径与扇形半径比
	private float startAngle = 0;//扇形图的起始角度，顺时针为正
	private float offsetAngle = 1.0f;//扇形图之间的空隙角度
	private float minAngle = 30;//扇形图显示单个扇形区域百分比文本时必须大于的最小角度
	private int decimalPlaces = 0;//显示百分比数值的需要设置小数位数
	private long animatTime = 1500;//显示动画的时间
	private float axisLength;//扇形图半径，默认 radius = Math.min(mWidth, mHeight) * 0.4f

	//以下是中间变量
	private RectF[] rectFs;//正常的扇形图的矩形、透明扇形图内外矩形
	private RectF[] offsetRectFs;//偏移的扇形图的矩形、透明扇形图内外矩形
	private float[] startAngles;//扇形区域的起始角度集合

	@Override
	public float getOffsetAngle() {
		return offsetAngle;
	}

	@Override
	public void setOffsetAngle(float offsetAngle) {
		this.offsetAngle = offsetAngle;
	}

	@Override
	public long getAnimatTime() {
		return animatTime;
	}

	@Override
	public void setAnimatTime(long animatTime) {
		this.animatTime = animatTime;
	}

	@Override
	public float getInsideRadiusScale() {
		return insideRadiusScale;
	}

	@Override
	public void setInsideRadiusScale(float insideRadiusScale) {
		this.insideRadiusScale = insideRadiusScale;
	}

	@Override
	public float getOutsideRadiusScale() {
		return outsideRadiusScale;
	}

	@Override
	public void setOutsideRadiusScale(float outsideRadiusScale) {
		this.outsideRadiusScale = outsideRadiusScale;
	}

	@Override
	public float getOffsetRadiusScale() {
		return offsetRadiusScale;
	}

	@Override
	public void setOffsetRadiusScale(float offsetRadiusScale) {
		this.offsetRadiusScale = offsetRadiusScale;
	}

	@Override
	public float getStartAngle() {
		return startAngle;
	}

	@Override
	public void setStartAngle(float startAngle) {
		this.startAngle = startAngle;
	}

	@Override
	public float getMinAngle() {
		return minAngle;
	}

	@Override
	public void setMinAngle(float minAngle) {
		this.minAngle = minAngle;
	}

	@Override
	public int getDecimalPlaces() {
		return decimalPlaces;
	}

	@Override
	public void setDecimalPlaces(int decimalPlaces) {
		this.decimalPlaces = decimalPlaces;
	}

	@Override
	public float[] getStartAngles() {
		return startAngles;
	}

	@Override
	public void setStartAngles(float[] startAngles) {
		this.startAngles = startAngles;
	}

	@Override
	public RectF[] getRectFs() {
		return rectFs;
	}

	@Override
	public void setRectFs(RectF[] rectFs) {
		this.rectFs = rectFs;
	}

	@Override
	public RectF[] getOffsetRectFs() {
		return offsetRectFs;
	}

	@Override
	public void setOffsetRectFs(RectF[] offsetRectFs) {
		this.offsetRectFs = offsetRectFs;
	}

	@Override
	public float getAxisLength() {
		return axisLength;
	}

	@Override
	public void setAxisLength(float axisLength) {
		this.axisLength = axisLength;
	}

	@Override
	public String toString() {
		return "PieAxisData{" + "insideRadiusScale=" + insideRadiusScale + ", outsideRadiusScale=" + outsideRadiusScale + ", offsetRadiusScale="
				+ offsetRadiusScale + ", startAngle=" + startAngle + ", minAngle=" + minAngle + ", decimalPlaces=" + decimalPlaces + ", startAngles="
				+ Arrays.toString(startAngles) + ", rectFs=" + Arrays.toString(rectFs) + ", offsetRectFs=" + Arrays.toString(offsetRectFs) + ", axisLength="
				+ axisLength + '}';
	}
}
