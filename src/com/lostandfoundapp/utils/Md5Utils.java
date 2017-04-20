package com.lostandfoundapp.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * MD5加密工具
 * 将字符串转化为字节，进行与运算达到加密
 * 解密使用该方法亦可
 * @author lee
 *
 */
public class Md5Utils {
	
	public static String md5Password(String password) {
		// 得到一个信息摘要器
		try {
		
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] result = digest.digest(password.getBytes());
			StringBuffer buffer = new StringBuffer();
			// 把每一个byte做一个与运算
			for (byte b : result) {
				// 与运算
				int number = b & 0xff;
				String str = Integer.toHexString(number);
				// System.out.println(str);
				if (str.length() == 1) {
					buffer.append("0");
				}
				buffer.append(str);
			}
			// 标识的md5加密后的结果
			return (buffer.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}
