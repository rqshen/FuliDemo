package com.bcb.presentation.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.model.IModel_UserAccount;
import com.bcb.presentation.model.IModel_UserAccountImpl;
import com.bcb.presentation.view.activity_interface.Interface_Base;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by cain on 16/3/18.
 */
public class IPresenter_JoinCompanyImpl implements IPresenter_JoinCompany {

    private static final String addressName = "13dfwe54aew456zcs";
    //View
    private Interface_Base interfaceJoinCompany;
    //Model
    private IModel_UserAccount iModelUserAccount;

    private String companyShortName;

    private Context context;

    private BcbRequestQueue requestQueue;

    public IPresenter_JoinCompanyImpl(Context context, Interface_Base interfaceJoinCompany) {
        this.context = context;
        this.interfaceJoinCompany = interfaceJoinCompany;
        this.iModelUserAccount = new IModel_UserAccountImpl();
        requestQueue = App.getInstance().getRequestQueue();
    }

    @Override
    public void onJoinCompany(String companyFullName, String companyShortName, String companyWebsite, int fileType) {
        this.companyShortName = companyShortName;
        JSONObject obj = new JSONObject();
        try {
            obj.put("CompanyAllName", companyFullName);
            obj.put("CompanyProfile", companyShortName);
            obj.put("CompanyWebSite", companyWebsite);
            if (fileType > 0) {
                obj.put("FileType", fileType);
                obj.put("FileData", convertIconToString(getPhotoCache()));
            } else {
                interfaceJoinCompany.onRequestResult(-1, "请选择需要上传的身份证明材料");
                return;
            }
            submitMaterialRequest(obj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交证明材料
     */
    private void submitMaterialRequest(JSONObject object) {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.JoinCompany, object, TokenUtil.getEncodeToken(context), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if (response.getInt("status") == 1) {
                        interfaceJoinCompany.onRequestResult(response.getInt("status"), "您的材料已提交，预计2个工作日内完成审核");
                    } else {
                        interfaceJoinCompany.onRequestResult(response.getInt("status"), response.getString("message").isEmpty() ? "提交失败，请稍后再试" : response.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    interfaceJoinCompany.onRequestResult(-1, "解析数据出错");
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                interfaceJoinCompany.onRequestResult(-1, "网络异常，请稍后再试");
            }
        });
        jsonRequest.setTag(BcbRequestTag.BCB_JOIN_COMPANY_REQUEST);
        requestQueue.add(jsonRequest);
    }


    //删除暂存的图片
    @Override
    public void deletePhotoCache() {
        File imageDir = new File(context.getCacheDir(), "com.bcb/images");
        if (!imageDir.isDirectory()) {
            imageDir.mkdirs();
        }
        File imageFile = new File(imageDir, addressName);
        //如果存在，则删除Bitmap数据
        if (imageFile.isDirectory()) {
            String[] children = imageFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(imageFile, children[i]).delete();
            }
        }
        imageFile.delete();
    }

    //获取暂存的图片
    @Override
    public Bitmap getPhotoCache() {
        File imageDir = new File(context.getCacheDir(), "com.bcb/images");
        if (!imageDir.isDirectory()) {
            imageDir.mkdirs();
        }

        File imageFile = new File(imageDir, addressName);

        if (!imageFile.exists() || imageFile.length() == 0)
            return null;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        if (bitmap != null)
            return bitmap;
        else {
            imageFile.delete();
            return null;
        }
    }


    //将Bitmap转成字符串类型
    private String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    @Override
    public void clearDependency() {
        requestQueue.cancelAll(BcbRequestTag.BCB_JOIN_COMPANY_REQUEST);
        interfaceJoinCompany = null;
        iModelUserAccount = null;
        context = null;
    }
}
