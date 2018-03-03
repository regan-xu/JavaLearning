package com.regan.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Secure Hash Algorithm(安全散列算法)
 */
public class SHAUtil {

	public static String str2SHA(String inStr) {
		try {
//			MessageDigest md = MessageDigest.getInstance("SHA");
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(inStr.getBytes());
			return StringUtil.byteArrayToHexString(digest);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	public static String str2SHA(String inStr, String charsetname) {
		try {
//			MessageDigest md = MessageDigest.getInstance("SHA");
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(inStr.getBytes(charsetname));
			return StringUtil.byteArrayToHexString(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(SHAUtil.str2SHA("aa"));
	}
}
