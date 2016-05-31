package com.bcb.presentation.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.loan.LoanItemDetailBean;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UIUtil;
import com.bcb.presentation.adapter.GridAdapter;
import com.bcb.presentation.view.activity.Activity_Image_Display;
import com.bcb.presentation.view.activity.Activity_Recharge_Second;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by cain on 16/1/13.
 */
public class Frag_LoanDetail extends Frag_Base {


    private Activity context;
    private String loanUniqueId;
    //借款用途
    private TextView loanType;
    //借款期限
    private TextView loanDuration;
    //借款金额
    private TextView loanAmount;
    //借款利率
    private TextView loanRate;
    //分期还款
    private TextView loanRepayment;
    //逾期罚款
    private TextView loanPunitive;
    //下一个还款日
    private LinearLayout layoutNextPayDate;
    //下一个还款日
    private TextView loanNextDate;
    //还款金额
    private TextView loanNextPayment;

    //客服和邮箱
    private LinearLayout layoutOtherMessage;
    private LoanItemDetailBean loanItemDetailBean;

    //相机
    final int CAMERA_CLICK = 5;
    //从手机相册选择
    final int ALBUM_CLICK = 6;

    //上传图片
    private GridView noScrollgridview;
    private GridAdapter adapter;
    private ArrayList<String> pics;
    private AlertView alertView;

    private static ProgressDialog progressDialog;


    //立即还款
    private Button button_recharge;

    private BcbRequestQueue requestQueue;

    //构造函数
    public Frag_LoanDetail() {
        super();
    }

    @SuppressLint("ValidFragment")
    public Frag_LoanDetail(Activity context, String loanUniqueId) {
        super();
        this.context = context;
        this.loanUniqueId = loanUniqueId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_loan_detail, container, false);

        //上传图片
        noScrollgridview = (GridView) view.findViewById(R.id.noScrollgridview);

        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        pics = new ArrayList<>();
        adapter = new GridAdapter(context, pics);
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pics.size() == pos){
                    alertView = new AlertView("上传图片", null, "取消", null, new String[]{"拍照","从相册中选择"},
                            context, AlertView.Style.ActionSheet, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            switch (position){
                                case -1:
                                    alertView.dismiss();
                                    break;
                                case 0:
                                    selectPicture(CAMERA_CLICK);
                                    break;
                                case 1:
                                    selectPicture(ALBUM_CLICK);
                                    break;
                            }
                        }
                    });
                    alertView.show();
                }else{
                    Activity_Image_Display.launch(context, pos, pics);
                }
            }
        });

        return view;
    }

    //初始化页面要在这里进行，多线程情况下，在onCreateView中初始化会崩溃
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        requestQueue = BcbNetworkManager.newRequestQueue(context);
        setupView(view);
        loanLoanDetailData();
    }

    //初始化界面元素
    private void setupView(View view) {
        loanType = (TextView) view.findViewById(R.id.loan_type);
        loanDuration = (TextView) view.findViewById(R.id.loanduration);
        loanAmount = (TextView) view.findViewById(R.id.loan_amount);
        loanRate = (TextView) view.findViewById(R.id.loan_rate);
        loanRepayment = (TextView) view.findViewById(R.id.loan_repayment);
        loanPunitive = (TextView) view.findViewById(R.id.loan_punitive);
        //下一个还款日
        layoutNextPayDate = (LinearLayout) view.findViewById(R.id.layout_next_pay);
        loanNextDate = (TextView) view.findViewById(R.id.loan_next_date);
        loanNextPayment = (TextView) view.findViewById(R.id.loan_next_payment);

        //还款按钮
        button_recharge = (Button) view.findViewById(R.id.button_recharge);
        button_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转至充值按钮
                Activity_Recharge_Second.launche(context);
            }
        });
    }

    //获取借款详情数据
    private void loanLoanDetailData() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("LoanUniqueId", loanUniqueId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MyLoanItemDetailMessage, jsonObject, TokenUtil.getEncodeToken(context), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.d("1234", "LoanDetail = " + response.toString());
                try{
                    //如果存在返回数据时
                    if(PackageUtil.getRequestStatus(response, context)) {
                        JSONObject resultObject = PackageUtil.getResultObject(response);

                        //判断JSON对象是否为空
                        if (resultObject != null) {
                            loanItemDetailBean = App.mGson.fromJson(resultObject.toString(), LoanItemDetailBean.class);
                            setupDetailData();
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
        jsonRequest.setTag(BcbRequestTag.MyLoanItemDetailMessageTag);
        requestQueue.add(jsonRequest);
    }

    /**
     * 上传
     * @param path
     */
    private void uploadFile(final String path){

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("LoandId", loanUniqueId);
            jsonObject.put("FileData", convertBitmap2String(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Loan_Supplementary_Material, jsonObject, TokenUtil.getEncodeToken(context), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UIUtil.hideProgressBar();
                LogUtil.d("1234", "uploadFile = " + response.toString());
                try{
                    //如果存在返回数据时
                    if(PackageUtil.getRequestStatus(response, context)) {
                        pics.add(path);
                        adapter.notifyDataSetChanged();
                        ToastUtil.alert(context, "上传成功");
                    }
                } catch (Exception e) {
                    ToastUtil.alert(context, "上传失败，请稍后重试");
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                UIUtil.hideProgressBar();
                ToastUtil.alert(context, "上传失败，请稍后重试");
            }
        });
        jsonRequest.setTag(BcbRequestTag.MyLoanItemDetailMessageTag);
        requestQueue.add(jsonRequest);
    }

    //将Bitmap转成字符串类型
    private String convertBitmap2String(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (null != bitmap){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        }
        return "";
    }

    //设置界面数据
    private void setupDetailData() {
        loanType.setText(loanItemDetailBean.LoanType);
        loanDuration.setText(loanItemDetailBean.Duration);
        loanAmount.setText(String.format("%.0f", loanItemDetailBean.Amount) + "元");
        loanRate.setText(loanItemDetailBean.Rate);
        loanRepayment.setText(loanItemDetailBean.LoanPeriod +"期");
        loanPunitive.setText(loanItemDetailBean.LatePenaltyRate * 100 + "%");
        loanNextDate.setText(loanItemDetailBean.NextPayDate);
        loanNextPayment.setText(String.format("%.2f", loanItemDetailBean.NextPayAmount) + "元");
        //status 等于 15 时，表示正在还款中
        if (loanItemDetailBean.Status == 15) {
            layoutNextPayDate.setVisibility(View.VISIBLE);
            layoutOtherMessage.setVisibility(View.GONE);
        }
        //表示还款完成
        else if (loanItemDetailBean.Status == 20) {
            layoutNextPayDate.setVisibility(View.GONE);
            layoutOtherMessage.setVisibility(View.GONE);
        }
        //其余状态均表示审核中的状态
        else {
            layoutNextPayDate.setVisibility(View.GONE);
            layoutOtherMessage.setVisibility(View.VISIBLE);
        }
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //拍照
            case CAMERA_CLICK:
                if (data != null && resultCode == Activity.RESULT_OK) {
                    UIUtil.showProgressBar(context, "正在压缩上传...");
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    //缓存数据
                    String tempName = "bcb_" + new Date().getTime();
                    String path = savePhotoCache(tempName, bitmap);
                    uploadFile(path);
                }
                break;

            //从手机相册选择
            case ALBUM_CLICK:
                if (data != null && resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        String picturePath = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();
                        //压缩
                        Bitmap bitmap = imageZoom(BitmapFactory.decodeFile(picturePath));
                        //缓存数据
                        String tempName = "bcb_" + new Date().getTime();
                        String path = savePhotoCache(tempName, bitmap);
                        System.gc();
                        uploadFile(path);
//                        pics.add(picturePath);
//                        adapter.notifyDataSetChanged();
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
            ToastUtil.alert(context, "没有找到照片");
        }
    }

    //保存照片到本地
    public String savePhotoCache(final String resourceId, final Bitmap bitmap) {
        if(bitmap == null)
            return null;

        File imageDir = new File(context.getCacheDir(), "com.bcb/images");
        if (!imageDir.isDirectory())
            imageDir.mkdirs();

        File cachedImage = new File(imageDir, resourceId);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(cachedImage);
            Log.d("Bitmap图片的大小", "压缩前：" + bitmap.getByteCount() +"字节");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);
        } catch (IOException e) {
            Log.d("CACHED_IMAGE", "Exception writing cache image", e);
        } finally {
            if (output != null)
                try {
                    output.close();
                } catch (IOException e) {
                    // Ignored
                }
        }
        Log.d("Bitmap图片的大小", "压缩后：" + cachedImage.length() +"字节");
        return cachedImage.getPath();
    }

    //图片压缩
    private Bitmap imageZoom(Bitmap bitmap) {
        //图片允许最大空间   单位：KB
        double maxSize = 500.00;
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length/1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitmap = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i),
                    bitmap.getHeight() / Math.sqrt(i));
        }
        return bitmap;
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
}