package com.bcb.presentation.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.bcb.R;
import com.bcb.data.util.DensityUtils;

public class RoundProgressBar extends HorizontalProgressBar {
	/**
	 * 半径
	 */
	private float mRadius = DensityUtils.sp2px(getContext(), 30);
	/**
	 * 两条线中线宽最大的那条线的线宽
	 */
	private int mMaxPaintWidth;

	public RoundProgressBar(Context context) {
		this(context, null, 0);
	}

	public RoundProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		saveAttributeData(attrs, defStyle);
	}

	@Override
	//这里默认在布局中padding值要么不设置，要么全部设置
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mMaxPaintWidth = Math.max(mReachedPBHeight, mUnReachedPBHeight);
		int expect = (int) mRadius * 2 + mMaxPaintWidth + getPaddingLeft() + getPaddingRight();
		int realWidth = Math.min(resolveSize(expect, widthMeasureSpec), resolveSize(expect, heightMeasureSpec));
		mRadius = (realWidth - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth) / 2;
		setMeasuredDimension(realWidth, realWidth);
//		setMeasuredDimension(resolveSize(50, widthMeasureSpec), resolveSize(50, heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		String text;
		if (getProgress() >= 100) {
			text = "售罄";
			mTextColor = 0xff999999;
		} else {
			text = getProgress() + "%";//文字内容
			mTextColor = 0xff4499f8;
		}

		float angle = 1.0f * getProgress() / getMax() * 360;//已完成的进度的长度

		//绘制完成的进度
		mPaint.setStyle(Style.STROKE);
		if (getProgress() < 100) mPaint.setColor(mReachedBarColor);
		else mPaint.setColor(mUnReachedBarColor);
		mPaint.setStrokeWidth(mReachedPBHeight);
		canvas.drawArc(getRectF(mReachedPBHeight), 0, angle, false, mPaint);
		//绘制未完成的进度
		mPaint.setColor(mUnReachedBarColor);
		mPaint.setStrokeWidth(mUnReachedPBHeight);
		canvas.drawArc(getRectF(mUnReachedPBHeight), angle, 360 - angle, false, mPaint);//当为false时为不经过圆心的弓形，为true时为经过圆心的扇形。起始角度、旋转角度，顺时针为正
		//绘制中间的文字
		if (mIfDrawText) {
			mPaint.setColor(mTextColor);
			mPaint.setStyle(Style.FILL);
			canvas.drawText(text, getMeasuredWidth() / 2 - mPaint.measureText(text) / 2, //
					getMeasuredHeight() / 2 - (mPaint.descent() + mPaint.ascent()) / 2, mPaint);
		}
	}

	/**
	 * 不管线宽是多少，均以最大的为准
	 */
	protected RectF getRectF(int lineWidth) {
		return new RectF(getMeasuredWidth() / 2 - mRadius + lineWidth / 2, getMeasuredHeight() / 2 - mRadius + lineWidth / 2, //left、top
				getMeasuredWidth() / 2 + mRadius - lineWidth / 2, getMeasuredHeight() / 2 + mRadius - lineWidth / 2);//right、bottom
	}

	/**
	 * 线宽比较短的中心线，以线宽比较大的中心线，为准
	 */
	protected RectF getRectF() {
		return new RectF(getMeasuredWidth() / 2 - mRadius + mMaxPaintWidth / 2, getMeasuredHeight() / 2 - mRadius + mMaxPaintWidth / 2, //left、top
				getMeasuredWidth() / 2 + mRadius - mMaxPaintWidth / 2, getMeasuredHeight() / 2 + mRadius - mMaxPaintWidth / 2);//right、bottom
	}

	@Override
	protected void saveAttributeData(AttributeSet attrs, int defStyle) {
		super.saveAttributeData(attrs, defStyle);
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RoundPB, defStyle, 0);
		mRadius = (int) ta.getDimension(R.styleable.RoundPB_progress_radius, mRadius);
		ta.recycle();
	}
}