package com.bcb.presentation.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * 描述：生利宝收益
 * 作者：baicaibang
 * 时间：2016/8/15 09:34
 */
public class SlbSyView extends View {
    Context context;
    Paint paint;
    Path path;//用于绘制复杂的图形轮廓，比如折线，圆弧以及各种复杂图案
    int width, height;//表格的宽高
    int widthBlank, heightBlank;//表格距离左侧、顶部的空白距离

    static final int WIDTH_MAX = 6;//View的相对宽高（7个）；WIDTH_MAX是不会变的
    float heightMax = 7.0f;//heightMax是根据需要随便改变；
    float[] heights = new float[WIDTH_MAX + 1];//高度值

    float heightOffset = 0.1f;//偏差，根据需要设置
    float y6Value;//最后一个坐标的值


    public SlbSyView(Context context) {
        this(context, null);
    }

    public SlbSyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        paint = new Paint();
        path = new Path();
        paint.setAntiAlias(true);
        paint.setDither(true);
        heightBlank = dp2px(25);
        widthBlank = dp2px(35);
        width = getScreenWidth(context) - widthBlank*2;
        height = dp2px(100);
    }

    public void setHeights(float[] heights) {
        if (heights.length != 7) throw new RuntimeException("长度不对");
        y6Value = heights[6];
        heightMax = getMaxValue(heights) - getMinValue(heights) + heightOffset * 2;//根据需要设置
        for (int i = 0; i < heights.length; i++) {
            this.heights[i] = getMaxValue(heights) + heightOffset - heights[i];//根据需要设置
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        //1、画背景
        paint.setColor(0x1adb3838);//10%透明度
        paint.setStyle(Paint.Style.FILL);
        path.moveTo(widthBlank, height + heightBlank);//指定初始轮廓点，若没指定默认从(0,0)点开始
        for (int i = 0; i < heights.length; i++) {
            path.lineTo(widthBlank + width * i / WIDTH_MAX, //中间的点
                    heightBlank + height * heights[i] / heightMax);
        }
        path.lineTo(widthBlank + width, heightBlank + height);//最后的一个点
        path.close(); // 回到初始点形成封闭的曲线
        canvas.drawPath(path, paint);
        //2、画基准线条。每条线都需要两个坐标(注意，是每两个值组成一个坐标)
        paint.setStrokeWidth(dp2px(0.5f));//设置画笔粗细，单位为像素
        paint.setColor(0xff999999);
        float[] points = new float[7 * 4];//坐标
        //画7条竖线
        for (int i = 0; i < points.length; i += 4) {
            points[i] = widthBlank + width * i / (4 * WIDTH_MAX);
            points[i + 1] = heightBlank;
            points[i + 2] = points[i];
            points[i + 3] = heightBlank + height;
        }
        canvas.drawLines(points, paint);
        //画2条横线
        canvas.drawLine(widthBlank, heightBlank, //
                widthBlank + width, heightBlank, paint);
        canvas.drawLine(widthBlank, heightBlank + height,//
                widthBlank + width, heightBlank + height, paint);
        //3、画利率线
        paint.setColor(0xffdb3838);
        paint.setStrokeWidth(dp2px(2));
        for (int i = 0; i < heights.length - 1; i++) {//注意遍历条件 heights.length - 1
            canvas.drawLine(widthBlank + width * i / WIDTH_MAX, //
                    heightBlank + height * heights[i] / heightMax, //
                    widthBlank + width * (i + 1) / WIDTH_MAX, //
                    heightBlank + height * heights[i + 1] / heightMax, paint);
        }
        //4、画圆形
        float x6Point = widthBlank + width * 6 / WIDTH_MAX;
        float y6Point = heightBlank + height * heights[6] / heightMax;
        //画白色的实心圆圆
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(x6Point, y6Point, dp2px(4), paint);
        //画空心圆
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xffdb3838);
        canvas.drawCircle(x6Point, y6Point, dp2px(4), paint);//圆心坐标，半径
        //5、画文本（今天的年利率）
        //画圆角矩形
        paint.setStyle(Paint.Style.FILL);
        RectF rect = new RectF(x6Point - dp2px(25), y6Point - dp2px(25), //左上角坐标
                x6Point + dp2px(25), y6Point - dp2px(8));//右下角坐标
        canvas.drawRoundRect(rect, dp2px(3), dp2px(3), paint);//两侧圆角弧度的大小
        //画文字
        paint.setColor(Color.WHITE);
        paint.setTextSize(dp2px(13));//单位是px，只在绘制文字时有效
        paint.setTextAlign(Paint.Align.CENTER);//绘制的文字以drawText时指定的 float x 水平居中，默认值是Align.LEFT
        canvas.drawText(y6Value + "", rect.centerX(),//
                rect.centerY() + dp2px(5f), paint);//注意 float y 代表的是 baseline 的值，也即e和f的下边界，而非g的下边界
    }

    private int dp2px(float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取屏幕宽
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 取最大值
     */
    public static float getMaxValue(float[] a) {
        float temp = a[0];
        for (int i = 0; i < a.length; i++) {
            if (a[i] > temp) temp = a[i];
        }
        return temp;
    }

    /**
     * 取最小值
     */
    public static float getMinValue(float[] a) {
        float temp = a[0];
        for (int i = 0; i < a.length; i++) {
            if (a[i] < temp) temp = a[i];
        }
        return temp;
    }
}
