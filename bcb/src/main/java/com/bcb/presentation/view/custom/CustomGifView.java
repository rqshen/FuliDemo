package com.bcb.presentation.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.view.View;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/8/9 10:07
 */
public class CustomGifView extends View {
    private Movie mMovie;
    private long mMovieStart;

    public CustomGifView(Context context, int resId) {
        super(context);
        //读入字节流（或字节数组或文件）来解码创建Movie对象
        mMovie = Movie.decodeStream(getResources().openRawResource(resId));
    }

    public void onDraw(Canvas canvas) {
        long now = android.os.SystemClock.uptimeMillis();//系统当前时刻
        //第一次播放
        if (mMovieStart == 0) mMovieStart = now;//动画开始的时间
        if (mMovie != null) {
            int dur = mMovie.duration();//动画持续的时间，也就是完成一次动画的时间
            if (dur == 0) dur = 1000;
            int relTime = (int) ((now - mMovieStart) % dur);//注意这是取余操作，这才能算出当前这次重复播放的第一帧的时间
            mMovie.setTime(relTime);//设置相对本次播放第一帧时间，根据这个时间来决定显示第几帧
            mMovie.draw(canvas, 0, 0);
            //强制重绘
            invalidate();
        }
    }
}