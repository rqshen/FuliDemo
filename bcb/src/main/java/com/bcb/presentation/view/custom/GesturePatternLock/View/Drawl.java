package com.bcb.presentation.view.custom.GesturePatternLock.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.bcb.data.util.ScreenUtils;
import com.bcb.presentation.view.custom.GesturePatternLock.bean.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cain on 16/3/2.
 */
public class Drawl extends View {
    private int mov_x;// 声明起点坐标
    private int mov_y;
    private Paint paint;// 声明画笔
    private Canvas canvas;// 画布
    private Bitmap bitmap;// 位图

    private List<Point> list;// 装有各个view坐标的集合
    private List<Pair<Point, Point>> lineList;// 记录画过的线

    private int[] screenDispaly;    //获取屏幕的宽高

    private boolean isSettingPasswd = false; //判断是否设置手势密码

    private Context context;

    /**
     * 手指当前在哪个Point内
     */
    private Point currentPoint;
    /**
     * 用户绘图的回调
     */
    private GestureCallBack callBack;

    /**
     * 用户当前绘制的图形密码
     */
    private StringBuilder passWordSb;

    /**
     * 用户传入的passWord
     */
    private String passWord;

    public Drawl(Context context, List<Point> list, String passWord, GestureCallBack callBack) {
        super(context);
        setupDrawl(context, list, false, passWord, callBack);
    }

    public Drawl(Context context, List<Point> list, GestureCallBack callBack) {
        super(context);
        setupDrawl(context, list, true, null, callBack);
    }

    //初始化
    private void setupDrawl(Context context, List<Point> list, boolean isSettingPasswd, String passWord, GestureCallBack callBack) {
        paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
        screenDispaly = ScreenUtils.getScreenDispaly(context); //获取屏幕的宽高
        this.context = context;
        bitmap = Bitmap.createBitmap(screenDispaly[0], screenDispaly[1], Bitmap.Config.ARGB_8888); // 设置位图的宽高
        canvas = new Canvas();
        canvas.setBitmap(bitmap);

        paint.setStyle(Paint.Style.STROKE);// 设置非填充
        paint.setStrokeWidth(4);// 笔宽像素
//        if (screenDispaly[0] <= 480) {
//            paint.setStrokeWidth(10);// 笔宽像素
//        } else if (screenDispaly[0] <= 720) {
//            paint.setStrokeWidth(15);// 笔宽像素
//        } else if (screenDispaly[0] <= 1080) {
//            paint.setStrokeWidth(20);// 笔宽像素
//        } else {
//            paint.setStrokeWidth(30);// 笔宽像素
//        }

        paint.setColor(Color.rgb(219, 56, 56));// 设置颜色
        paint.setAntiAlias(true);// 不显示锯齿

        this.list = list;
        this.lineList = new ArrayList<Pair<Point, Point>>();
        this.callBack = callBack;

        //设置是否用于设置密码
        this.isSettingPasswd = isSettingPasswd;

        //初始化密码缓存
        this.passWordSb = new StringBuilder();
        if (passWord != null) {
            this.passWord = passWord;
        }
        initAutoCheckPointMap();//自动选中某些点
    }
    /**自动选中的情况点*/
    private Map<String, Point> autoCheckPointMap;
    private void initAutoCheckPointMap() {
        autoCheckPointMap = new HashMap<String, Point>();
        autoCheckPointMap.put("1,3", getGesturePointByNum(2));
        autoCheckPointMap.put("1,7", getGesturePointByNum(4));
        autoCheckPointMap.put("1,9", getGesturePointByNum(5));
        autoCheckPointMap.put("2,8", getGesturePointByNum(5));
        autoCheckPointMap.put("3,7", getGesturePointByNum(5));
        autoCheckPointMap.put("3,9", getGesturePointByNum(6));
        autoCheckPointMap.put("4,6", getGesturePointByNum(5));
        autoCheckPointMap.put("7,9", getGesturePointByNum(8));
    }
    private Point getGesturePointByNum(int num) {
        for (Point point : list) {
            if (point.getNum() == num) return point;
        }
        return null;
    }
    private Point getBetweenCheckPoint(Point pointStart, Point pointEnd) {
        int startNum = pointStart.getNum();
        int endNum = pointEnd.getNum();
        String key = null;
        if (startNum < endNum) {
            key = startNum + "," + endNum;
        } else {
            key = endNum + "," + startNum;
        }
        return autoCheckPointMap.get(key);
    }
    // 画位图
    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    // 触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            //手指按下的时候
            case MotionEvent.ACTION_DOWN:
                mov_x = (int) event.getX();
                mov_y = (int) event.getY();
                // 判断当前点击的位置是处于哪个点之内
                currentPoint = getPointAt(mov_x, mov_y);
                if (currentPoint != null) {
                    currentPoint.setHighLighted(true);
                    passWordSb.append(currentPoint.getNum());
                }
                // canvas.drawPoint(mov_x, mov_y, paint);// 画点
                invalidate();
                break;

            //手指移动的时候
            case MotionEvent.ACTION_MOVE:
                clearScreenAndDrawList();
                // 得到当前移动位置是处于哪个点内
                Point pointAt = getPointAt((int) event.getX(), (int) event.getY());
                //代表当前用户手指处于点与点之前
                if(currentPoint==null && pointAt == null){
                    return true;
                }else{//代表用户的手指移动到了点上
                    if(currentPoint == null){//先判断当前的point是不是为null
                        //如果为空，那么把手指移动到的点赋值给currentPoint
                        currentPoint = pointAt;
                        //把currentPoint这个点设置选中为true;
                        currentPoint.setHighLighted(true);
                        passWordSb.append(currentPoint.getNum());
                    }
                }

                if (pointAt == null || currentPoint.equals(pointAt) || pointAt.isHighLighted()) {
                    // 点击移动区域不在圆的区域 或者
                    // 如果当前点击的点与当前移动到的点的位置相同
                    // 那么以当前的点中心为起点，以手指移动位置为终点画线
                    canvas.drawLine(currentPoint.getCenterX(), currentPoint.getCenterY(), event.getX(), event.getY(), paint);// 画线
                } else {
                    // 如果当前点击的点与当前移动到的点的位置不同
                    // 那么以前前点的中心为起点，以手移动到的点的位置画线
                    canvas.drawLine(currentPoint.getCenterX(), currentPoint.getCenterY(), pointAt.getCenterX(), pointAt.getCenterY(), paint);// 画线

                    pointAt.setHighLighted(true);
                    // 判断是否中间点需要选中
                    Point betweenPoint = getBetweenCheckPoint(currentPoint, pointAt);
                    if (betweenPoint != null && !betweenPoint.isHighLighted()) {
                        // 存在中间点并且没有被选中
                        Pair<Point, Point> pair1 = new Pair<Point, Point>(currentPoint, betweenPoint);
                        lineList.add(pair1);
                        passWordSb.append(betweenPoint.getNum());
                        Pair<Point, Point> pair2 = new Pair<Point, Point>(betweenPoint, pointAt);
                        lineList.add(pair2);
                        passWordSb.append(pointAt.getNum());
                        // 设置中间点选中
                        betweenPoint.setHighLighted(true);
                        // 赋值当前的point;
                        currentPoint = pointAt;
                    }else {
                        Pair<Point, Point> pair = new Pair<Point, Point>(currentPoint, pointAt);
                        lineList.add(pair);
                        // 赋值当前的point;
                        currentPoint = pointAt;
                        passWordSb.append(currentPoint.getNum());
                    }
                }
                invalidate();
                break;

            // 当手指抬起的时候, 清掉屏幕上所有的线，只画上集合里面保存的线
            case MotionEvent.ACTION_UP:
                //代表输入的密码与用户密码相同
                if (passWordSb.toString().length() < 4) {
                    callBack.settingPasswdSuccess(passWordSb, false);
                } else if (!isSettingPasswd && passWord.equals(passWordSb.toString())) {
                    callBack.checkedSuccess();
                } else if (isSettingPasswd) {
                    isSettingPasswd = false;
                    passWord = passWordSb.toString();
                    //缓存密码回调，用于保存密码
                    callBack.settingPasswdSuccess(passWordSb, true);
                } else {
                    //用户绘制的密码与传入的密码不同。
                    callBack.checkedFail();
                }

                //重置passWordSb
                passWordSb = new StringBuilder();
                //清空保存点的集合
                lineList.clear();
                //重新绘制界面
                clearScreenAndDrawList();
                for (Point p : list) {
                    p.setHighLighted(false);
                }
                invalidate();
                break;

            default:
                break;
        }
        return true;
    }

    /**
     * 设置是否设置手势密码
     * @param status
     */
    public void setSettingPasswd(boolean status) {
        this.isSettingPasswd = status;
    }

    public boolean getSettingPasswdStatus() {
        return isSettingPasswd;
    }

    /**
     * 通过点的位置去集合里面查找这个点是包含在哪个Point里面的
     *
     * @param x
     * @param y
     * @return 如果没有找到，则返回null，代表用户当前移动的地方属于点与点之间
     */
    private Point getPointAt(int x, int y) {

        for (Point point : list) {
            // 先判断x
            int leftX = point.getLeftX();
            int rightX = point.getRightX();
            if (!(x >= leftX && x < rightX)) {
                // 如果为假，则跳到下一个对比
                continue;
            }
            int topY = point.getTopY();
            int bottomY = point.getBottomY();
            if (!(y >= topY && y < bottomY)) {
                // 如果为假，则跳到下一个对比
                continue;
            }

            // 如果执行到这，那么说明当前点击的点的位置在遍历到点的位置这个地方
            return point;
        }

        return null;
    }

    /**
     * 清掉屏幕上所有的线，然后画出集合里面的线
     */
    private void clearScreenAndDrawList() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (Pair<Point, Point> pair : lineList) {
            canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
                    pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
        }
    }

    //手势回调
    public interface GestureCallBack {
        /**
         * 代表用户绘制的密码与传入的密码相同
         */
        public abstract void checkedSuccess();
        /**
         * 代表用户绘制的密码与传入的密码不相同
         */
        public abstract void checkedFail();

        /**
         * 用于设置密码回调
         */
        public abstract void settingPasswdSuccess(StringBuilder stringBuilder, boolean settingPasswdStatus);
    }

}
