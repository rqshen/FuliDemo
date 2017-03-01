package com.bcb.presentation.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.bcb.R;
import com.bcb.data.util.DensityUtils;

public class HorizontalProgressBar extends ProgressBar {
	protected Paint mPaint = new Paint();
	/**中间文字颜色*/
	protected int mTextColor = 0XFFFC00D1;
	/**中间文字大小*/
	protected int mTextSize = DensityUtils.sp2px(getContext(), 10);
	/**是否显示中间的文字*/
	protected boolean mIfDrawText = true;
	/**中间文字和左右两边进度之间的距离*/
	protected int mTextOffset = DensityUtils.dp2px(getContext(), 5);
	/**已完成部分进度的线宽*/
	protected int mReachedPBHeight = DensityUtils.dp2px(getContext(), 2);
	/**已完成部分进度的颜色*/
	protected int mReachedBarColor = 0XFFFC00D1;
	/**未完成部分进度的线宽*/
	protected int mUnReachedPBHeight = DensityUtils.dp2px(getContext(), 2);
	/**未完成部分进度的颜色*/
	protected int mUnReachedBarColor = 0xFFd3d6da;

	public HorizontalProgressBar(Context context) {
		this(context, null, 0);
	}

	public HorizontalProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint.setTextSize(mTextSize);
		mPaint.setColor(mTextColor);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		saveAttributeData(attrs, defStyle);
	}

	//******************************************************************************************
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//宽不支持使用wrap_content，要不然我也不知道到底该设为多少
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//canvas.save();//先保存目前画纸的位置，画完后调用restore方法返回到刚才保存的位置
		canvas.translate(0, getHeight() / 2);//移动画笔到初始位置

		String text = getProgress() + "%";//文字内容
		float textWidth = mPaint.measureText(text);//文字的长度

		int mProgressWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft() - 2 * mTextOffset - (int) textWidth;//进度条实际的长度
		float progressPosX = 1.0f * getProgress() / getMax() * mProgressWidth;//已完成的进度的长度

		//绘制左侧已完成的进度
		mPaint.setColor(mReachedBarColor);
		mPaint.setStrokeWidth(mReachedPBHeight);//设置线宽
		canvas.drawLine(getPaddingLeft(), 0, progressPosX, 0, mPaint);
		//绘制中间的文字
		if (mIfDrawText) {
			mPaint.setColor(mTextColor);
			canvas.drawText(text, progressPosX + mTextOffset, -(mPaint.descent() + mPaint.ascent()) / 2, mPaint);//注意文字Y轴的中心坐标的算法和文字的高度的算法大不相同
		}
		//绘制右侧未完成的进度
		mPaint.setColor(mUnReachedBarColor);
		mPaint.setStrokeWidth(mUnReachedPBHeight);
		canvas.drawLine(progressPosX + mTextOffset * 2 + textWidth, 0, getMeasuredWidth() - getPaddingRight(), 0, mPaint);
		//canvas.restore();//返回到刚才保存的位置
	}

	//******************************************************************************************

	/**测量高度，高度没考虑换行情况*/
	protected int measureHeight(int heightMeasureSpec) {
		int height = 0;
		switch (MeasureSpec.getMode(heightMeasureSpec)) {
			case MeasureSpec.EXACTLY:
				height = MeasureSpec.getSize(heightMeasureSpec);
				break;
			case MeasureSpec.AT_MOST:
			case MeasureSpec.UNSPECIFIED:
				float textHeight = (mPaint.descent() - mPaint.ascent());//字符高度
				float viewHeight = Math.max(Math.max(mReachedPBHeight, mUnReachedPBHeight), Math.abs(textHeight));
				height = getPaddingTop() + getPaddingBottom() + (int) viewHeight;
				break;
		}
		return height;
	}

	/**初始化属性集*/
	protected void saveAttributeData(AttributeSet attrs, int defStyle) {
		TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalPB, defStyle, 0);
		mTextColor = attributes.getColor(R.styleable.HorizontalPB_progress_text_color, 0XFFFC00D1);
		mTextSize = (int) attributes.getDimension(R.styleable.HorizontalPB_progress_text_size, mTextSize);
		mReachedBarColor = attributes.getColor(R.styleable.HorizontalPB_progress_reached_color, mTextColor);
		mUnReachedBarColor = attributes.getColor(R.styleable.HorizontalPB_progress_unreached_color, 0xFFd3d6da);
		mReachedPBHeight = (int) attributes.getDimension(R.styleable.HorizontalPB_progress_reached_bar_height, mReachedPBHeight);
		mUnReachedPBHeight = (int) attributes.getDimension(R.styleable.HorizontalPB_progress_unreached_bar_height, mUnReachedPBHeight);
		mTextOffset = (int) attributes.getDimension(R.styleable.HorizontalPB_progress_text_offset, mTextOffset);
		mIfDrawText = attributes.getBoolean(R.styleable.HorizontalPB_progress_text_visibility, true);
		attributes.recycle();
	}
}