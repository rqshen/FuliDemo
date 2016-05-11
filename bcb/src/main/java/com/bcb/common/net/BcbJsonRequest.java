package com.bcb.common.net;

import android.util.Base64;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.bcb.data.util.DESUtil;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;

/**
 * Created by cain on 16/4/15.
 */
public class BcbJsonRequest extends BcbRequest<JSONObject> {

    private String mEncodeToken;

    /**
     * 构造函数，默认为POST请求
     * @param url           请求接口
     * @param jsonObject    JSONObject对象
     * @param encodeToken   Token
     * @param callBack      请求回调
     */
    public BcbJsonRequest(String url, JSONObject jsonObject, String encodeToken, BcbCallBack<JSONObject> callBack) {
        this(Method.POST, url, jsonObject, encodeToken, callBack);
    }

    /**
     * 构造函数，默认为POST请求
     * @param url           请求接口
     * @param jsonObject    JSONObject对象
     * @param encodeToken   Token
     * @param index         位置参数
     * @param callBack      请求回调
     */
    public BcbJsonRequest(String url, JSONObject jsonObject, String encodeToken, int index, BcbIndexCallBack<JSONObject> callBack) {
        this(Method.POST, url, jsonObject, encodeToken, index, callBack);
    }

    /**
     * 构造函数
     * @param url           请求接口
     * @param jsonObject    JSONObject对象
     * @param encodeToken   Token
     * @param isAddDevInfo  是否添加设备信息
     * @param callBack      请求回调
     */
    public BcbJsonRequest(String url, JSONObject jsonObject, String encodeToken, boolean isAddDevInfo, BcbCallBack<JSONObject> callBack) {
        super(Method.POST, url, jsonObject == null ? "" : jsonObject.toString(), encodeToken, isAddDevInfo, callBack);
        mEncodeToken = encodeToken;
    }

    /**
     * 构造函数
     * @param method        请求方式，GET ？ POST 还是其他
     * @param url           请求接口
     * @param jsonObject    JSONObject对象
     * @param encodeToken   Token
     * @param callBack      请求回调
     */
    public BcbJsonRequest(int method, String url, JSONObject jsonObject, String encodeToken, BcbCallBack<JSONObject> callBack) {
        super(method, url, jsonObject == null ? "" : jsonObject.toString(), encodeToken, callBack);
        mEncodeToken = encodeToken;
    }

    /**
     * 构造函数
     * @param method        请求方式，GET ？ POST 还是其他
     * @param url           请求接口
     * @param jsonObject    JSONObject对象
     * @param encodeToken   Token
     * @param index         位置参数
     * @param callBack      请求回调
     */
    public BcbJsonRequest(int method, String url, JSONObject jsonObject, String encodeToken, int index, BcbIndexCallBack<JSONObject> callBack) {
        super(method, url, jsonObject == null ? "" : jsonObject.toString(), encodeToken, index, callBack);
        mEncodeToken = encodeToken;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {

            //返回不成功，则提示是否成功
            if (response.statusCode != 200) {
                LogUtil.d(getUrl(), "返回出错，状态码为：" + response.statusCode);
                return null;
            }

            //获取解密的key
            String key;
            if (null == mEncodeToken) {
                // 没有token参数，数据解密的key为默认的
                key = MyConstants.KEY;
            } else {
                key = DESUtil.decodeKey(mEncodeToken);
            }

            //解密数据
            String result = new String(response.data);
            byte[] baseDecodeResult = Base64.decode(result, Base64.DEFAULT);
            byte[] keybyte = key.getBytes();
            byte[] decodeByte_ECB = DESUtil.ees3DecodeECB(keybyte, baseDecodeResult);
            String decodeJsonString = new String(decodeByte_ECB, PROTOCOL_CHARSET);
            LogUtil.d("解密后的JSON串", decodeJsonString);
            //返回解密后的对象
            return Response.success(new JSONObject(decodeJsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        } catch (Exception ex) {
            return Response.error(new ParseError(ex));
        }
    }

}
