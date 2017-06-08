package com.bcb.presentation.view.custom.GesturePatternLock.View;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/9/5 18:35
 */

import android.widget.ImageView;

import com.bcb.R;

/**
 * 绘制的点
 *
 * @author 白乾涛
 */
public class GesturePoint {
    /**
     * 左边x的值
     */
    private int leftX;
    /**
     * 右边x的值
     */
    private int rightX;
    /**
     * 上边y的值
     */
    private int topY;
    /**
     * 下边y的值
     */
    private int bottomY;
    /**
     * 这个点对应的ImageView控件
     */
    private ImageView image;

    /**
     * 中心x值
     */
    private int centerX;

    /**
     * 中心y值
     */
    private int centerY;

    /**
     * 是否是高亮(划过)，仅用于确定背景图片
     */
    private boolean highLighted;

    /**
     * 代表这个Point对象代表的数字，从1开始(直接感觉从1开始)
     */
    private int num;

    public GesturePoint(int leftX, int rightX, int topY, int bottomY, ImageView image, int num) {
        super();
        this.leftX = leftX;
        this.rightX = rightX;
        this.topY = topY;
        this.bottomY = bottomY;
        this.image = image;
        this.num = num;

        this.centerX = (leftX + rightX) / 2;
        this.centerY = (topY + bottomY) / 2;
    }

    /**
     * 判断是否是相同的点
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof GesturePoint)) return false;//类型不对
        GesturePoint other = (GesturePoint) obj;
        if (bottomY != other.bottomY || leftX != other.leftX || rightX != other.rightX || topY != other.topY)
            return false;//位置不对
        if ((image == null && other.image != null) || !image.equals(other.image))
            return false;//图片不对（状态不对）
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + bottomY;
        result = prime * result + ((image == null) ? 0 : image.hashCode());
        result = prime * result + leftX;
        result = prime * result + rightX;
        result = prime * result + topY;
        return result;
    }

    @Override
    public String toString() {
        return "Point [leftX=" + leftX + ", rightX=" + rightX + ", topY=" + topY + ", bottomY=" + bottomY + "]";
    }

    //******************************************************************************************

    public void setHighLighted(boolean highLighted) {
        this.highLighted = highLighted;
        if (highLighted) {
            this.image.setImageResource(R.drawable.gesture_node_highlighted);
        } else {
            this.image.setImageResource(R.drawable.gesture_node_default);
        }
    }

    public boolean isHighLighted() {
        return highLighted;
    }

    //******************************************************************************************

    public int getLeftX() {
        return leftX;
    }

    public void setLeftX(int leftX) {
        this.leftX = leftX;
    }

    public int getRightX() {
        return rightX;
    }

    public void setRightX(int rightX) {
        this.rightX = rightX;
    }

    public int getTopY() {
        return topY;
    }

    public void setTopY(int topY) {
        this.topY = topY;
    }

    public int getBottomY() {
        return bottomY;
    }

    public void setBottomY(int bottomY) {
        this.bottomY = bottomY;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}