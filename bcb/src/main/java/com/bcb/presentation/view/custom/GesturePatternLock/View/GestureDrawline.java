package com.bcb.presentation.view.custom.GesturePatternLock.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.bcb.presentation.view.custom.GesturePatternLock.GestureUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 手势密码路径绘制
 */
public class GestureDrawline extends View {
	/**声明起点坐标	 */
	private int mov_x;
	private int mov_y;
	private Paint paint;
	private Canvas canvas;
	private Bitmap bitmap;
	/**装有各个view坐标的集合	 */
	private List<GesturePoint> list;

	/**记录画过的线	 */
	private List<Pair<GesturePoint, GesturePoint>> lineList;

	/**判断是否设置手势密码	 */
	private boolean isSettingPasswd = false;

	/**手指当前在哪个GesturePoint内	 */
	private GesturePoint currentGesturePoint;
	/**用户绘图的回调	 */
	private GestureCallBack callBack;

	/**用户当前绘制的图形密码	 */
	private StringBuilder passWordSb;

	/**用户传入的passWord */
	private String passWord;
	/**自动选中的情况点*/
	private Map<String, GesturePoint> autoCheckPointMap;

	public GestureDrawline(Context context, List<GesturePoint> list, String passWord, GestureCallBack callBack) {
		super(context);
		this.list = list;
		this.passWord = passWord;
		this.callBack = callBack;
		//******************************************************************************************
		this.lineList = new ArrayList<Pair<GesturePoint, GesturePoint>>();
		if (passWord == null || passWord.equals("")) isSettingPasswd = true;
		else isSettingPasswd = false;
		//初始化密码缓存
		this.passWordSb = new StringBuilder();
		//******************************************************************************************
		//屏幕宽高
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metric);
		bitmap = Bitmap.createBitmap(metric.widthPixels, metric.heightPixels, Bitmap.Config.ARGB_8888);

		canvas = new Canvas();
		canvas.setBitmap(bitmap);

		paint = new Paint(Paint.DITHER_FLAG);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(GestureUtils.dp2px(context, 2f));
		paint.setColor(Color.rgb(219, 56, 56));// 设置默认连线颜色
		paint.setAntiAlias(true);
		initAutoCheckPointMap();//自动选中某些点
	}

	private void initAutoCheckPointMap() {
		autoCheckPointMap = new HashMap<String, GesturePoint>();
		autoCheckPointMap.put("1,3", getGesturePointByNum(2));
		autoCheckPointMap.put("1,7", getGesturePointByNum(4));
		autoCheckPointMap.put("1,9", getGesturePointByNum(5));
		autoCheckPointMap.put("2,8", getGesturePointByNum(5));
		autoCheckPointMap.put("3,7", getGesturePointByNum(5));
		autoCheckPointMap.put("3,9", getGesturePointByNum(6));
		autoCheckPointMap.put("4,6", getGesturePointByNum(5));
		autoCheckPointMap.put("7,9", getGesturePointByNum(8));
	}

	private GesturePoint getGesturePointByNum(int num) {
		for (GesturePoint point : list) {
			if (point.getNum() == num) return point;
		}
		return null;
	}

	// 画位图
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
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
				currentGesturePoint = getGesturePointAt(mov_x, mov_y);
				if (currentGesturePoint != null) {
					currentGesturePoint.setHighLighted(true);
					passWordSb.append(currentGesturePoint.getNum());
				}
				// canvas.drawGesturePoint(mov_x, mov_y, paint);// 画点
				break;

			//手指移动的时候
			case MotionEvent.ACTION_MOVE:
				clearScreenAndDrawList();
				// 得到当前移动位置是处于哪个点内
				GesturePoint gesturePointAt = getGesturePointAt((int) event.getX(), (int) event.getY());
				//代表当前用户手指处于点与点之前
				if (currentGesturePoint == null && gesturePointAt == null) {
					return true;
				} else {//代表用户的手指移动到了点上
					if (currentGesturePoint == null) {//先判断当前的GesturePoint是不是为null
						//如果为空，那么把手指移动到的点赋值给currentGesturePoint
						currentGesturePoint = gesturePointAt;
						//把currentGesturePoint这个点设置选中为true;
						currentGesturePoint.setHighLighted(true);
						passWordSb.append(currentGesturePoint.getNum());
					}
				}

				if (gesturePointAt == null || currentGesturePoint.equals(gesturePointAt) || gesturePointAt.isHighLighted()) {
					// 点击移动区域不在圆的区域，或者如果当前点击的点与当前移动到的点的位置相同，那么以当前的点中心为起点，以手指移动位置为终点画线
					canvas.drawLine(currentGesturePoint.getCenterX(), currentGesturePoint.getCenterY(), event.getX(), event.getY(), paint);// 画线
				} else {
					// 如果当前点击的点与当前移动到的点的位置不同，那么以前前点的中心为起点，以手移动到的点的位置画线
					canvas.drawLine(currentGesturePoint.getCenterX(), currentGesturePoint.getCenterY(), gesturePointAt.getCenterX(), gesturePointAt.getCenterY(),
							paint);// 画线

					gesturePointAt.setHighLighted(true);

					Pair<GesturePoint, GesturePoint> pair = new Pair<GesturePoint, GesturePoint>(currentGesturePoint, gesturePointAt);
					lineList.add(pair);

					// 赋值当前的GesturePoint;
					currentGesturePoint = gesturePointAt;
					passWordSb.append(currentGesturePoint.getNum());
				}
				break;

			// 当手指抬起的时候, 清掉屏幕上所有的线，只画上集合里面保存的线
			case MotionEvent.ACTION_UP:
				//代表输入的密码与用户密码相同
				if (passWordSb.toString().length() < 4) callBack.settingPasswdSuccess(passWordSb, false);
				else if (!isSettingPasswd && passWord.equals(passWordSb.toString())) callBack.checkedSuccess();
				else if (isSettingPasswd) {
					isSettingPasswd = false;
					passWord = passWordSb.toString();
					//缓存密码回调，用于保存密码
					callBack.settingPasswdSuccess(passWordSb, true);
				} else
					//用户绘制的密码与传入的密码不同。
					callBack.checkedFail();

				//重置passWordSb
				passWordSb = new StringBuilder();
				//清空保存点的集合
				lineList.clear();
				//重新绘制界面
				clearScreenAndDrawList();
				for (GesturePoint p : list) {
					p.setHighLighted(false);
				}
				break;

			default:
				break;
		}
		invalidate();
		return true;
	}

	/**
	 * 通过点的位置去集合里面查找这个点是包含在哪个GesturePoint里面的
	 * @return 如果没有找到，则返回null，代表用户当前移动的地方属于点与点之间
	 */
	private GesturePoint getGesturePointAt(int x, int y) {

		for (GesturePoint GesturePoint : list) {
			// 先判断x
			int leftX = GesturePoint.getLeftX();
			int rightX = GesturePoint.getRightX();
			if (!(x >= leftX && x < rightX)) {
				// 如果为假，则跳到下一个对比
				continue;
			}
			int topY = GesturePoint.getTopY();
			int bottomY = GesturePoint.getBottomY();
			if (!(y >= topY && y < bottomY)) {
				// 如果为假，则跳到下一个对比
				continue;
			}

			// 如果执行到这，那么说明当前点击的点的位置在遍历到点的位置这个地方
			return GesturePoint;
		}

		return null;
	}

	/**清掉屏幕上所有的线，然后画出集合里面的线 */
	private void clearScreenAndDrawList() {
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		for (Pair<GesturePoint, GesturePoint> pair : lineList) {
			canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(), pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
		}
	}

	//******************************************************************************************

	/**是否设置手势密码 */
	public void setSettingPasswd(boolean isSettingPasswd) {
		this.isSettingPasswd = isSettingPasswd;
	}

	public boolean getSettingPasswdStatus() {
		return isSettingPasswd;
	}

	/**手势回调*/
	public interface GestureCallBack {
		/**代表用户绘制的密码与传入的密码相同 */
		public abstract void checkedSuccess();

		/**代表用户绘制的密码与传入的密码不相同 */
		public abstract void checkedFail();

		/**用户设置/输入了手势密码*/
		public abstract void settingPasswdSuccess(StringBuilder stringBuilder, boolean settingPasswdStatus);
	}
}