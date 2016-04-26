package com.bcb.presentation.view.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcb.R;
import com.bcb.presentation.presenter.IPresenter_JoinCompany;
import com.bcb.presentation.presenter.IPresenter_JoinCompanyImpl;
import com.bcb.presentation.view.activity_interface.Interface_Base;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.UmengUtil;

/**
 * Created by cain on 15/12/28.
 */
public class Activity_Join_Company extends Activity_Base implements Interface_Base, View.OnClickListener{
    private static final String TAG = "Activity_Join_Company";

    //缓存附件类型
    private int FileType = 0;

    //公司全称
    private EditText companyFullName;
    //公司简称
    private EditText companyShortName;
    //公司官网
    private EditText companyWebsite;

    //材料照片缩略图
    private RelativeLayout layout_material_image;
    private ImageView material_thumbnail;
    //选择职业身份认证材料文字描述
    private LinearLayout layout_material_description;
    //编辑图片
    private LinearLayout edit_image;

    //选择公司按钮
    private Button buttonSelectCompany;

    //转圈提示
    private ProgressDialog progressDialog;

    private IPresenter_JoinCompany iPresenterJoinCompany;

    public static void launche(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, Activity_Join_Company.class);
        ctx.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Join_Company.this);
        setBaseContentView(R.layout.activity_join_company);
        setTitleValue("申请认证");
        setLeftTitleListener(this);
        iPresenterJoinCompany = new IPresenter_JoinCompanyImpl(getBaseContext(), Activity_Join_Company.this);
        setupView();
    }

    //初始化界面
    private void setupView() {
        //公司全称
        companyFullName = (EditText) findViewById(R.id.company_fullname);
        //公司简称
        companyShortName = (EditText) findViewById(R.id.company_shortname);
        //公司官网
        companyWebsite = (EditText) findViewById(R.id.company_website);

        //认证材料缩略图
        layout_material_image = (RelativeLayout) findViewById(R.id.layout_material_image);
        material_thumbnail = (ImageView) findViewById(R.id.material_thumbnail);
        material_thumbnail.setOnClickListener(this);
        //选择身份认证材料描述
        layout_material_description = (LinearLayout) findViewById(R.id.layout_material_description);
        layout_material_description.setOnClickListener(this);
        //图片
        edit_image = (LinearLayout) findViewById(R.id.edit_image);
        edit_image.setOnClickListener(this);

        //选择公司按钮
        buttonSelectCompany = (Button) findViewById(R.id.button_submit);
        buttonSelectCompany.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.back_img:
                iPresenterJoinCompany.deletePhotoCache();
                finish();
                break;

            //选择身份证明描述文字
            case R.id.edit_image:
            case R.id.layout_material_description:
                gotoMaterialSelectPage();
                break;

            //点击缩略图进行放大
            case R.id.material_thumbnail:
                zoomPicture(((BitmapDrawable)material_thumbnail.getDrawable()).getBitmap());
                break;

            //提交认证
            case R.id.button_submit:
                UmengUtil.eventById(Activity_Join_Company.this, R.string.self_autu_y);
                submitButtonClick();
                break;
        }
    }

    //放大图片
    private void zoomPicture(Bitmap bitmap) {
        final Dialog confirmDialog = new Dialog(this);
        View view = View.inflate(this, R.layout.dialog_show_image, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.zoom_imageView);
        imageView.setImageBitmap(bitmap);

        Window win = confirmDialog.getWindow();
        confirmDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);

        confirmDialog.setContentView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog.dismiss();
            }
        });
        confirmDialog.show();
    }

    //选择职业身份证明
    private void gotoMaterialSelectPage() {
        //先回收ImageView的图片
        recycleImageView(material_thumbnail);
        layout_material_image.setVisibility(View.GONE);
        layout_material_description.setVisibility(View.VISIBLE);
        //跳转至页面进行选择照片
        Intent newIntent = new Intent(Activity_Join_Company.this, Activity_Select_Material.class);
        startActivityForResult(newIntent, 1);
    }

    //选择职业身份证明回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //选择职业身份证明返回
            case 1:
                //判断返回的值是否为空
                if (data != null && data.getBooleanExtra("takePictureResult", false)) {
                    //附件数据类型
                    FileType = data.getIntExtra("FileType", 0);
                    //获取Bitmap图片
                    material_thumbnail.setImageBitmap(iPresenterJoinCompany.getPhotoCache());
                    //调用gc建议回收垃圾
                    System.gc();
                    layout_material_image.setVisibility(View.VISIBLE);
                    layout_material_description.setVisibility(View.GONE);
                }
                break;

            default:
                break;
        }
    }

    //提交认证
    private void submitButtonClick() {
        //填写公司信息
        if (companyFullName.getText().toString().isEmpty() || companyShortName.getText().toString().isEmpty() || companyWebsite.getText().toString().isEmpty()) {
            ToastUtil.alert(Activity_Join_Company.this, "请填写完整的证明材料");
            return;
        }
        UmengUtil.eventById(Activity_Join_Company.this, R.string.self_auth_input);
        UmengUtil.eventById(Activity_Join_Company.this, R.string.self_autu_y);
        showProgressBar("正在提交证明材料，请耐心等候...");
        //请求加入公司
        iPresenterJoinCompany.onJoinCompany(companyFullName.getText().toString(), companyShortName.getText().toString(), companyWebsite.getText().toString(), FileType);
    }

    //回收ImageView占用的图片内存
    private void recycleImageView(View view) {
        //如果为空，则直接返回
        if (view == null) {
            return;
        }
        //判断是否为 ImageView并获取Bitmap图片进行回收
        if (view instanceof ImageView) {
            Drawable drawable = ((ImageView) view).getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    ((ImageView) view).setImageBitmap(null);
                    bitmap.recycle();
                    bitmap  = null;
                }
            }
        }
    }

    /********************* 转圈提示 **************************/
    //显示转圈提示
    private void showProgressBar(String title) {
        if(null == progressDialog) progressDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setMessage(title);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    //隐藏转圈提示
    private void hideProgressBar() {
        if(null != progressDialog && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    //点击系统返回键销毁页面
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //销毁缓存在本地的图片
        iPresenterJoinCompany.deletePhotoCache();
        iPresenterJoinCompany.clearDependency();
        iPresenterJoinCompany = null;
        finish();
    }

    /************************ 发送广播 *********************************/
    private void sendBroadCast() {
        Intent intent = new Intent();
        intent.setAction("com.bcb.update.company.joined");
        sendBroadcast(intent);
    }

    //请求结果回调
    @Override
    public void onRequestResult(int resultStatus, String message) {
        hideProgressBar();
        ToastUtil.alert(Activity_Join_Company.this, message);
        // 请求释放多余的内存
        System.gc();
        // 提交成功后，发送广播并关闭当前页面
        if (resultStatus == 1) {
            sendBroadCast();
            MyActivityManager.getInstance().finishAllActivity();
        }
        // token过期则跳转至登录页面
        else if (resultStatus == -5) {
            Activity_Login.launche(this);
        }
    }
}