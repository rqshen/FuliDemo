package com.bcb.presentation.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.bcb.R;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/10/13 18:12
 */
public class HorizontalProgressBarWithNumber extends ProgressBar {
	private static final int DEFAULT_TEXT_SIZE = 10;
	private static final int DEFAULT_TEXT_COLOR = 0xFFdb3838;
	private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFf5f5f5;
	private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 3;
	private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 3;
	private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;
	protected Paint mPaint = new Paint();
	protected int mTextColor = DEFAULT_TEXT_COLOR;
	protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
	protected int mTextOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);
	protected int mReachedPBHeight = dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);
	protected int mReachedBarColor = DEFAULT_TEXT_COLOR;
	protected int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
	protected int mUnReachedPBHeight = dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
	protected int mRealWidth;
	/**
	 * 是否显示文字
	 */
	protected boolean mIfDrawText = false;

	public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalPB);
		mTextColor = attributes.getColor(R.styleable.HorizontalPB_progress_text_color, DEFAULT_TEXT_COLOR);
		mTextSize = (int) attributes.getDimension(R.styleable.HorizontalPB_progress_text_size, mTextSize);
		mReachedBarColor = attributes.getColor(R.styleable.HorizontalPB_progress_reached_color, mTextColor);
		mUnReachedBarColor = attributes.getColor(R.styleable.HorizontalPB_progress_unreached_color, DEFAULT_COLOR_UNREACHED_COLOR);
		mReachedPBHeight = (int) attributes.getDimension(R.styleable.HorizontalPB_progress_reached_bar_height, mReachedPBHeight);
		mUnReachedPBHeight = (int) attributes.getDimension(R.styleable.HorizontalPB_progress_unreached_bar_height,
				mUnReachedPBHeight);
		mTextOffset = (int) attributes.getDimension(R.styleable.HorizontalPB_progress_text_offset, mTextOffset);
		mIfDrawText = attributes.getBoolean(R.styleable.HorizontalPB_progress_text_visibility, false);
		attributes.recycle();
		mPaint.setTextSize(mTextSize);
		mPaint.setColor(mTextColor);
		mPaint.setAntiAlias(true);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);//宽没法使用wrap_content，要不然谁也不知道到底该设为多少
		int height = measureHeight(heightMeasureSpec);
		setMeasuredDimension(width, height);
		mRealWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			float textHeight = (mPaint.descent() - mPaint.ascent());
			result = (int) (getPaddingTop() + getPaddingBottom() + Math.max(Math.max(mReachedPBHeight, mUnReachedPBHeight), Math.abs
					(textHeight)));
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		canvas.save();//先保存目前画纸的位置，画完后调用restore方法返回到刚才保存的位置
		canvas.translate(getPaddingLeft(), getHeight() / 2);//移动画笔
		boolean noNeedBg = false;
		float radio = getProgress() * 1.0f / getMax();
		float progressPosX = (int) (mRealWidth * radio);
		String text = getProgress() + "%";
		// mPaint.getTextBounds(text, 0, text.length(), mTextBound);
		float textWidth = mPaint.measureText(text);
		float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;
		if (progressPosX + textWidth > mRealWidth) {
			progressPosX = mRealWidth - textWidth;
			noNeedBg = true;
		}
		// draw reached bar
		float endX = progressPosX ;//- mTextOffset / 2
		if (endX > 0) {
			mPaint.setColor(mReachedBarColor);
			mPaint.setStrokeWidth(mReachedPBHeight);
			canvas.drawLine(0, 0, endX, 0, mPaint);
		}
		// draw progress bar,measure text bound
		if (mIfDrawText) {
			mPaint.setColor(mTextColor);
			canvas.drawText(text, progressPosX, -textHeight, mPaint);
		}
		canvas.drawCircle(progressPosX, 0,dp2px(5), mPaint);
		// draw unreached bar
		if (!noNeedBg) {
			float start = progressPosX+dp2px(5);// + mTextOffset / 2 + textWidth
			mPaint.setColor(mUnReachedBarColor);
			mPaint.setStrokeWidth(mUnReachedPBHeight);
			canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
		}
		canvas.restore();//返回到刚才保存的位置
	}

	protected int dp2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
	}

	protected int sp2px(int spVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
	}
}