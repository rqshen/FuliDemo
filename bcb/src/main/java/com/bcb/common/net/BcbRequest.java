package com.bcb.common.net;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.bcb.data.util.DESUtil;
import com.bcb.data.util.MyConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cain on 16/4/15.
 */
public abstract class BcbRequest<T> extends Request<T> {
    /** Default charset for JSON request. */
    protected static final String PROTOCOL_CHARSET = "utf-8";
    protected static final String CONTENT_TYPE = "application/json; charset=utf-8";
    //自定义监听器
    private  BcbCallBack<T> mBcbCallBack;
    private  BcbIndexCallBack<T> mBcbIndexCallBack;

    //Post请求的实体，如果为 null，则为GET请求，否则为POST请求
    private String mRequestBody;

    //已加密的token，没有为null
    private String mEncodeToken;

    private String key;
    //位置参数，默认为 -1，表示不使用位置参数回调
    private int index = -1;

    //是否添加设备信息
    private boolean isAddDevInfo = false;

    /**
     * 构造函数
     *
     * @param url           接口地址
     * @param requestBody   请求实体
     * @param encodeToken   已加密的token，没有就传null
     * @param bcbCallBack   请求结果回调
     */
    public BcbRequest(String url, String requestBody, String encodeToken, BcbCallBack<T> bcbCallBack) {
        this(requestBody == null ? Method.GET : Method.POST, url, requestBody, encodeToken, bcbCallBack);
    }

    /**
     * 构造函数
     *
     * @param method        请求方式
     * @param url           请求接口
     * @param requestBody   请求实体
     * @param encodeToken   已加密的token，没有就传null
     * @param bcbCallBack   请求结果回调
     */
    public BcbRequest(int method, String url, String requestBody, String encodeToken, final BcbCallBack bcbCallBack) {
        //将出错信息用接口保存起来
        super(method, url, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bcbCallBack.onErrorResponse(error);
            }
        });
        Log.d("url","url = " + url + " requestBody = " + requestBody);
        mBcbCallBack = bcbCallBack;
        if (TextUtils.isEmpty(requestBody)) {
            mRequestBody = null;
        } else  {
            mRequestBody = requestBody;
        }
        mEncodeToken = encodeToken;
    }

    /**
     * 构造函数
     *
     * @param method        请求方式
     * @param url           请求接口
     * @param requestBody   请求实体
     * @param encodeToken   已加密的token，没有就传null
     * @param isAddDevInfo  是否添加设备信息
     * @param bcbCallBack   请求结果回调
     */
    public BcbRequest(int method, String url, String requestBody, String encodeToken, boolean isAddDevInfo, final BcbCallBack bcbCallBack) {
        //将出错信息用接口保存起来
        super(method, url, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bcbCallBack.onErrorResponse(error);
                Log.d("url","error = " + error.toString());
            }
        });
        Log.d("url","url = " + url + " requestBody = " + requestBody);

        mBcbCallBack = bcbCallBack;
        if (TextUtils.isEmpty(requestBody)) {
            mRequestBody = null;
        } else  {
            mRequestBody = requestBody;
        }
        mEncodeToken = encodeToken;
        this.isAddDevInfo = isAddDevInfo;
    }

    /**
     * 构造函数
     * @param method    请求方式
     * @param url       请求接口
     * @param requestBody   请求实体，没有就传null
     * @param encodeToken   已加密的token, 没有就传null
     * @param index         位置参数，主要用于记录列表某个位置请求
     * @param bcbCallBack   回调
     */
    public BcbRequest(int method, String url, String requestBody, String encodeToken, int index, final BcbIndexCallBack bcbCallBack) {
        //将出错信息用接口保存起来
        super(method, url, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bcbCallBack.onErrorResponse(error);
            }
        });
        Log.d("url","url = " + url + " requestBody = " + requestBody);
        this.index = index;
        mBcbIndexCallBack = bcbCallBack;
        if (TextUtils.isEmpty(requestBody)) {
            mRequestBody = null;
        } else  {
            mRequestBody = requestBody;
        }
        mEncodeToken = encodeToken;
    }

    @Override
    protected void deliverResponse(T response) {
        //如果不存在位置参数，则使用默认回调，否则使用位置回调
        if (index == -1) {
            mBcbCallBack.onResponse(response);
        } else {
            mBcbIndexCallBack.onResponse(response, index);
        }
    }

    /**
     * 子类必须实现该功能，对返回的回调响应数据 response.data 进行解码转成相应类型的数据
     * @param response Response from the network
     * @return
     */
    @Override
    abstract protected Response<T> parseNetworkResponse(NetworkResponse response);

    /**
     * @deprecated Use {@link #getBodyContentType()}.
     */
    @Override
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    /**
     * @deprecated Use {@link #getBody()}.
     */
    @Override
    public byte[] getPostBody() {
        return getBody();
    }


    @Override
    public String getBodyContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mEncodeToken != null) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("access-token",mEncodeToken);
            if (isAddDevInfo){
                headers.put("version", android.os.Build.VERSION.RELEASE);
                headers.put("platform", "android");
            }
            return headers;
        } else {
            return super.getHeaders();
        }
    }

    /**
     * 重写请求实体
     * @return  如果存在请求的参数，则需要返回加了密的请求实体
     */
    @Override
    public byte[] getBody() {
        try {

            // 检测传入的密文token是否为空
            if (null == mEncodeToken) {
                // 没有token参数，数据解密的key为默认的
                key = MyConstants.KEY;
            } else {
                key = DESUtil.decodeKey(mEncodeToken);
            }
            // 如果传入参数不为空，则需要加密传入的数据
            if (null != mRequestBody) {
                byte[] data = mRequestBody.getBytes(PROTOCOL_CHARSET);
                byte[] encodeByte_ECB = DESUtil.des3EncodeECB(key.getBytes(), data);
                return Base64.encodeToString(encodeByte_ECB, Base64.DEFAULT).getBytes();
            }
        } catch (Exception e) {
            VolleyLog.wtf("Unsupported DES3Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
        }
        //返回为空
        return null;
    }

    public interface BcbCallBack<T> {
        void onResponse(T response);
        void onErrorResponse(Exception error);
    }

    public interface BcbIndexCallBack<T> {
        void onResponse(T response, int index);
        void onErrorResponse(Exception error);
    }
}
