package com.regan.util;

public class StringUtil {

	private static final char HEX_DIGITS[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	/**
	 * 转换字节数组为十六进制字符串
	 */
	public static String byteArrayToHexString(byte data[]) {
		final int len = data.length;
		final char[] outStr = new char[len << 1];
		int n = 0;
		for(int i = 0; i < len; i++) {
			outStr[n++] = HEX_DIGITS[(0xF0 & data[i]) >>> 4];
			outStr[n++] = HEX_DIGITS[0x0F & data[i]];
		}
		return new String(outStr);
	}
	
	/**
	 * 转换十六进制字符串为字节数组
	 */
	public static byte[] hexString2Bytes(String hexstr) {
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			int f = (Character.digit(c0, 16) << 4) | Character.digit(c1, 16);
			b[i] = (byte) (f & 0xFF);
		}
		return b;
	}


}
