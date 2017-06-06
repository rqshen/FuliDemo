package com.bcb.presentation.view.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.util.LogUtil;
import com.bcb.util.MyActivityManager;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.MaterialDialogView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by cain on 16/2/24.
 */
public class Activity_Select_Material extends Activity_Base implements View.OnClickListener {

    //用于缓存选择的附件类型
    private int FileType = 0;

    //名片
    final int VISIT_CARD = 1;
    //在职证明
    final int INCUMBENCY_CERIFICATION = 2;
    //工牌
    final int BADGE_CARD = 3;
    //企业邮箱
    final int COMPANY_EMAIL = 4;

    //相机
    final int CAMERA_CLICK = 5;
    //从手机相册选择
    final int ALBUM_CLICK = 6;

    //名片
    private LinearLayout layout_visit_card;
    //在职证明
    private LinearLayout layout_incumbency_cerification;
    //工牌
    private LinearLayout layout_badge_card;
    //企业邮箱
    private LinearLayout layout_company_email;

    //弹框
    private DialogWidget dialogWidget;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Select_Material.this);
        setBaseContentView(R.layout.activity_material_selected);
        setLeftTitleVisible(true);
        setTitleValue("选择认证材料");
        setupView();
    }

    //初始化界面
    private void setupView() {
        //名片
        layout_visit_card = (LinearLayout) findViewById(R.id.layout_visit_card);
        layout_visit_card.setOnClickListener(this);
        //在职证明
        layout_incumbency_cerification = (LinearLayout) findViewById(R.id.layout_incumbency_cerification);
        layout_incumbency_cerification.setOnClickListener(this);
        //工牌
        layout_badge_card = (LinearLayout) findViewById(R.id.layout_badge_card);
        layout_badge_card.setOnClickListener(this);
        //企业邮箱
        layout_company_email = (LinearLayout) findViewById(R.id.layout_company_email);
        layout_company_email.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //名片
            case R.id.layout_visit_card:
                showMaterialDialog(VISIT_CARD);
                FileType = VISIT_CARD;
                break;

            //在职证明
            case R.id.layout_incumbency_cerification:
                showMaterialDialog(INCUMBENCY_CERIFICATION);
                FileType = INCUMBENCY_CERIFICATION;
                break;

            //工牌
            case R.id.layout_badge_card:
                showMaterialDialog(BADGE_CARD);
                FileType = BADGE_CARD;
                break;

            //企业邮箱
            case R.id.layout_company_email:
                showMaterialDialog(COMPANY_EMAIL);
                FileType = COMPANY_EMAIL;
                break;
        }
    }


    /***************************** 弹框 *****************************/
    private void showMaterialDialog(int stepCode) {
        dialogWidget = new DialogWidget(Activity_Select_Material.this, getDecorViewDialog(stepCode));
        dialogWidget.getView().setPadding(0, 0, 0, 0);
        dialogWidget.show();
    }

    protected View getDecorViewDialog(int stepCode) {
        return MaterialDialogView.getInstance(Activity_Select_Material.this, stepCode, new MaterialDialogView.OnClickListener() {
            @Override
            public void onCameraClick() {
                selectPicture(CAMERA_CLICK);
            }

            @Override
            public void onAlbumClick() {
                selectPicture(ALBUM_CLICK);
            }

            @Override
            public void onCancelClick() {
                dialogWidget.dismiss();
                dialogWidget = null;
            }
        }).getView();
    }


    //选择照片
    private void selectPicture(int clickType) {
        //拍照
        if (clickType == CAMERA_CLICK) {
            startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), CAMERA_CLICK);
        }
        //从手机相册选择
        else if (clickType == ALBUM_CLICK) {
            doPickPhotoFromGallery();
        }
        dialogWidget.dismiss();
        dialogWidget = null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //拍照
            case CAMERA_CLICK:
                if (data != null && resultCode == RESULT_OK) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    //缓存数据
                    savePhotoCache("13dfwe54aew456zcs", bitmap);
                    //将结果发送出去
                    Intent intent=new Intent();
                    intent.putExtra("takePictureResult", true);
                    intent.putExtra("FileType", FileType);
                    setResult(1, intent);
                    //销毁当前页面
                    finish();
                }
                break;

            //从手机相册选择
            case ALBUM_CLICK:
                if (data != null && resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        Cursor cursor =getContentResolver().query(selectedImage, filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        String picturePath = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();
                        //压缩
                        Bitmap bitmap = getSmallBitmap(picturePath);
                        //缓存数据
                        savePhotoCache("13dfwe54aew456zcs", bitmap);
                        System.gc();
                        //将结果发送出去
                        Intent intent=new Intent();
                        intent.putExtra("takePictureResult", true);
                        intent.putExtra("FileType", FileType);
                        setResult(1, intent);
                        //销毁当前页面
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    //从手机相册中获取照片
    protected void doPickPhotoFromGallery() {
        try {
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_PICK);//Pick an item from the data
            intent.setType("image/*");//从所有图片中进行选择
            startActivityForResult(intent, ALBUM_CLICK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "没有找到照片", Toast.LENGTH_LONG).show();
        }
    }


    //保存照片到本地
    public void savePhotoCache(final String resourceId, final Bitmap bitmap) {
        if(bitmap == null)
            return;

        File imageDir = new File(this.getCacheDir(), "com.bcb/images");
        if (!imageDir.isDirectory())
            imageDir.mkdirs();

        File cachedImage = new File(imageDir, resourceId);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(cachedImage);
            LogUtil.d("Bitmap图片的大小", bitmap.getByteCount() +"字节");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);
        } catch (IOException e) {
            LogUtil.d("CACHED_IMAGE", "Exception writing cache image", e);
        } finally {
            if (output != null)
                try {
                    output.close();
                } catch (IOException e) {
                    // Ignored
                }
        }
    }

    /**
     * 根据路径获得图片并压缩返回bitmap用于显示
     *
     * @param filePath
     * @return
     */
    private Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}


