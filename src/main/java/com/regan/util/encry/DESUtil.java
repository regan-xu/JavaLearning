package com.regan.util.encry;

import com.regan.util.StringUtil;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Data Encryptin Standard(数据加密标准)
 */
public class DESUtil {

	public final static String DES = "DES";
	public final static String KEY = "12345678";////要求key至少长度为8个字符
	
	public static String str2DES(String inStr, String key) {
		
		try {
			SecretKey secretKey = byte2Key(key.getBytes());
			SecureRandom secureRandom = new SecureRandom();
			Cipher cipher = Cipher.getInstance(DES);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey,secureRandom);
			byte[] data = cipher.doFinal(inStr.getBytes());
			return StringUtil.byteArrayToHexString(data);
//          return  new String(Base64.getEncoder().encode(data));//直接用base64来转
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String decrypt(String inStr, String key) {
		
		try {
			SecretKey secretKey = byte2Key(key.getBytes());
			SecureRandom secureRandom = new SecureRandom();
			Cipher cipher = Cipher.getInstance(DES);
			cipher.init(Cipher.DECRYPT_MODE, secretKey,secureRandom);
			byte[] data = cipher.doFinal(StringUtil.hexString2Bytes(inStr));
//    		byte[] data = c.doFinal(Base64.getDecoder().decode(inStr));////直接用base64来转
			return new String(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static SecretKey byte2Key(byte[] key) throws Exception{
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey secretKey = keyFactory.generateSecret(dks);
		return secretKey;
	}
	
	public static void main(String[] args) {
		String des = str2DES("aa", KEY);
		System.out.println(des);
		System.out.println(decrypt(des, KEY));
	}
}
