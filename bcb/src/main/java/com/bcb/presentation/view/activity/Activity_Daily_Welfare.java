package com.bcb.presentation.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.event.BroadcastEvent;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.util.DbUtil;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.dg.spinnerwheel.WheelVerticalView;
import com.dg.spinnerwheel.adapters.ArrayWheelAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * Created by Ray on 2016/5/10.
 *
 * @desc 每日福利
 */
public class Activity_Daily_Welfare extends Activity_Base implements View.OnClickListener{
    private ImageView img_background;//整个动画加背景
    private AnimationDrawable welfare_animationDrawable;//动画图片

    //滚屏文字
    private WheelVerticalView value_scroll;
    //滚屏计时器
    private Timer adTimer;
    private TimerTask adTimerTask;
    //是否停止滚动
    private boolean isPause;
    private ArrayWheelAdapter<String> adapter;

    private static final int SCROLL = 100;//滚屏文字

    private TextView join_count;//参与人数
    private String[] rotateValues;//滚动内容
    private String totalInterest;//累计收益

    private Activity context;
    private BcbRequestQueue requestQueue;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SCROLL:
                    value_scroll.scroll(1, 1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushOneActivity(this);
        setContentView(R.layout.activity_daily_welfare);
        context = this;
        setTitleValue("每日福利");

        setLeftTitleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengUtil.eventById(Activity_Daily_Welfare.this, R.string.welfare_back);
                finish();
            }
        });

        //初始化背景动画
        img_background = (ImageView) findViewById(R.id.img_background);
        img_background.setImageResource(R.drawable.daily_welfare_frame);
        welfare_animationDrawable = (AnimationDrawable) img_background.getDrawable();

        //初始化滚动文字
        rotateValues = getResources().getStringArray(R.array.scrollValues);
        //文字滚动
        value_scroll  = (WheelVerticalView) findViewById(R.id.value_scroll);
        isPause = false;
        startRotate();

        //活动规则按钮
        findViewById(R.id.btn_welfare_rule).setOnClickListener(this);
        //动画按钮点击
        findViewById(R.id.null_view).setOnClickListener(this);

        join_count = (TextView) findViewById(R.id.join_count);

        requestQueue = BcbNetworkManager.newRequestQueue(context);
    }

    /**
     * 请求统计数据
     */
    private void getStatisticsData(){
        JSONObject obj = new JSONObject();
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.DailyWelfareData, obj, TokenUtil.getEncodeToken(context), true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status") == 1) {
                        JSONObject resultObject = response.getJSONObject("result");
                        //设置对应位置的数据
                        JSONArray jsonArray = resultObject.getJSONArray("JoinList");
                        if (null != jsonArray) {
                            //更新UI
                            LogUtil.d("统计数据", jsonArray.toString());
                            rotateValues = new String[jsonArray.length()];
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObj = (JSONObject)jsonArray.get(i);
                                rotateValues[i] = jsonObj.getString("Title");
                            }
                            startRotate();
                        }
                        //参与人数
                        String totalPopulation = resultObject.getString("TotalPopulation");
                        if(!TextUtils.isEmpty(totalPopulation)){
                            join_count.setText(totalPopulation);
                        }
                        //累计收益
                        totalInterest = resultObject.getString("TotalInterest");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.UrlWelfareStatisticsTag);
        requestQueue.add(jsonRequest);
    }

    /**
     * 初始化并开始滚动文字
     */
    private void startRotate(){
        if (!this.isFinishing()){
            adapter = new ArrayWheelAdapter<>(this, rotateValues);
            adapter.setTextGravity(Gravity.CENTER);
            adapter.setTextSize(12);
            adapter.setTextColor(getResources().getColor(R.color.red));
            adapter.setTextTypeface(Typeface.DEFAULT);
            value_scroll.setVisibleItems(5);
            value_scroll.setViewAdapter(adapter);
            value_scroll.setCurrentItem(0);
            value_scroll.setCyclic(true);
            value_scroll.setEnabled(false);

            if (null == adTimer){
                adTimer = new Timer();
                adTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(SCROLL);
                    }
                };
                adTimer.schedule(adTimerTask, 1000, 2000);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != welfare_animationDrawable){
            welfare_animationDrawable.start();
        }
        //恢复滚屏
        if (isPause){
            if (null == adTimer){
                adTimer = new Timer();
                adTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(SCROLL);
                    }
                };
                adTimer.schedule(adTimerTask, 1000, 2000);
            }
            isPause = false;
        }

        //请求统计数据
        getStatisticsData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != welfare_animationDrawable){
            welfare_animationDrawable.stop();
        }
        //关闭滚屏定时器
        if (adTimerTask != null)
            adTimerTask.cancel();
        if (adTimer != null){
            adTimer.cancel();
            adTimer.purge();
            adTimer = null;
            isPause = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestQueue.cancelAll(BcbRequestTag.UrlWelfareStatisticsTag);
        requestQueue.cancelAll(BcbRequestTag.UrlJoinWelfareTag);
    }

    /**
     * 启动
     * @param ctx
     */
    public static void launche(Context ctx){
        Intent intent = new Intent(ctx, Activity_Daily_Welfare.class);
        ctx.startActivity(intent);
    }

    /**
     *显示活动规则对话框
     */
    private void showRuleDialog(){
        final AlertView dialog = new AlertView(this, R.style.alertviewstyle);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_welfare_rule, null);
        layout.findViewById(R.id.dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();
    }

    /**
     * 请求福袋
     */
    private void getPackageData(){
        JSONObject obj = new JSONObject();
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.JoinDailyWelfare, obj, TokenUtil.getEncodeToken(context), true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");
                    if (1 == status || -3 == status) {
                        //设置对应位置的数据
                        String value = response.getString("result");
                        LogUtil.d("福袋数据", value);
                        if (!TextUtils.isEmpty(value) && !value.equals("null")){
                            Activity_Daily_Welfare_Result.launche(context, value, totalInterest);

                            //保存到数据库
                            DbUtil.saveWelfare(value);
                            App.getInstance().setWelfare(value);

                            //通知刷新
                            EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.REFRESH));
                        }
                    }else if(-2 == status){//领福利时间为每日 06：00-22：00
                        Activity_Daily_Welfare_Tip.launche(context);
                        finish();
                    }else{
                        //获取数据库缓存数据,若有数据就显示已经缓存的数据
                        if (TextUtils.isEmpty(App.getInstance().getWelfare())){
                            ToastUtil.alert(context, response.getString("message"));
                            App.getInstance().requestWelfare();
                        }else{
                            //通知刷新
                            EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.REFRESH));
                            Activity_Daily_Welfare_Result.launche(context, App.getInstance().getWelfare(), totalInterest);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.UrlJoinWelfareTag);
        requestQueue.add(jsonRequest);
    }


    private long lastClickTime;//上次点击时间
    private final long MIN_CLICK_DELAY_TIME = 500;//最小时间间隔

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_welfare_rule://活动规则
                showRuleDialog();
                break;
            case R.id.null_view://打开福袋
                UmengUtil.eventById(context, R.string.fuli_c2);
                if (App.saveUserInfo.getAccess_Token() == null) {
                    Activity_Login.launche(context);
                    break;
                }
                //防止按钮多次点击
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                    lastClickTime = currentTime;
                    getPackageData();
                }
                break;
        }
    }


}
