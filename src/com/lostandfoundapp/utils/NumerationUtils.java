package com.lostandfoundapp.utils;
/**
 * 关于数字运算的工具类
 * @author lee
 *
 */
public class NumerationUtils {
	/**
	 * 获取随机数
	 */
	public static int random(){
		return (int) (Math.random()*9);
	}
	/**
	 * 设置数字为正确的显示格式
	 * @param m
	 * @return
	 */
	public static String setTime(int m){
		String s = "00";
		if (m<10) {
			s = "0"+m;
		}else {
			s = ""+m;
		}
		return s;
	}
}
