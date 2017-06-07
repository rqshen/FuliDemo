package com.bcb.module.myinfo.welfare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
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

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.event.BroadcastEvent;
import com.bcb.module.login.LoginActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.presentation.view.activity.Activity_Daily_Welfare_Tip;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.util.DbUtil;
import com.bcb.util.LogUtil;
import com.bcb.util.MyActivityManager;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;
import com.bcb.util.UmengUtil;
import com.dg.spinnerwheel.WheelVerticalView;
import com.dg.spinnerwheel.adapters.ArrayWheelAdapter;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by ruiqin.shen
 * 类说明：每日福利
 */
public class DailyWelfareActivity extends Activity_Base implements View.OnClickListener {
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
    private int totalPopulation;//参与人数
    private float totalInterest;//累计收益
    private String[] rotateValues;//滚动内容

    //短促音
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundID;

    private Activity context;
    private BcbRequestQueue requestQueue;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCROLL:
                    value_scroll.scroll(1, 1000);
                    break;
            }
        }
    };

    /**
     * 启动
     *
     * @param ctx
     * @param rotateValues    滚动文字
     * @param totalPopulation 参与人数
     * @param totalInterest   累计加息
     */
    public static void launche(Context ctx, String[] rotateValues, int totalPopulation, float totalInterest) {
        Intent intent = new Intent(ctx, DailyWelfareActivity.class);
        intent.putExtra("rotateValues", rotateValues);
        intent.putExtra("totalPopulation", totalPopulation);
        intent.putExtra("totalInterest", totalInterest);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushOneActivity(this);
        setContentView(R.layout.activity_daily_welfare);
        context = this;
        setTitleValue("每日福利");

        initSoundPool();

        setLeftTitleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengUtil.eventById(DailyWelfareActivity.this, R.string.welfare_back);
                finish();
            }
        });

        //初始化背景动画
        img_background = (ImageView) findViewById(R.id.img_background);
        img_background.setImageResource(R.drawable.daily_welfare_frame);
        welfare_animationDrawable = (AnimationDrawable) img_background.getDrawable();

        //初始化滚动文字、累计加息、参与人数
        rotateValues = getIntent().getStringArrayExtra("rotateValues");
        totalInterest = getIntent().getFloatExtra("totalInterest", 0);
        totalPopulation = getIntent().getIntExtra("totalPopulation", 0);

        //活动规则按钮
        findViewById(R.id.btn_welfare_rule).setOnClickListener(this);
        //动画按钮点击
        findViewById(R.id.null_view).setOnClickListener(this);
        //参与人数
        join_count = (TextView) findViewById(R.id.join_count);
        String str = String.format("今天已有%s位用户获得加息", totalPopulation);
        join_count.setText(str);
        //文字滚动
        value_scroll = (WheelVerticalView) findViewById(R.id.value_scroll);
        isPause = false;
        startRotate();

        requestQueue = MyApplication.getInstance().getRequestQueue();
    }

    /**
     * 初始化并开始滚动文字
     */
    private void startRotate() {
        if (!this.isFinishing()) {
            adapter = new ArrayWheelAdapter<>(this, rotateValues);
            adapter.setTextGravity(Gravity.CENTER);
            adapter.setTextSize(12);
            adapter.setTextColor(getResources().getColor(R.color.txt_middle_gray));
            adapter.setTextTypeface(Typeface.DEFAULT);
            value_scroll.setVisibleItems(3);
            value_scroll.setViewAdapter(adapter);
            value_scroll.setCurrentItem(0);
            value_scroll.setCyclic(true);
            value_scroll.setEnabled(false);

            if (null == adTimer) {
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

    /**
     * 初始化音乐池
     */
    public void initSoundPool() {//初始化声音池
        soundPool = new SoundPool(
                1,     //maxStreams参数，该参数为设置同时能够播放多少音效
                AudioManager.STREAM_MUSIC,    //streamType参数，该参数设置音频类型，在游戏中通常设置为：STREAM_MUSIC
                0    //srcQuality参数，该参数设置音频文件的质量，目前还没有效果，设置为0为默认值。
        );

        soundID = new HashMap<>();
        try {
            soundID.put(1, soundPool.load(getAssets().openFd("welfare.wav"), 1));  //需要捕获IO异常
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != welfare_animationDrawable) {
            welfare_animationDrawable.start();
        }
        //恢复滚屏
        if (isPause) {
            if (null == adTimer) {
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

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != welfare_animationDrawable) {
            welfare_animationDrawable.stop();
        }
        //关闭滚屏定时器
        if (adTimerTask != null)
            adTimerTask.cancel();
        if (adTimer != null) {
            adTimer.cancel();
            adTimer.purge();
            adTimer = null;
            isPause = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestQueue.cancelAll(BcbRequestTag.UrlJoinWelfareTag);
    }

    /**
     * 显示活动规则对话框
     */
    private void showRuleDialog() {
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
    private void getPackageData() {
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
                        if (!TextUtils.isEmpty(value) && !value.equals("null")) {
                            //播放短促音
                            soundPool.play(soundID.get(1), 1, 1, 0, 0, 1);
                            //弹出金币
                            DailyWelfareResultActivity.launche(context, value, String.valueOf(totalInterest));

                            //保存到数据库
                            DbUtil.saveWelfare(value);
                            MyApplication.getInstance().setWelfare(value);

                            //通知刷新
                            EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.REFRESH));
                        }
                    } else if (-2 == status) {//领福利时间为每日 06：00-22：00
                        Activity_Daily_Welfare_Tip.launche(context);
//                        finish();
                    } else if (-5 == status) {
                        MyApplication.saveUserInfo.clear();
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    } else {
                        //获取数据库缓存数据,若有数据就显示已经缓存的数据
                        if (TextUtils.isEmpty(MyApplication.getInstance().getWelfare())) {
                            ToastUtil.alert(context, response.getString("message"));
                            MyApplication.getInstance().requestWelfare();
                        } else {
                            //通知刷新
                            EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.REFRESH));
                            DailyWelfareResultActivity.launche(context, MyApplication.getInstance().getWelfare(), String.valueOf(totalInterest));
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
        switch (v.getId()) {
            case R.id.btn_welfare_rule://活动规则
                showRuleDialog();
                break;
            case R.id.null_view://打开福袋
                UmengUtil.eventById(context, R.string.fuli_c2);
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
