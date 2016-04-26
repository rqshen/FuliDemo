package com.bcb.presentation.view.custom.EditTextWithDate;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.DatePicker;
import android.widget.EditText;
import java.util.Calendar;
import com.bcb.R;
import com.bcb.presentation.view.custom.CustomDialog.MyDatePickerDialog;

/**
 * Created by cain on 16/1/8.
 * 自定义日历
 */
public class EditTextWithDate extends EditText {
    private final static String TAG = "EditTextWithDate";
    private Drawable dateIcon;
    private Context mContext;
    private Calendar calendar;
    private int selectYear;
    private int selectMonth;
    private int selectDay;
    private boolean datePickerEnabled=true;

    public EditTextWithDate(Context context) {
        super(context);
        mContext=context;
        init();
    }

    public EditTextWithDate(Context context,AttributeSet attrs) {
        super(context,attrs);
        mContext=context;
        init();
    }

    public EditTextWithDate(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }
    //初始化
    private void init() {
        dateIcon=mContext.getResources().getDrawable(R.drawable.arrow_down);
        dateIcon.setBounds(20, 5, 20, 5);
        setCompoundDrawables(null, null, dateIcon, null);
        calendar = Calendar.getInstance();
        final Calendar cal = Calendar.getInstance();
        selectYear = cal.get(Calendar.YEAR); // 获取当前年份
        selectMonth = cal.get(Calendar.MONTH);// 获取当前月份
        selectDay = cal.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码
        this.setEnabled(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (dateIcon != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Log.e(TAG, "eventX = " + eventX + "; eventY = " + eventY);
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = 0;
            if(rect.contains(eventX, eventY)){
                if(datePickerEnabled){
                    new MyDatePickerDialog(mContext, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            selectYear = year;
                            selectMonth = monthOfYear;
                            selectDay = dayOfMonth;
                            setText(new StringBuilder().append("").append(selectYear).append("-").append(selectMonth + 1).append("-").append(selectDay).append(""));
                        }

                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        }
        return super.onTouchEvent(event);
    }


    public boolean isDatePickerEnabled() {
        return datePickerEnabled;
    }

    public void setDatePickerEnabled(boolean datePickerEnabled) {
        this.datePickerEnabled = datePickerEnabled;
        if(!datePickerEnabled){
            setText("");
        }
    }

    //判断期望到账时间大于1天
    public boolean isCalendarLargerThanOneDay() {
        //获取当前日期
        Calendar cal =Calendar.getInstance();
        int year = cal.get(Calendar.YEAR); // 获取当前年份
        int  month = cal.get(Calendar.MONTH);// 获取当前月份
        int day = cal.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码

        //如果选择年份大于当前年，则直接返回true
        if (selectYear > year) return true;
        //如果选择月份大于当前月，则直接返回true
        if (selectMonth > month) return true;
        //如果选择年份和月份不小于当前年月，则判断是否选择日是否大于当日+1，如果满足，则直接返回true
        if (selectYear >= year && selectMonth >= month && selectDay - day > 1) return true;
        //都不满足的时候，就返回false
        return false;
    }

}