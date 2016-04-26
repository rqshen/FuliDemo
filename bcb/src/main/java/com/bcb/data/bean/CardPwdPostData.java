package com.bcb.data.bean;

/**
 * Created by cain on 15/12/23.
 */
public class CardPwdPostData {
    public String data;
    public String encryptkey;
    public String merchant_id;

//    public String bind_id;
//    public String member_id;
//    public String order_no;
//    public String terminal_type;
//    public String return_url;
//    public String notify_url;
//    public String version;
//    public String sign;

    public String changeToUrl(String url) {
//        String params = "merchant_id=" + merchant_id + "&bind_id=" + bind_id + "&member_id=" + member_id
//                + "&order_no=" + order_no + "&terminal_type=" + terminal_type + "&return_url=" + return_url
//                + "&notify_url=" + notify_url + "&version=" + version + "&sign=" + sign;
        String params = "data=" + data + "&encryptkey=" + encryptkey + "&merchant_id=" + merchant_id;
        return url + "?" + params;
    }
}
