package com.bcb.presentation.view.custom.PayPasswordView;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.project.KeyboardEnum;

import java.util.ArrayList;


/**
 * Created by cain on 16/2/1.
 */
@SuppressLint("InflateParams")
public class PayPasswordView implements View.OnClickListener, View.OnLongClickListener{

    private ImageView del;
    private ImageView zero;
    private ImageView one;
    private ImageView two;
    private ImageView three;
    private ImageView four;
    private ImageView five;
    private ImageView sex;
    private ImageView seven;
    private ImageView eight;
    private ImageView nine;
    private TextView box1;
    private TextView box2;
    private TextView box3;
    private TextView box4;
    private TextView box5;
    private TextView box6;
    private TextView title;
    private TextView content;
    private ImageView cancel;

    private ArrayList<String> mList=new ArrayList<String>();
    private View mView;
    private OnPayListener listener;
    private Context mContext;

    //构造函数
    public PayPasswordView(String monney, Context mContext, OnPayListener listener){
        getDecorView(monney, mContext, listener);
    }

    public static PayPasswordView getInstance(String monney, Context mContext, OnPayListener listener){
        return new PayPasswordView(monney, mContext, listener);
    }

    public void getDecorView(String monney,Context mContext,OnPayListener listener) {
        this.listener = listener;
        this.mContext = mContext;
        mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_buy_project, null);

        del = (ImageView) mView.findViewById(R.id.pay_keyboard_del);
        del.setOnClickListener(this);
        del.setOnLongClickListener(this);
        zero = (ImageView) mView.findViewById(R.id.pay_keyboard_zero);
        zero.setOnClickListener(this);
        one = (ImageView) mView.findViewById(R.id.pay_keyboard_one);
        one.setOnClickListener(this);
        two = (ImageView) mView.findViewById(R.id.pay_keyboard_two);
        two.setOnClickListener(this);
        three = (ImageView) mView.findViewById(R.id.pay_keyboard_three);
        three.setOnClickListener(this);
        four = (ImageView) mView.findViewById(R.id.pay_keyboard_four);
        four.setOnClickListener(this);
        five = (ImageView) mView.findViewById(R.id.pay_keyboard_five);
        five.setOnClickListener(this);
        sex = (ImageView) mView.findViewById(R.id.pay_keyboard_sex);
        sex.setOnClickListener(this);
        seven = (ImageView) mView.findViewById(R.id.pay_keyboard_seven);
        seven.setOnClickListener(this);
        eight = (ImageView) mView.findViewById(R.id.pay_keyboard_eight);
        eight.setOnClickListener(this);
        nine = (ImageView) mView.findViewById(R.id.pay_keyboard_nine);
        nine.setOnClickListener(this);

        cancel = (ImageView) mView.findViewById(R.id.pay_cancel);
        cancel.setOnClickListener(this);

        box1 = (TextView) mView.findViewById(R.id.pay_box1);
        box2 = (TextView) mView.findViewById(R.id.pay_box2);
        box3 = (TextView) mView.findViewById(R.id.pay_box3);
        box4 = (TextView) mView.findViewById(R.id.pay_box4);
        box5 = (TextView) mView.findViewById(R.id.pay_box5);
        box6 = (TextView) mView.findViewById(R.id.pay_box6);

        title = (TextView) mView.findViewById(R.id.pay_title);
        content = (TextView) mView.findViewById(R.id.pay_content);
        content.setText(monney + "元");
    }

    private void parseActionType(KeyboardEnum type) {
        if (type.getType() == KeyboardEnum.ActionEnum.add) {
            if(mList.size() < 6) {
                mList.add(type.getValue());
                updateUi();
                //如果添加之后的位数等于6时，立即请求
                if (mList.size() == 6) {
                    String payValue="";
                    for (int i = 0; i < mList.size(); i++) {
                        payValue += mList.get(i);
                    }
                    listener.onSurePay(payValue);
                }
            }
        }
        //取消
        else if (type.getType() == KeyboardEnum.ActionEnum.cancel) {
            listener.onCancelPay();
        }
        //删除
        else if (type.getType() == KeyboardEnum.ActionEnum.delete) {
            int size = mList.size();
            if (size > 0) {
                mList.remove(size - 1);
                updateUi();
            }
        }
        //长按删除所有
        else if (type.getType() == KeyboardEnum.ActionEnum.longClick){
            mList.clear();
            updateUi();
        }
    }
    private void updateUi() {
        if (mList.size() == 0) {
            box1.setText("");
            box2.setText("");
            box3.setText("");
            box4.setText("");
            box5.setText("");
            box6.setText("");
        } else if (mList.size() == 1) {
            box1.setText(mList.get(0));
            box2.setText("");
            box3.setText("");
            box4.setText("");
            box5.setText("");
            box6.setText("");
        } else if (mList.size() == 2) {
            box1.setText(mList.get(0));
            box2.setText(mList.get(1));
            box3.setText("");
            box4.setText("");
            box5.setText("");
            box6.setText("");
        } else if (mList.size() == 3) {
            box1.setText(mList.get(0));
            box2.setText(mList.get(1));
            box3.setText(mList.get(2));
            box4.setText("");
            box5.setText("");
            box6.setText("");
        } else if (mList.size() == 4) {
            box1.setText(mList.get(0));
            box2.setText(mList.get(1));
            box3.setText(mList.get(2));
            box4.setText(mList.get(3));
            box5.setText("");
            box6.setText("");
        } else if (mList.size() == 5) {
            box1.setText(mList.get(0));
            box2.setText(mList.get(1));
            box3.setText(mList.get(2));
            box4.setText(mList.get(3));
            box5.setText(mList.get(4));
            box6.setText("");
        } else if (mList.size() == 6) {
            box1.setText(mList.get(0));
            box2.setText(mList.get(1));
            box3.setText(mList.get(2));
            box4.setText(mList.get(3));
            box5.setText(mList.get(4));
            box6.setText(mList.get(5));

        }
    }

    @Override
    public void onClick(View v) {
        if(v == zero) {
            parseActionType(KeyboardEnum.zero);
        } else if (v == one) {
            parseActionType(KeyboardEnum.one);
        } else if (v == two) {
            parseActionType(KeyboardEnum.two);
        } else if (v == three) {
            parseActionType(KeyboardEnum.three);
        } else if (v == four) {
            parseActionType(KeyboardEnum.four);
        } else if (v == five) {
            parseActionType(KeyboardEnum.five);
        } else if (v == sex) {
            parseActionType(KeyboardEnum.sex);
        } else if (v == seven) {
            parseActionType(KeyboardEnum.seven);
        } else if (v == eight) {
            parseActionType(KeyboardEnum.eight);
        } else if(v == nine) {
            parseActionType(KeyboardEnum.nine);
        } else if(v == del) {
            parseActionType(KeyboardEnum.del);
        } else if (v == cancel) {
            parseActionType(KeyboardEnum.cancel);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        parseActionType(KeyboardEnum.longdel);
        return true;
    }

    public interface OnPayListener {
        void onCancelPay();
        void onSurePay(String password);
    }

    public View getView(){
        return mView;
    }
}
