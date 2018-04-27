package com.regan.util.encry;

import com.regan.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Message Digest Algorithm 5(信息摘要算法5)
 */
public class MD5Util {

	public static String str2MD5(String inStr) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(inStr.getBytes());
			return StringUtil.byteArrayToHexString(digest);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	public static String str2MD5(String inStr, String charsetname) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
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
		System.out.println(MD5Util.str2MD5("aa"));
	}
}
