package com.bcb.data.util;

import android.media.Image;

import com.bcb.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by baicaibang on 2016/5/12.
 */
public class BankLogo {

    HashMap hashmap;

    public BankLogo() {
        hashmap = new HashMap();
        hashmap.put("CMB", R.drawable.bank_cmb);         //招商银行
        hashmap.put("ICBC", R.drawable.bank_icbc);         //中国工商银行
        hashmap.put("ABC", R.drawable.bank_abc);           //中国农业银行
        hashmap.put("CCB", R.drawable.bank_ccb);           //中国建设银行
        hashmap.put("BOC", R.drawable.bank_boc);          //中国银行
        hashmap.put("CIB", R.drawable.bank_cib);             //兴业银行
        hashmap.put("CITIC", R.drawable.bank_citic);       //中信银行
        hashmap.put("CEB", R.drawable.bank_ceb);           //光大银行
        hashmap.put("PAYH", R.drawable.bank_payh);      //平安银行
        hashmap.put("PSBC", R.drawable.bank_psbc);       //邮政储蓄银行
        hashmap.put("CMBC", R.drawable.bank_cmbc);    //民生银行
        hashmap.put("CGB", R.drawable.bank_cgb);          //广发银行
        hashmap.put("HXB", R.drawable.bank_hxb);          //华夏银行
        hashmap.put("SPDB", R.drawable.bank_spdb);      //浦发银行
    }

    public int getDrawableBankLogo(String key) {
        if (hashmap == null) {
            new BankLogo();
        }
        //如果不存在对应的logo，则返回空白图片
        if (hashmap.get(key) == null) {
            return (int)R.drawable.edittext_none_background;
        }
        return (int) hashmap.get(key);
    }

}
