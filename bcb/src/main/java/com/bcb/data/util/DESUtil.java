package com.bcb.data.util;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import android.util.Base64;

public class DESUtil {
	
	private static final String TAG = "DESUtil";
	
	public DESUtil() throws Exception {
		
	}

	public static byte[] des3EncodeECB(byte[] key, byte[] data) throws Exception {
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS7Padding");
		cipher.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] bOut = cipher.doFinal(data);
		return bOut;
	}

	public static byte[] ees3DecodeECB(byte[] key, byte[] data) throws Exception {

		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS7Padding");
		cipher.init(Cipher.DECRYPT_MODE, deskey);
		byte[] bOut = cipher.doFinal(data);
		return bOut;
	}
	
	/**
	 * 从加密了的token中获取key
	 * @param encodeToken
	 * @return
	 */
	public static String decodeKey(String encodeToken){
		try {
			byte[] baseDecodeResult = Base64.decode(encodeToken,Base64.DEFAULT);
			byte[] keybyte = MyConstants.KEY.getBytes();
			byte[] decodeByte_ECB = ees3DecodeECB(keybyte, baseDecodeResult);
			String decodeString_ECB = new String(decodeByte_ECB,"UTF-8");
			// 从已加密的token中解析key
			// 1.先解密token为铭文，再解析字符串
			// {usertoken}|{secutiryKey}
			if(null != decodeString_ECB && decodeString_ECB.contains("|")){
				
				return decodeString_ECB.substring(decodeString_ECB.indexOf("|")+1);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
			
	}
}