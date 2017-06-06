package com.bcb.util;

import android.app.Activity;
import android.content.Context;

import com.bcb.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

public class UmengUtil {

	public static void init(Context ctx){
		MobclickAgent.updateOnlineConfig(ctx);
		OnlineConfigAgent.getInstance().updateOnlineConfig(ctx);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
	}
	
	public static void onResume(Context ctx){
		MobclickAgent.onResume(ctx);
	}
	
	public static void onPause(Context ctx) {
		MobclickAgent.onPause(ctx);
	}
	
	/**
	 * 意见反馈
	 * @param ctx
	 */
	public static void feedback(Context ctx){
		FeedbackAgent agent = new FeedbackAgent(ctx);
		agent.startFeedbackActivity();
	}
	
	
	public static void feedbackSyn(Context ctx){
		FeedbackAgent agent = new FeedbackAgent(ctx);
		agent.sync();
	}
	
	/**
	 * 检查更新
	 * @param ctx
	 */
	public static void update(Context ctx){
		//设置是否在wifi环境下更新
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		//设置全量更新
		UmengUpdateAgent.setDeltaUpdate(false);
		//设置强制更新
		UmengUpdateAgent.forceUpdate(ctx);
		//检测更新
		UmengUpdateAgent.update(ctx);
	}

	public static void update(final Activity object) {
		//设置是否在wifi环境下更新
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		//设置全量更新
		UmengUpdateAgent.setDeltaUpdate(false);
		//设置强制更新
		UmengUpdateAgent.forceUpdate(object);

		UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
			@Override
			public void onClick(int status) {
				switch (status) {
					case UpdateStatus.Update:
                        //记录弹出升级提示
                        eventById(object, R.string.updatepop);
                        //确定升级
                        eventById(object, R.string.updatepop_y);
						break;
					//退出APP
					default:
                        //记录弹出升级提示
                        eventById(object, R.string.updatepop);
                        //不升级
                        eventById(object, R.string.updatepop_n);
						object.finish();
						break;
				}
			}
		});

		//检测更新
		UmengUpdateAgent.update(object);
	}


	/**
	 * 统计事件
	 * @param ctx
	 * @param evnet
	 */
	public static void event(Context ctx,String evnet){
		MobclickAgent.onEvent(ctx, evnet);
	}
	
	public static void eventById(Context ctx,int id){
		MobclickAgent.onEvent(ctx, ctx.getString(id));
	}
	
}


