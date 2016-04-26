package com.bcb.presentation.view.custom.CustomDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bcb.R;

/**
 * Created by cain on 16/2/24.
 */
public class MaterialDialogView implements View.OnClickListener {

    final int VISIT_CARD = 1;
    final int INCUMBENY_VERIFICATION = 2;
    final int BADGE_CARD = 3;
    final int COMPANY_EMAIL = 4;

    private View view;
    private Context context;
    //背景图片
    private ImageView backgroundImageView;
    //拍照
    private LinearLayout layout_camera;
    //从手机相册选择
    private LinearLayout layout_album;
    //取消
    private LinearLayout layout_cancel;

    //监听器
    private MaterialDialogView.OnClickListener onClikListener;

    public static MaterialDialogView getInstance(Context context, int stepCode, OnClickListener onClikListener) {
        return  new MaterialDialogView(context, stepCode, onClikListener);
    }

    private MaterialDialogView(Context context, int stepCode, OnClickListener onClikListener) {
        getDecorView(context, stepCode, onClikListener);
    }


    private void getDecorView(Context context, int stepCode, OnClickListener onClikListener) {
        this.context = context;
        this.onClikListener = onClikListener;
        view = LayoutInflater.from(context).inflate(R.layout.material_dialog_view, null);
        //拍照
        layout_camera = (LinearLayout) view.findViewById(R.id.layout_camera);
        layout_camera.setOnClickListener(this);
        //从手机相册选择
        layout_album = (LinearLayout) view.findViewById(R.id.layout_album);
        layout_album.setOnClickListener(this);
        //取消
        layout_cancel = (LinearLayout) view.findViewById(R.id.layout_cancel);
        layout_cancel.setOnClickListener(this);

        //背景图片
        backgroundImageView = (ImageView) view.findViewById(R.id.background_imageView);

        switch (stepCode) {
            //名片
            case VISIT_CARD:
                backgroundImageView.setBackgroundResource(R.drawable.visit_dialog_image);
                break;
            //在职证明
            case INCUMBENY_VERIFICATION:
                backgroundImageView.setBackgroundResource(R.drawable.incembeny_verification_dialog_image);
                break;
            //工牌
            case BADGE_CARD:
                backgroundImageView.setBackgroundResource(R.drawable.badge_dialog_image);
                break;
            //企业邮箱
            case COMPANY_EMAIL:
                backgroundImageView.setBackgroundResource(R.drawable.company_email_dialog_image);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //拍照
            case R.id.layout_camera:
                onClikListener.onCameraClick();
                break;

            //从手机相册选择
            case R.id.layout_album:
                onClikListener.onAlbumClick();
                break;

            //取消
            case R.id.layout_cancel:
                onClikListener.onCancelClick();
                break;
        }
    }


    public interface OnClickListener {
        //相机
        void onCameraClick();
        //相册
        void onAlbumClick();
        //取消
        void onCancelClick();
    }

    public View getView() {
        return view;
    }
}
