package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.utils.MyActivityManager;
import com.bcb.utils.ToastUtil;
import com.bcb.presentation.view.custom.PagerIndicator.CirclePageIndicator;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Ray on 2016/5/31.
 *
 * @desc 图片浏览
 */
public class Activity_Image_Display extends Activity_Base implements ViewPager.OnPageChangeListener, View.OnClickListener{

    private Context context;
    private int curposition;
    private ArrayList<String> pics;

    private ImageView close;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private CirclePageIndicator circle_index;

    public static void launch(Context context, int pos, ArrayList<String> pics){
        Intent intent = new Intent(context, Activity_Image_Display.class);
        intent.putStringArrayListExtra("pics",pics);
        intent.putExtra("pos", pos);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        MyActivityManager.getInstance().pushOneActivity(Activity_Image_Display.this);
        context = this;
        initData();
    }

    private void initData() {
        curposition = getIntent().getIntExtra("pos", 0);
        pics = getIntent().getStringArrayListExtra("pics");
        if (null == pics || 0 == pics.size()){
            finish();
        }
        String uri = getUri(curposition);
        if (TextUtils.isEmpty(uri)){
            ToastUtil.alert(Activity_Image_Display.this, "图片路径错误");
        }
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPageMargin(20);
        viewPager.setCurrentItem(curposition);
        viewPager.addOnPageChangeListener(this);
        circle_index = (CirclePageIndicator) findViewById(R.id.circle_index);
        circle_index.setViewPager(viewPager);

        close = (ImageView) findViewById(R.id.close);
        close.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }

    private String getUri(int index){
        if (index >= 0 && null != pics && index < pics.size())
            return pics.get(index);
        else
            return null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                finish();
                break;
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {

        private ViewHoder viewHoder;

        public ViewPagerAdapter() {
            this.viewHoder = new ViewHoder();
        }

        @Override
        public int getCount() {
            return pics.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int pos) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_imageview, null);
            viewHoder.mImageView = (PhotoView) view.findViewById(R.id.imageView);
            Glide.with(context).load(getUri(pos)).centerCrop().into(viewHoder.mImageView);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        private class ViewHoder{
            public PhotoView mImageView;
        }
    }
}
