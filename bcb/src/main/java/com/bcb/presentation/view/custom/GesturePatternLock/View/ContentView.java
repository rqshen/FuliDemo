package com.bcb.presentation.view.custom.GesturePatternLock.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bcb.R;
import com.bcb.util.ScreenUtils;
import com.bcb.presentation.view.custom.GesturePatternLock.bean.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cain on 16/3/2.
 */
public class ContentView extends ViewGroup {

    private int[] screenDispaly;

    private int eachWith; //图片半径
    private int margin; //间距

    /**
     * 声明一个集合用来封装坐标集合
     */
    private List<Point> list;
    private Context context;
    private Drawl drawl;


    public ContentView(Context context, Drawl.GestureCallBack callBack) {
        super(context);
        setupContentView(context, true, "", callBack);
    }

    public ContentView(Context context,String passWord, Drawl.GestureCallBack callBack) {
        super(context);
        setupContentView(context, false, passWord, callBack);
    }

    //初始化ContentView
    private void setupContentView(Context context, boolean isSettingPasswd, String passWord, Drawl.GestureCallBack callBack) {
        screenDispaly = ScreenUtils.getScreenDispaly(context);
        this.list = new ArrayList<Point>();
        this.context = context;
        // 添加9个图标
        addChild();
        // 初始化一个可以画线的view
        if (isSettingPasswd) {
            drawl = new Drawl(context, list, callBack);
        } else {
            drawl = new Drawl(context, list, passWord,callBack);
        }
    }

    //添加子元素
    private void addChild(){
        for (int i = 0; i < 9; i++) {
            ImageView image = new ImageView(context);
            image.setBackgroundResource(R.drawable.gesture_node_default);
            this.addView(image);

            //获取图片宽度
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.gesture_logo);
            eachWith = bitmap.getWidth() / 2;
            margin = (screenDispaly[0] - 3 * bitmap.getWidth()) / 4;

            // 第几行
            int row = i / 3;
            // 第几列
            int col = i % 3;

            // 定义点的上下左右位置
            int leftX = col * (2 * eachWith + margin) + margin;
            int topY = row * (2 * eachWith + margin) + margin;
            int rightX = (col + 1) * (2 * eachWith + margin);
            int bottomY = (row + 1) * (2 * eachWith + margin);

            Point p = new Point(leftX, rightX, topY, bottomY, image,i+1);
            this.list.add(p);
        }
    }

    //设置父界面
    public void setParentView(ViewGroup parent){
        // 得到屏幕的宽度
        int width = screenDispaly[0];
        LayoutParams layoutParams = new LayoutParams(width, width);

        this.setLayoutParams(layoutParams);
        drawl.setLayoutParams(layoutParams);

        parent.addView(drawl);
        parent.addView(this);

    }

    //设置是否用于设置手势密码
    public void setGesturePassword(boolean status) {
        drawl.setSettingPasswd(status);
    }

    //获取是否设置手势密码的状态
    public boolean getGesturePassword() {
        return drawl.getSettingPasswdStatus();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            //第几行
            int row = i / 3;
            //第几列
            int col = i % 3;
            View v = getChildAt(i);
            //布局每个子元素的大小和位置
            v.layout(col * (2 * eachWith + margin) + margin, row * (2 * eachWith + margin) + margin, (col + 1) * (2 * eachWith + margin), (row + 1) * (2 * eachWith + margin));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
