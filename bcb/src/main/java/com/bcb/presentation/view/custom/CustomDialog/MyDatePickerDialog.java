package com.bcb.presentation.view.custom.CustomDialog;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * Created by cain on 16/2/23.
 */
public class MyDatePickerDialog extends DatePickerDialog {
    public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    public MyDatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, theme, callBack, year, monthOfYear, dayOfMonth);
    }

    @Override
    protected void onStop() {

    }
}
