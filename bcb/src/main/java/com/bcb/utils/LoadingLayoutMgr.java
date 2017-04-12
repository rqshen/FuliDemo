package com.bcb.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;

public class LoadingLayoutMgr {
	Context ctx;
	String tips;
	View main;
	
	TextView tips_textView;
	
	RelativeLayout dialog_listview_header;
	Animation loadAnimation;
	ImageView loadImage;
	boolean loadFail;
	
	public LoadingLayoutMgr(Context ctx, boolean loadFail,String tips){
		this.ctx = ctx;
		this.tips = tips;
		this.loadFail = loadFail;
		
		init();
	}
	
	private void init(){
		main = View.inflate(ctx, R.layout.loading_data_progress, null);
		dialog_listview_header = (RelativeLayout) main.findViewById(R.id.header);
		
		tips_textView = (TextView) main.findViewById(R.id.tips);
		tips_textView.setText(tips);
		
		loadImage = (ImageView) main.findViewById(R.id.loading);
		if (!loadFail) {
			loadImage.setVisibility(View.VISIBLE);
			loadImage.setBackgroundResource(R.drawable.load_data_loading);
			loadAnimation = AnimationUtils.loadAnimation(ctx,R.anim.animation);
			loadImage.startAnimation(loadAnimation);
		}else {
			loadImage.setBackgroundResource(R.drawable.load_data_fail);
		}
		
	}
	
	public View getLayout(){		
		return main;
	}
}
