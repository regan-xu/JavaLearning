package com.regan.java.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Advanced Encryption Standard(高级加密标准)
 */
public class AESUtil {

	public final static String AES = "AES";
	public final static String KEY = "abcdefghigklmnop";//key需要为16位

    public static String encrypt(String data, String key){
    	try {
    		Cipher c = Cipher.getInstance(AES);
    		SecretKeySpec k =  new SecretKeySpec(key.getBytes(), AES);
    		c.init(Cipher.ENCRYPT_MODE, k);
    		byte[] encryptedData = c.doFinal(data.getBytes());
    		return StringUtil.byteArrayToHexString(encryptedData);
    		
//          return  new String(Base64.getEncoder().encode(encryptedData));//直接用base64来转
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String data, String key){
    	try {
    		Cipher c = Cipher.getInstance(AES);
    		SecretKeySpec k =  new SecretKeySpec(key.getBytes(), AES);
    		c.init(Cipher.DECRYPT_MODE, k);
    		byte[] decrypted = c.doFinal(StringUtil.hexString2Bytes(data));
//    		byte[] decrypted = c.doFinal(Base64.getDecoder().decode(data));////直接用base64来转
    		return new String(decrypted);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args){
        String password=encrypt("aaa",KEY);
        System.out.println(password);
        System.out.println(decrypt(password,KEY));
    }
}

